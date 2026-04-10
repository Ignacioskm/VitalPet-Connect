package com.vitalpet.msusers.service;

import com.vitalpet.msusers.dto.UserRequestDTO;
import com.vitalpet.msusers.model.User;
import com.vitalpet.msusers.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    //Traer usuarios
    public List<User> getAll(){return userRepository.findAll();}

    //Buscar por su id.
    //Aca hacemos uso del optional, elseThrow lanza una excepción si el valor no está presente.
    public User getById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    //Crear usuario DTO **Recordar que el requestDTO es lo que el cliente envía.
    public User create(UserRequestDTO dto){
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAdress());

        return userRepository.save(user);
    }

    //Update usuario DTO
    public User update(Long id, UserRequestDTO dto){
        User existing = getById(id);

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setAddress(dto.getAdress());
        //Considerando que en la lógica buscamos todos por email, no lo dejamos actualizar para evitar duplicados.
        return userRepository.save(existing);
    }

    //Eliminar usuario
    public void delete(Long id){
        User user = getById(id);
        userRepository.delete(user);
    }
}
