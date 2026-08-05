package com.simple_cabinet_medical.Backend.service.MdbImport;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Curseur "avec un coup d'avance" (peek) au-dessus d'un ResultSet JDBC (forward-only).
 * Nécessaire pour faire un merge-join manuel entre plusieurs curseurs triés
 * (mal / cons / presc) sans jamais charger une table entière en mémoire :
 * on ne garde que la ligne courante de chaque flux.
 *
 * Usage : tant que peek() correspond à la clé qu'on traite, on consume() et
 * on avance ; dès que la clé change, on passe au groupe suivant.
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

    /** Ligne courante, sans avancer. Null si le flux est épuisé. */
    public T peek() {
        return buffered;
    }

    /** Retourne la ligne courante et avance au suivant. */
    public T consume() throws SQLException {
        T value = buffered;
        advance();
        return value;
    }

    public boolean isExhausted() {
        return exhausted;
    }
}