package com.simple_cabinet_medical.Backend.dto.audit;

import java.util.List;

public record AuditFilterOptions(
        List<ClientOption> clients,
        List<UserOption> users,
        List<String> entityTypes
) {
    public record ClientOption(Long id, String name) {
    }

    public record UserOption(Long id, String username, Long clientId) {
    }
}