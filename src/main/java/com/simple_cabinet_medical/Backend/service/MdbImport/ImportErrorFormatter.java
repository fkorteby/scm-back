package com.simple_cabinet_medical.Backend.service.MdbImport;

/**
 * JPA/Hibernate exceptions often arrive wrapped several times
 * (TransactionSystemException -> RollbackException -> ConstraintViolationException...).
 * This class walks down the cause chain and returns the deepest, most
 * meaningful message, so that result.errors stays readable even with
 * thousands of failing rows.
 */
public final class ImportErrorFormatter {

    private ImportErrorFormatter() {}

    public static String describe(Throwable e) {
        Throwable current = e;
        String lastMeaningful = e.getClass().getSimpleName();
        int guard = 0;

        while (current != null && guard++ < 8) {
            String msg = current.getMessage();
            if (msg != null && !msg.isBlank()) {
                lastMeaningful = current.getClass().getSimpleName() + " : " + msg;
            }
            current = current.getCause();
        }
        return lastMeaningful;
    }
}