package com.vitalpet.msauth.service;

import com.vitalpet.msauth.client.UserClient;
import com.vitalpet.msauth.dto.AuthLoginDTO;
import com.vitalpet.msauth.dto.UserRequestDTO;
import com.vitalpet.msauth.dto.UserResponseDTO;
import com.vitalpet.msauth.model.Credential;
import com.vitalpet.msauth.repository.CredentialRepository;
import com.vitalpet.msauth.security.JwtProvider;
import com.vitalpet.msauth.dto.AuthRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Transactional //Si algo falla se deshace
    public String register(AuthRegisterDTO dto){

        //Validamos si existe el email.
        if(credentialRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException("El email ya esta registrado");
        }

        //Armamos el request para ms-users
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName(dto.getFirstName());
        userRequestDTO.setLastName(dto.getLastName());
        userRequestDTO.setEmail(dto.getEmail());
        userRequestDTO.setPhoneNumber(dto.getPhoneNumber());
        userRequestDTO.setAddress(dto.getAddress());
        userRequestDTO.setRoleName(dto.getRoleName());

        // Llamamos a ms user para crear el perfil
        UserResponseDTO userResponse = userClient.createUser(userRequestDTO);

        // Creamos y guardamos la credencial segura
        Credential credential = new Credential();
        credential.setUserId(userResponse.getId());
        credential.setEmail(dto.getEmail());
        credential.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        credentialRepository.save(credential);

        return "Usuario registrado exitosamente";
    }

    public String login(AuthLoginDTO dto){

        //Buscamos si el correo existe
        Credential credential = credentialRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales invalidas"));

        //Comparamos contraseñas (ocupamos matches en vez de == porque el hash es distinto cada vez)
        if(!passwordEncoder.matches(dto.getPassword(), credential.getPasswordHash())){
            throw new RuntimeException("Credenciales Inválidas");
        }

        //Consultamos rol al ms-user
        UserResponseDTO userProfile = userClient.getUserById(credential.getId());

        //Actualizamos el login netamente pa trazabilidad
        credential.setLastLogin(LocalDateTime.now());
        credentialRepository.save(credential);

        //RETORNAMOS el token fabricado
        return jwtProvider.generateToken(credential.getUserId(),userProfile.getRoleName());
    }

}
