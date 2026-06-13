package com.vitalpet.msusers.service;

import com.vitalpet.msusers.dto.UserRequestDTO;
import com.vitalpet.msusers.dto.UserResponseDTO;
import com.vitalpet.msusers.exception.ResourceNotFoundException;
import com.vitalpet.msusers.model.Role;
import com.vitalpet.msusers.model.User;
import com.vitalpet.msusers.repository.RoleRepository;
import com.vitalpet.msusers.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private static final List<String> PLATAFORM_ROLES = List.of("ADMIN","VET","CLIENT","RECEPCIONIST");

    private Faker faker;
    private User mockUser;
    private Role mockRole;

    @BeforeEach
    void setUp(){

        faker = new Faker();

        String randomRoleName = faker.options().nextElement(PLATAFORM_ROLES);

        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName(randomRoleName);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName(faker.name().firstName());
        mockUser.setLastName(faker.name().lastName());
        mockUser.setEmail(faker.internet().emailAddress());
        mockUser.setPhoneNumber(faker.phoneNumber().phoneNumber());
        mockUser.setAddress(faker.address().streetAddress());
        mockUser.setActive(true);
        mockUser.setRole(mockRole);
    }

    //1. GetALL Users
    @Test
    void getALl_ShouldReturnListOfUserDTO(){
        when(userRepository.findByActiveTrue()).thenReturn(List.of(mockUser));

        List<UserResponseDTO> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(mockUser.getEmail(), result.get(0).getEmail());
        assertEquals(mockRole.getName(), result.get(0).getRoleName());

        verify(userRepository,times(1)).findByActiveTrue();
    }

    //2. Create funcionando.
    @Test
    void create_ShouldReturnUserDTO_WhenDataIsCorrect(){
        UserRequestDTO requestDTO = new UserRequestDTO(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),
                mockRole.getName()
        );

        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName(requestDTO.getRoleName())).thenReturn(Optional.of(mockRole));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO result = userService.create(requestDTO);

        assertNotNull(result);
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockRole.getName(), result.getRoleName());

        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(roleRepository, times(1)).findByName(anyString());
        verify(userRepository,times(1)).save(any(User.class));
    }

    //3. Create no funcionando, IllegalArgumentException
    @Test
    void create_ShouldIllegalArgumentException_WhenEmailAlreadyExists(){
        UserRequestDTO requestDTO = new UserRequestDTO(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),
                mockRole.getName()
        );

        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> userService.create(requestDTO));

        assertEquals("El email ya está registrado", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    //4. Create no funcionando , ResourceNotFoundException
    @Test
    void create_ShouldThrowResourceNotFoundException_WhenRoleDoesNotExists(){
        UserRequestDTO requestDTO = new UserRequestDTO(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),
                "Rol Fantasma"
        );

        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName(requestDTO.getRoleName())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.create(requestDTO);
        });

        assertEquals("Rol no encontrado" + requestDTO.getRoleName(), exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    //5. ExistsById funcionando
    @Test
    void UserExistsById_ShouldReturnUserDTO_WhenUserExists(){
        Long validId = 1L;
        when(userRepository.existsById(validId)).thenReturn(true);
        boolean exists = userService.UserExistsById(validId);
        assertTrue(exists);
        verify(userRepository,times(1)).existsById(1L);
    }

    //6.GetById Funcionando
    @Test
    void getById_ShouldReturnUserDTO_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        UserResponseDTO result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    //7.GetById no funcionando.
    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }

    //8.Update
    @Test
    void update_ShouldReturnUpdatedUserDTO_WhenDataIsCorrect() {
        Long validId = 1L;
        UserRequestDTO requestDTO = new UserRequestDTO(
                "NombreNuevo",
                "ApellidoNuevo",
                mockUser.getEmail(),
                "+569999",
                "Direccion Nueva",
                mockRole.getName()
        );

        when(userRepository.findById(validId)).thenReturn(Optional.of(mockUser));
        when(roleRepository.findByName(requestDTO.getRoleName())).thenReturn(Optional.of(mockRole));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO result = userService.update(validId, requestDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    //9. Desactivate
    @Test
    void deactivate_ShouldSetActiveToFalse_WhenUserExists() {
        Long validId = 1L;
        when(userRepository.findById(validId)).thenReturn(Optional.of(mockUser));

        userService.deactivate(validId);

        assertFalse(mockUser.getActive());
        verify(userRepository, times(1)).save(mockUser);
    }

    //10.GetUsersByRole
    @Test
    void getUsersByRol_ShouldReturnListOfUsers() {
        String rol = "ADMIN";
        when(userRepository.findByRoleName(rol)).thenReturn(List.of(mockUser));
        List<UserResponseDTO> result = userService.getUsersByRol(rol);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByRoleName(rol);
    }

    //11.IsClient
    @Test
    void isClient_ShouldReturnTrue_WhenUserIsClient() {
        mockRole.setName("CLIENT");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        assertTrue(userService.isClient(1L));
    }

    //12. IsVet
    @Test
    void isVet_ShouldReturnTrue_WhenUserIsVet() {
        mockRole.setName("VET");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        assertTrue(userService.isVet(1L));
    }

    //13.GetEmailById funcionando
    @Test
    void getEmailById_ShouldReturnEmail_WhenUserExists() {
        when(userRepository.findEmailById(1L)).thenReturn(Optional.of("test@gmail.com"));
        String email = userService.getEmailById(1L);
        assertEquals("test@gmail.com", email);
    }

    //14.GetEmailById no funcionando.
    @Test
    void getEmailById_ShouldThrowResourceNotFoundException_WhenEmailDoesNotExist() {
        when(userRepository.findEmailById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getEmailById(99L));
    }

}
