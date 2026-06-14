package com.vitalpet.msbranches.service;

import com.vitalpet.msbranches.dto.BranchesRequestDTO;
import com.vitalpet.msbranches.dto.BranchesResponseDTO;
import com.vitalpet.msbranches.dto.CityResponseDTO;
import com.vitalpet.msbranches.exception.ResourceNotFoundException;
import com.vitalpet.msbranches.model.Branch;
import com.vitalpet.msbranches.model.City;
import com.vitalpet.msbranches.repository.RepositoryBranches;
import com.vitalpet.msbranches.repository.RepositoryCity;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //Esto sirve para decirle a JUnit que usaremos mockito para simular objetos
class ServiceBranchesTest {

    //Creamos versiones "fake" (simuladas) de nuestros repos.
    @Mock
    private RepositoryBranches repositoryBranches;

    @Mock
    private RepositoryCity repositoryCity;

    //Ahora creamos el service real pero le injectamos nuestros repositorios fake;
    @InjectMocks
    private ServiceBranches serviceBranches;

    //Variables globales para los datos de prueba
    private Faker faker;
    private Branch mockBranch;
    private City mockCity;

    // @BeforeEach: Este metodo se ejecuta automaticamente ANTES de cada @test.
    // Lo vamos a ocupar para incializar datos frescos.
    @BeforeEach
    void setUp(){
        //Nota, faker tiene en sus metodos los datos solo hay que buscarlos.
        faker = new Faker();

        mockCity = new City();
        mockCity.setId(1L);
        mockCity.setName(faker.address().cityName());

        mockBranch = new Branch();
        mockBranch.setId(1L);
        mockBranch.setName(faker.company().name());
        mockBranch.setAddress(faker.address().streetAddress());
        mockBranch.setPhone(faker.phoneNumber().phoneNumber());
        mockBranch.setEmail(faker.internet().emailAddress());
        mockBranch.setActive(true);
        mockBranch.setCreatedAt(LocalDateTime.now());
        mockBranch.setCity(mockCity);

    }

    //Para llegar al 100% de cobertura hay que incluir test tanto para los caminos que funcionan de manera correcta como para las excepciones

    //GetAll Branches
    @Test
    void getAll_ShouldReturnListOfBranchesDTO() {
        //Acá le decimos a mockito como responder a nuestro llamado
        when(repositoryBranches.findByActiveTrue()).thenReturn(List.of(mockBranch));

        List<BranchesResponseDTO> result = serviceBranches.getAll();

        //Afirmar los resultados esperados.
        assertNotNull(result); // La lista no debe ser nula.
        assertEquals(1, result.size()); // Debe tener 1 elemento.
        assertEquals(mockBranch.getName(),result.get(0).getName()); // El DTO debe tener el nombre de la entidad simulada.
        assertEquals(mockCity.getName(), result.get(0).getCityName()); // El mapeo de la ciudad debe funcionar.

        verify(repositoryBranches, times(1)).findByActiveTrue(); // Verificamos que el repo sea llamado almenos 1 vez
    }
    //GetAll Cities
    @Test
    void getAll_ShouldReturnListOfCitiesDTO(){
        when(repositoryCity.findAll()).thenReturn(List.of(mockCity));

        List<CityResponseDTO> result = serviceBranches.getAllCities();

        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(mockCity.getName(),result.get(0).getName());

        verify(repositoryCity, times(1)).findAll();
    }

    //Create funcionando
    @Test
    void create_ShouldReturnBranchDTO_WhenDataIsCorrect(){
        BranchesRequestDTO requestDTO = new BranchesRequestDTO(
          faker.company().name(),
          faker.address().streetAddress(),
          faker.phoneNumber().phoneNumber(),
          faker.internet().emailAddress(),
          mockCity.getName()
        );

        //Aca basicamente tenemos que ir pasando nuestras propias validaciones
        //1. Que la dirección no exista.
        when(repositoryBranches.existsByAddress(requestDTO.getAddress())).thenReturn(false);
        //2. Que la ciudad SI existe.
        when(repositoryCity.findByName(requestDTO.getCityName())).thenReturn(Optional.of(mockCity));
        //3.Simulamos el guardado  (usamos any(Branch.class) porque el metodo create() hace un new Branch internamente.)
        when(repositoryBranches.save(any(Branch.class))).thenReturn(mockBranch);

        //"CREAMOS"
        BranchesResponseDTO result = serviceBranches.create(requestDTO);

        //Ahora los asserts
        assertNotNull(result);
        assertEquals(mockBranch.getName(), result.getName());
        assertEquals(mockCity.getName(), result.getCityName());

        //Verificamos el flujo (que se llamaron al menos una vez)
        verify(repositoryBranches, times(1)).existsByAddress(anyString());
        verify(repositoryCity, times(1)).findByName(anyString());
        verify(repositoryBranches, times(1)).save(any(Branch.class));
    }

