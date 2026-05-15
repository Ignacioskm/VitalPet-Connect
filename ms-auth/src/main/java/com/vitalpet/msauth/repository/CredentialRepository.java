package com.vitalpet.msauth.repository;

import com.vitalpet.msauth.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential,Long> {

    Optional<Credential> findByEmail(String email);
    boolean existsByEmail(String email);

}
