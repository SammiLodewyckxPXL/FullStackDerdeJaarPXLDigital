package be.pxl.employeeservice.repository;

import be.pxl.employeeservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findById(Long id);
    List<Employee> findAll();

    Optional<List<Employee>> findAllByDepartmentId(Long departmentId);

    Optional<List<Employee>> findAllByOrganizationId(Long organizationId);
}
