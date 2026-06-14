package com.vitalpet.msusers.controller;

import com.vitalpet.msusers.assembler.UserModelAssembler;
import com.vitalpet.msusers.dto.UserRequestDTO;
import com.vitalpet.msusers.dto.UserResponseDTO;
import com.vitalpet.msusers.model.User;
import com.vitalpet.msusers.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private UserModelAssembler userAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAll(){
        List<UserResponseDTO> users = userService.getAll();
        return ResponseEntity.ok(userAssembler.toCollectionModel(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getById(@PathVariable Long id){
        UserResponseDTO userDTO = userService.getById(id);
        return ResponseEntity.ok(userAssembler.toModel(userDTO));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO dto){
        UserResponseDTO createdUser = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAssembler.toModel(createdUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto){
        UserResponseDTO updateUser = userService.update(id,dto);
        return ResponseEntity.ok(userAssembler.toModel(updateUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    //listar usuarios por rol
    @GetMapping("/role/{roleName}")
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getUsersByRol(@PathVariable String roleName){
        List<UserResponseDTO> users = userService.getUsersByRol(roleName);
        return ResponseEntity.ok(userAssembler.toCollectionModel(users));
    }

    //Verificar si el usuario existe (Este endpoint sera consumido por otros MS)
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExistsById(@PathVariable Long id){
        return ResponseEntity.ok(userService.UserExistsById(id));
    }

    //Verificar con id si user es cliente
    @GetMapping("/{id}/is-client")
    public ResponseEntity<Boolean> isClient(@PathVariable Long id){
        return ResponseEntity.ok(userService.isClient(id));
    }

    @GetMapping("/{id}/is-vet")
    public ResponseEntity<Boolean> isVet(@PathVariable Long id){
        return ResponseEntity.ok(userService.isVet(id));
    }

    //Endpoint para obtener SOLO el email (ms-notifications)
    @GetMapping("/{id}/email")
    public ResponseEntity<String> getEmailById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getEmailById(id));
    }
}

