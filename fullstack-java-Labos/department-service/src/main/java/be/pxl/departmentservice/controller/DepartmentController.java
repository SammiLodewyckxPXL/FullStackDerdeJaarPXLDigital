package be.pxl.departmentservice.controller;

import be.pxl.departmentservice.model.Department;
import be.pxl.departmentservice.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping
    public Department add(@Valid @RequestBody Department department) {
        return service.add(department);
    }

    @GetMapping("/{id}")
    public Department findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<Department> findAll() {
        return service.findAll();
    }

    @GetMapping("/organization/{organizationId}")
    public List<Department> findByOrganization(
            @PathVariable Long organizationId) {
        return service.findByOrganization(organizationId);
    }

    @GetMapping("/organization/{organizationId}/with-employees")
    public Map<String, Object> findByOrganizationWithEmployees(
            @PathVariable Long organizationId) {

        return Map.of(
                "departments", service.findByOrganization(organizationId),
                "employees", List.of()
        );
    }
}