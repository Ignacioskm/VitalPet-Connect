package com.vitalpet.msusers.service;

import com.vitalpet.msusers.model.User;
import com.vitalpet.msusers.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    //Traer usuarios
    public List<User> getAll(){return userRepository.findAll();}

    //Buscar por id
    public User getById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    //Crear usuario DTO


    //Update usuario DTO


    //Eliminar usuario
    public void delete(Long id){
        User user = getById(id);
        userRepository.delete(user);
    }
}
