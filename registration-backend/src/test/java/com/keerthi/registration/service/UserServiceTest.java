package com.keerthi.registration.service;

import com.keerthi.registration.dto.RegisterRequest;
import com.keerthi.registration.entity.User;
import com.keerthi.registration.exception.EmailAlreadyExistsException;
import com.keerthi.registration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    private RegisterRequest createValidRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("9876543210");
        request.setPassword("Password@123");
        request.setConfirmPassword("Password@123");
        return request;
    }

    @Test
    @DisplayName("Should successfully register a new user when details are valid")
    void testRegisterUser_Success() {
        RegisterRequest request = createValidRequest();

        when(userRepository.existsByEmailIgnoreCase("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("$2a$10$hashedPassword123");

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        assertEquals("9876543210", savedUser.getPhone());
        assertEquals("$2a$10$hashedPassword123", savedUser.getPassword());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when password and confirmPassword do not match")
    void testRegisterUser_PasswordMismatch_ThrowsException() {
        RegisterRequest request = createValidRequest();
        request.setConfirmPassword("Mismatch@123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("Passwords do not match", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already registered")
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = createValidRequest();

        when(userRepository.existsByEmailIgnoreCase("john.doe@example.com")).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("Email is already registered", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}
