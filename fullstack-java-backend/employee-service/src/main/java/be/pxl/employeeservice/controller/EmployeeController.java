package be.pxl.employeeservice.controller;

import be.pxl.employeeservice.model.Employee;
import be.pxl.employeeservice.service.EmployeeService;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService _service;


    public EmployeeController(EmployeeService _service)
    {
        this._service=_service;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createNewEmployee(@RequestBody Employee employee) {
        return _service.add(employee);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Employee createNewEmployee(@PathVariable("id") long id) {
        return  _service.findById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public List<Employee> findAll() {
        return  _service.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/department/{departmentId}")
    public List<Employee> findByDepartment(@PathVariable("departmentId") long departmentId) {
        return  _service.findByDepartment(departmentId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/organization/{organizationId}")
    public List<Employee> findByOrganization(@PathVariable("organizationId") long organizationId) {
        return  _service.findByOrganization(organizationId);
    }
}
