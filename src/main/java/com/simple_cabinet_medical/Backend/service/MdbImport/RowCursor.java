package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A "one row lookahead" (peek) cursor on top of a forward-only JDBC
 * ResultSet. Needed to do a manual merge-join between several sorted
 * cursors (mal / cons / presc) without ever loading a whole table into
 * memory: only the current row of each stream is kept.
 *
 * Usage: as long as peek() matches the key currently being processed,
 * consume() and advance; as soon as the key changes, move to the next group.
 */
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

    /** Current row, without advancing. Null if the stream is exhausted. */
    public T peek() {
        return buffered;
    }

    /** Returns the current row and advances to the next one. */
    public T consume() throws SQLException {
        T value = buffered;
        advance();
        return value;
    }

    public boolean isExhausted() {
        return exhausted;
    }
}