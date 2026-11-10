package be.pxl.departmentservice.service;

import be.pxl.departmentservice.model.Department;
import be.pxl.departmentservice.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public Department add(Department department) {
        return repository.save(department);
    }

    public Department findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public List<Department> findAll() {
        return repository.findAll();
    }

    public List<Department> findByOrganization(Long organizationId) {
        return repository.findAllByOrganizationId(organizationId);
    }
}