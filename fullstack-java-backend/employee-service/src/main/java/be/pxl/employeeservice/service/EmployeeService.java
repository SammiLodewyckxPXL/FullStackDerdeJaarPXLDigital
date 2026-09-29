package be.pxl.employeeservice.service;

import be.pxl.employeeservice.model.Employee;
import be.pxl.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository _repository;

    public EmployeeService(EmployeeRepository repository) {
        _repository = repository;
    }

    public Employee add(Employee employee) {
//        Employee employee = new Employee(firstName, lastName, email, departmentId, organizationId);
        _repository.save(employee);
        return employee;
    }

    public Employee findById(long id) {
        return _repository.findById(id)
                .orElseThrow(() -> new RuntimeException("employee not found"));
    }

    public List<Employee> findAll() {
        return _repository.findAll();
    }

    public List<Employee> findByDepartment(long departmentId) {
        return _repository.findAllByDepartmentId(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

    }

    public List<Employee> findByOrganization(long organizationId) {
        return _repository.findAllByOrganizationId(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

    }
}
