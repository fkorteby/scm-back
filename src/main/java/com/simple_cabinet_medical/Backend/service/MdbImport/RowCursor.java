package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowCursor<T> {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    private final ResultSet rs;
    private final RowMapper<T> mapper;
    private T buffered;
    private boolean exhausted = false;

    public RowCursor(ResultSet rs, RowMapper<T> mapper) throws SQLException {
        this.rs = rs;
        this.mapper = mapper;
        advance();
    }

    private void advance() throws SQLException {
        if (rs.next()) {
            buffered = mapper.map(rs);
        } else {
            buffered = null;
            exhausted = true;
        }
    }

     public T peek() {
        return buffered;
    }

     public T consume() throws SQLException {
        T value = buffered;
        advance();
        return value;
    }

    public boolean isExhausted() {
        return exhausted;
    }
}