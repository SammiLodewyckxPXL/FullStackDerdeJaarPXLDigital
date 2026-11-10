package be.pxl.organizationservice.controller;

import be.pxl.organizationservice.model.Organization;
import be.pxl.organizationservice.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Organization findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/{id}/with-departments")
    public Map<String, Object> findWithDepartments(@PathVariable Long id) {
        return Map.of(
                "organization", service.findById(id),
                "departments", List.of()
        );
    }

    @GetMapping("/{id}/with-employees")
    public Map<String, Object> findWithEmployees(@PathVariable Long id) {
        return Map.of(
                "organization", service.findById(id),
                "employees", List.of()
        );
    }

    @GetMapping("/{id}/with-departments-and-employees")
    public Map<String, Object> findWithDepartmentsAndEmployees(
            @PathVariable Long id) {

        return Map.of(
                "organization", service.findById(id),
                "departments", List.of(),
                "employees", List.of()
        );
    }
}