    //Create no funcionando, IllegalArgumentException
    @Test
    void create_ShouldIllegalArgumentException_WhenAddresAlreadyExists(){
        //Creamos un DTO simulado
        BranchesRequestDTO requestDTO = new BranchesRequestDTO(
          faker.company().name(),
          mockBranch.getAddress(),
          faker.phoneNumber().phoneNumber(),
          faker.internet().emailAddress(),
          mockCity.getName()
        );

        //Simulamos que la base de datos dice , sí el email ya existe
        when(repositoryBranches.existsByAddress(requestDTO.getAddress())).thenReturn(true);

        // Aca utilizamos assertThrows para capturar el error controlado
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serviceBranches.create(requestDTO));

        //Verificamos que el mensaje de error sea exactamente el que pusimos en el service.
        assertEquals("La dirección ya está registrada en una sucursal.", exception.getMessage());

        // verificamos que NUNCA se haya llamado al save.
        verify(repositoryBranches, never()).save(any(Branch.class));
    }

    // Create no funcionando , ResourceNotFoundException
    @Test
    void create_ShouldThrowResourceNotFoundException_WhenCityDoesNotExist() {
        BranchesRequestDTO requestDTO = new BranchesRequestDTO(
                faker.company().name(),
                "Direccion Falsa 123",
                faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress(),
                "CiudadFantasma" // Simulamos una ciudad que no existe
        );

        // Simulamos que la dirección NO existe
        when(repositoryBranches.existsByAddress(requestDTO.getAddress())).thenReturn(false);
        // Simulamos que al buscar la ciudad en la BD, nos devuelve un Optional vacío
        when(repositoryCity.findByName(requestDTO.getCityName())).thenReturn(Optional.empty());

        // Ahora le decimos a JUnit que espere la excepcion que tenemos personalizada
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceBranches.create(requestDTO);
        });

        assertEquals("Ciudad no encontrada" + requestDTO.getCityName(), exception.getMessage());
        verify(repositoryBranches, never()).save(any(Branch.class));
    }

    //GetById funcionando
    @Test
    void getById_ShouldReturnBranchDTO_WhenBranchExists() {
        Long validId = 1L;
        // Simulamos que la base de datos SÍ encuentra la sucursal y la devuelve envuelta en un Optional
        when(repositoryBranches.findById(validId)).thenReturn(Optional.of(mockBranch));

        BranchesResponseDTO result = serviceBranches.getById(validId);

        assertNotNull(result); // Validamos que no devuelva nulo
        assertEquals(mockBranch.getId(), result.getId()); // Validamos que los IDs coincidan
        assertEquals(mockBranch.getName(), result.getName()); // Validamos que el mapeo del nombre funcione

        // Verificamos que el repositorio fue consultado exactamente 1 vez con ese ID
        verify(repositoryBranches, times(1)).findById(validId);
    }

    //GetById no funcionando, ResourceNotFoundException
    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenBranchDoesNotExists(){
        Long invalidId = 99L;
        when(repositoryBranches.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceBranches.getById(invalidId);
        });

        assertEquals("No se encontró ninguna sucursal con el ID: " + invalidId, exception.getMessage());
        verify(repositoryBranches, times(1)).findById(invalidId);
    }

    //Validación simple
    @Test
    void branchExistsById_ShouldReturnTrue_WhenBranchExists() {
        when(repositoryBranches.existsById(1L)).thenReturn(true);
        boolean exists = serviceBranches.branchExistsById(1L);
        assertTrue(exists);
        verify(repositoryBranches, times(1)).existsById(1L);
    }

    //Update Feliz
    @Test
    void update_ShouldReturnUpdateBranchesDTO_WhenDataIsCorrect(){
        Long validId = 1L;
        BranchesRequestDTO requestDTO = new BranchesRequestDTO(
                faker.company().name(),
                "Direccion Falsa 133",
                faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress(),
                mockCity.getName()
        );

        when(repositoryBranches.findById(validId)).thenReturn(Optional.of(mockBranch));
        when(repositoryCity.findByName(requestDTO.getCityName())).thenReturn(Optional.of(mockCity));
        when(repositoryBranches.save(any(Branch.class))).thenReturn(mockBranch);

        BranchesResponseDTO result = serviceBranches.update(validId,requestDTO);

        assertNotNull(result);
        assertEquals(requestDTO.getName(), result.getName());
        assertEquals(requestDTO.getAddress(), result.getAddress());
        assertEquals(requestDTO.getPhone(), result.getPhone());
        assertEquals(requestDTO.getEmail(), result.getEmail());
        assertEquals(requestDTO.getCityName(), result.getCityName());

        verify(repositoryBranches, times(1)).findById(validId);
        verify(repositoryCity, times(1)).findByName(requestDTO.getCityName());
        verify(repositoryBranches, times(1)).save(any(Branch.class));

    }

    //Update no Funcionando
    @Test
    void update_ShouldThrowResourceNotFoundException_WhenBranchDoesNotExists(){
        Long invalidId = 99L;
        BranchesRequestDTO requestDTO = new BranchesRequestDTO(
                faker.company().name(),
                "Direccion Falsa 133",
                faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress(),
                "CiudadX"
        );

        when(repositoryBranches.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, ()-> {
            serviceBranches.update(invalidId,requestDTO);
        });

        assertEquals("No se encontró ninguna sucursal con el ID: " + invalidId,exception.getMessage());

        //Aca confirmamos que la operación se cayó y nunca busco nada.
        verify(repositoryBranches, times(1)).findById(invalidId);
        verify(repositoryCity,never()).findByName(anyString());
        verify(repositoryBranches,never()).save(any());
    }

    //Para el desactivate (Remove logico)
    @Test
    void deactivate_ShouldSetBranchAsInactive_WhenBranchExists() {
        Long validId = 1L;
        // Nos aseguramos de que el mock empiece como activo
        mockBranch.setActive(true);
        when(repositoryBranches.findById(validId)).thenReturn(Optional.of(mockBranch));

        serviceBranches.deactivate(validId);

        assertFalse(mockBranch.getActive());

        // Verificamos que se consultó la BD y luego se guardó el cambio
        verify(repositoryBranches, times(1)).findById(validId);
        verify(repositoryBranches, times(1)).save(mockBranch);
    }

    //FindByCity funcionando.
    @Test
    void findByCityId_ShouldReturnListOfBranches_WhenCityExists() {
        Long validCityId = 1L;
        when(repositoryCity.existsById(validCityId)).thenReturn(true);

        when(repositoryBranches.findByCityIdAndActiveTrue(validCityId)).thenReturn(List.of(mockBranch));

        List<BranchesResponseDTO> result = serviceBranches.findByCityId(validCityId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockBranch.getName(), result.get(0).getName());

        verify(repositoryCity, times(1)).existsById(validCityId);
        verify(repositoryBranches, times(1)).findByCityIdAndActiveTrue(validCityId);
    }

    //FindbyCity no funcionando.
    @Test
    void findByCityId_ShouldThrowResourceNotFoundException_WhenCityDoesNotExist() {
        Long invalidCityId = 99L;

        when(repositoryCity.existsById(invalidCityId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceBranches.findByCityId(invalidCityId);
        });

        assertEquals("La ciudad : " + invalidCityId + " no existe.", exception.getMessage());

        verify(repositoryCity, times(1)).existsById(invalidCityId);
        verify(repositoryBranches, never()).findByCityIdAndActiveTrue(anyLong());
    }
}