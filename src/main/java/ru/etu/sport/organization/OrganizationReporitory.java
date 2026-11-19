package ru.etu.sport.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.etu.sport.organization.model.Organization;

public interface OrganizationReporitory extends JpaRepository<Organization, Long> {   
}
