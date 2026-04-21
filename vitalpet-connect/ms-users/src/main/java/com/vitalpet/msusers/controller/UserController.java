package com.vitalpet.msusers.controller;

import com.vitalpet.msusers.dto.UserRequestDTO;
import com.vitalpet.msusers.dto.UserResponseDTO;
import com.vitalpet.msusers.model.User;
import com.vitalpet.msusers.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    //Como primera etapa no agregare las respuestas negativas.

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody UserRequestDTO dto){
        return ResponseEntity.ok(userService.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    //listar usuarios por rol
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRol(@PathVariable String roleName){
        return ResponseEntity.ok(userService.getUsersByRol(roleName));
    }

    //Verificar si el usuario existe (Este endpoint sera consumido por otros MS)
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> userExistsById(@PathVariable Long id){
        return ResponseEntity.ok(userService.UserExistsById(id));
    }
}

