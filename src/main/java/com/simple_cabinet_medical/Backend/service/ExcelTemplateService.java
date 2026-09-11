package com.simple_cabinet_medical.Backend.service;

import com.simple_cabinet_medical.Backend.model.ParametreDefinition;
import com.simple_cabinet_medical.Backend.repository.DureeRepository;
import com.simple_cabinet_medical.Backend.repository.FormeRepository;
import com.simple_cabinet_medical.Backend.repository.ParametreDefinitionRepository;
import com.simple_cabinet_medical.Backend.repository.PosologieRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

@Service
public class ExcelTemplateService {

    private final ParametreDefinitionRepository repository;
    private final EntityManager entityManager;
    @Autowired
    private FormeRepository formeRepository;

    @Autowired
    private DureeRepository dureeRepository;

    @Autowired
    private PosologieRepository posologieRepository;


    public ExcelTemplateService(ParametreDefinitionRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    public byte[] generateTemplate(String type) {
        List<ParametreDefinition> columns = repository.findByType(type);

        if (columns.isEmpty()) {
            throw new IllegalArgumentException("No columns defined for type: " + type);
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(type + "_Template");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            int colIndex = 0;
            for (ParametreDefinition col : columns) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(col.getColumnLabel());
                cell.setCellStyle(headerStyle);
            }

            // Auto-size columns
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel template", e);
        }
    }

    @Transactional
    public void importExcelData(String type, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Load column definitions from DB
            List<ParametreDefinition> columns = repository.findByType(type);
            if (columns.isEmpty()) {
                throw new RuntimeException("No parameter definitions found for type: " + type);
            }

            // Validate header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel file is empty or missing header row!");
            }

            int numCols = headerRow.getPhysicalNumberOfCells();
            if (numCols != columns.size()) {
                throw new RuntimeException("Column count mismatch! Expected " + columns.size() + " but found " + numCols);
            }

            // Check column names match DB
            for (int i = 0; i < numCols; i++) {
                String excelColName = headerRow.getCell(i).getStringCellValue().trim();
                String dbColLabel = columns.get(i).getColumnLabel().trim();
                if (!excelColName.equalsIgnoreCase(dbColLabel)) {
                    throw new RuntimeException("Column mismatch at position " + (i + 1) +
                            ". Expected: '" + dbColLabel + "', Found: '" + excelColName + "'");
                }
            }

            // Load entity class dynamically
            String className = "com.simple_cabinet_medical.Backend.model." + type;
            Class<?> entityClass = Class.forName(className);

            // Iterate over rows (skip header)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (dataRow == null) continue;

                Object entity = entityClass.getDeclaredConstructor().newInstance();

                for (int j = 0; j < numCols; j++) {
                    Cell cell = dataRow.getCell(j);
                    String fieldName = columns.get(j).getColumnName();

                    Field field = entityClass.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    if (cell != null) {
                        String cellValue = switch (cell.getCellType()) {
                            case STRING -> cell.getStringCellValue();
                            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                            default -> null;
                        };

                        if (cellValue != null) {
                            // ✅ Plus de lookup — on set directement la valeur texte
                            if (field.getType().equals(Long.class)) {
                                field.set(entity, Long.parseLong(cellValue));
                            } else if (field.getType().equals(Double.class)) {
                                field.set(entity, Double.parseDouble(cellValue));
                            } else {
                                field.set(entity, cellValue);  // String directement
                            }
                        }
                    }
                }
                entityManager.persist(entity);
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to import Excel for type " + type + ": " + e.getMessage(), e);
        }
    }


}
