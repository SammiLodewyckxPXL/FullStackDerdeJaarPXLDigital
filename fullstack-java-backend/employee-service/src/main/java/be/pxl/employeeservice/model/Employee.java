package be.pxl.employeeservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Long departmentId;

    @NotNull
    private Long organizationId;

    public  Employee(){}

    public  Employee(String firstName, String lastName, String email, long departmentId, long organizationId)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.departmentId = departmentId;
        this.organizationId=organizationId;
    }
}
