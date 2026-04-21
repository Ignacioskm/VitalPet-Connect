package com.vitalpet.msusers.repository;

import com.vitalpet.msusers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    //Optional puede contener o no un valor nulo, nos sirve utilizar la excepción en el service.
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByActiveTrue();
    List<User> findByRoleName(String roleName);
}
