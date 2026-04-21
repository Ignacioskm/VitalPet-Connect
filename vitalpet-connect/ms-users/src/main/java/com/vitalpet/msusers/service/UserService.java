package com.vitalpet.msusers.service;

import com.vitalpet.msusers.dto.UserRequestDTO;
import com.vitalpet.msusers.dto.UserResponseDTO;
import com.vitalpet.msusers.model.Role;
import com.vitalpet.msusers.model.User;
import com.vitalpet.msusers.repository.RoleRepository;
import com.vitalpet.msusers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    //Convertir User -> UserResponseDTO
    private UserResponseDTO toDTO(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRoleName(user.getRole().getName());
        return dto;
    }

    /*Explicación código:
    findBy busca a todos los users activos, luego pasamos la lista a un flujo de datos con stream como si fuese una cinta transportadora
    luego a cada dato de esa cinta le hacemos un map con el metodo toDTO que transforma un user a dto y retorna dto
    y con .collect volvemos a agrupar esos datos que se mapearon uno a uno una lista pero ahora de UserResponseDTO
     */
    public List<UserResponseDTO> getAll(){
        return userRepository.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }


    //Aca importante acordarse que findById devuelve un Optional! (Leer notas Notion)
    public UserResponseDTO getById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return toDTO(user);
    }


    //Crear usuario DTO **Recordar que el requestDTO es lo que el cliente envía.
    public UserResponseDTO create(UserRequestDTO dto){
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("El email ya está registrado");
        }

        //Buscamos rol por el nombre
        Role role = roleRepository.findByName(dto.getRoleName()).orElseThrow(() -> new RuntimeException("Rol no encontrado" + dto.getRoleName()));

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setRole(role);

        return toDTO(userRepository.save(user));
    }

    //Update usuario DTO
    public UserResponseDTO update(Long id, UserRequestDTO dto){
        User existing = userRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

        Role role = roleRepository.findByName(dto.getRoleName()).orElseThrow(()-> new RuntimeException("Rol no encontrado" + dto.getRoleName()));


        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setAddress(dto.getAddress());
        existing.setRole(role);
        //Considerando que en la lógica buscamos todos por email, no lo dejamos actualizar para evitar duplicados.

        return toDTO(userRepository.save(existing));
    }

    //DESACTIVAMOS un usuario.
    public void deactivate(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado"));

        user.setActive(false);
        userRepository.save(user);
    }

    //Verificar si existe
    public boolean UserExistsById(Long id){
        return userRepository.existsById(id);
    }


    //Traer usuarios por rol
    public List<UserResponseDTO> getUsersByRol(String roleName){
        return userRepository.findByRoleName(roleName).stream().map(this::toDTO).collect(Collectors.toList());
    }

    //Verificar si el user es Cliente
    public Boolean isClient(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getRole().getName().equalsIgnoreCase("CLIENT");
    }
}
