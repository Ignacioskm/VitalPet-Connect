package com.vitalpet.msauth.service;

import com.vitalpet.msauth.client.UserClient;
import com.vitalpet.msauth.dto.AuthLoginDTO;
import com.vitalpet.msauth.dto.AuthRegisterDTO;
import com.vitalpet.msauth.dto.UserRequestDTO;
import com.vitalpet.msauth.dto.UserResponseDTO;
import com.vitalpet.msauth.exception.InvalidCredentialsException;
import com.vitalpet.msauth.model.Credential;
import com.vitalpet.msauth.repository.CredentialRepository;
import com.vitalpet.msauth.security.JwtProvider;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private CredentialRepository credentialRepository;
    @Mock private UserClient userClient;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;

    @InjectMocks private AuthService authService;

    private Faker faker;
    private AuthRegisterDTO registerDTO;
    private AuthLoginDTO loginDTO;
    private Credential mockCredential;
    private UserResponseDTO mockUserResponse;

    @BeforeEach
    void setUp() {
        faker = new Faker();
        String rawPassword = "MySecretPassword123";
        String encodedPassword = "$2a$10$hashedpasswordfake"; // Simulamos un hash de BCrypt
        String email = faker.internet().emailAddress();

        // Preparamos el DTO de registro
        registerDTO = new AuthRegisterDTO(
                faker.name().firstName(),
                faker.name().lastName(),
                email,
                faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),
                "CLIENT",
                rawPassword
        );
        loginDTO = new AuthLoginDTO(email, rawPassword);

        // Preparamos la respuesta falsa del Feign Client (ms-users)
        mockUserResponse = new UserResponseDTO();
        mockUserResponse.setId(1L); // ID que devuelve ms-users
        mockUserResponse.setRoleName("CLIENT");

        // Preparamos la credencial simulada de la base de datos
        mockCredential = new Credential();
        mockCredential.setId(100L);
        mockCredential.setUserId(1L); // Relacionado al ID de ms-users
        mockCredential.setEmail(email);
        mockCredential.setPasswordHash(encodedPassword);
    }

    //1. Test Register funcionando
    @Test
    void register_ShouldReturnSuccessMessage_WhenDataIsCorrect() {
        when(credentialRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
        // Simulamos la respuesta de ms-users
        when(userClient.createUser(any(UserRequestDTO.class))).thenReturn(mockUserResponse);
        // Simulamos la encriptación de la contraseña
        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("hashed_password");
        // Simulamos el guardado en BD
        when(credentialRepository.save(any(Credential.class))).thenReturn(mockCredential);

        String result = authService.register(registerDTO);

        assertEquals("Usuario registrado exitosamente", result);

        verify(credentialRepository, times(1)).existsByEmail(anyString());
        verify(userClient, times(1)).createUser(any(UserRequestDTO.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(credentialRepository, times(1)).save(any(Credential.class));
    }

    //2. TestRegister no funcionando.
    @Test
    void register_ShouldThrowIllegalArgumentException_WhenEmailExists() {
        when(credentialRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerDTO);
        });

        assertEquals("El email ya esta registrado", exception.getMessage());

        verify(userClient, never()).createUser(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(credentialRepository, never()).save(any());
    }

    //3. Login Funcionando
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        String fakeJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fake_token";
        when(credentialRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockCredential));
        // Simulamos que el passwordEncoder dice: "Sí, la contraseña cruda coincide con el hash"
        when(passwordEncoder.matches(loginDTO.getPassword(), mockCredential.getPasswordHash())).thenReturn(true);
        // Simulamos la búsqueda del rol en ms-users
        when(userClient.getUserById(mockCredential.getId())).thenReturn(mockUserResponse);
        // Simulamos la creación del token
        when(jwtProvider.generateToken(mockCredential.getUserId(), mockUserResponse.getRoleName())).thenReturn(fakeJwtToken);

        String token = authService.login(loginDTO);

        assertNotNull(token);
        assertEquals(fakeJwtToken, token);

        verify(credentialRepository, times(1)).save(mockCredential);
        verify(jwtProvider, times(1)).generateToken(anyLong(), anyString());
    }

    //4. Login Email not found
    @Test
    void login_ShouldThrowInvalidCredentialsException_WhenEmailNotFound() {
        when(credentialRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("Credenciales invalidas", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userClient, never()).getUserById(anyLong());
    }

    //5.Login Password is incorrect
    @Test
    void login_ShouldThrowInvalidCredentialsException_WhenPasswordIsIncorrect() {
        when(credentialRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockCredential));
        // Simulamos que el validador de contraseñas diga: "No coinciden"
        when(passwordEncoder.matches(loginDTO.getPassword(), mockCredential.getPasswordHash())).thenReturn(false);

        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals("Credenciales Inválidas", exception.getMessage());

        verify(userClient, never()).getUserById(anyLong());
        verify(jwtProvider, never()).generateToken(anyLong(), anyString());
    }

}