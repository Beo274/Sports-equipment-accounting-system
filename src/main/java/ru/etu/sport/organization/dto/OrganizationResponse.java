package ru.etu.sport.organization.dto;

import ru.etu.sport.organization.model.Organization;

public record OrganizationResponse(Long id, String name) {
    public static OrganizationResponse fromEntity(Organization e) {
        return new OrganizationResponse(e.getId(), e.getName());
    }
}
