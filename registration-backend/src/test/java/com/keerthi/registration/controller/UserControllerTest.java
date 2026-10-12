package com.keerthi.registration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keerthi.registration.dto.RegisterRequest;
import com.keerthi.registration.exception.EmailAlreadyExistsException;
import com.keerthi.registration.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private RegisterRequest createValidRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice Smith");
        request.setEmail("alice@example.com");
        request.setPhone("9876543210");
        request.setPassword("SecurePass123");
        request.setConfirmPassword("SecurePass123");
        return request;
    }

    @Test
    @DisplayName("GET /api/users/health returns HTTP 200 OK")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Backend is running"));
    }

    @Test
    @DisplayName("POST /api/users/register returns HTTP 201 when payload is valid")
    void testRegister_ValidPayload_Returns201() throws Exception {
        RegisterRequest request = createValidRequest();
        doNothing().when(userService).registerUser(any(RegisterRequest.class));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    @DisplayName("POST /api/users/register returns HTTP 400 when email is invalid")
    void testRegister_InvalidEmail_Returns400() throws Exception {
        RegisterRequest request = createValidRequest();
        request.setEmail("not-an-email");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /api/users/register returns HTTP 400 when password lacks complexity")
    void testRegister_WeakPassword_Returns400() throws Exception {
        RegisterRequest request = createValidRequest();
        request.setPassword("alllowercase");
        request.setConfirmPassword("alllowercase");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("POST /api/users/register returns HTTP 409 when email already exists")
    void testRegister_DuplicateEmail_Returns409() throws Exception {
        RegisterRequest request = createValidRequest();
        doThrow(new EmailAlreadyExistsException("Email is already registered"))
                .when(userService).registerUser(any(RegisterRequest.class));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }
}
