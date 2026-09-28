package be.pxl.organizationservice.repository;

import be.pxl.organizationservice.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}