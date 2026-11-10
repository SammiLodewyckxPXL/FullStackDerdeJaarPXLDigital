package be.pxl.employeeservice;

import be.pxl.employeeservice.controller.EmployeeController;
import be.pxl.employeeservice.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    EmployeeService employeeService;
    /*@Test
    void addEmployee_returns201() throws Exception {
        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jasper\"}"))
                .andExpect(status().isCreated());
    }*/
}

