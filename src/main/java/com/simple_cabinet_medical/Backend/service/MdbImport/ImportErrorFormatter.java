package com.simple_cabinet_medical.Backend.service.MdbImport;

/**
 * Les exceptions JPA/Hibernate arrivent souvent emballées plusieurs fois
 * (TransactionSystemException -> RollbackException -> ConstraintViolationException...).
 * Cette classe descend la chaîne de causes et retourne le message le plus
 * profond et le plus parlant, pour que result.errors reste lisible même
 * avec des milliers de lignes en erreur.
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