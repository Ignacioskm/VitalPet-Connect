package com.vitalpet.msbranches.repository;

import com.vitalpet.msbranches.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryBranches extends JpaRepository<Branch,Long> {

    Optional<Branch> findByAddress(String address);
    boolean existsByAddress(String address);

    List<Branch> findByActiveTrue();

    //Para que nos busque por ciudad y activas.
    List<Branch> findByCityIdAndActiveTrue(Long cityId);
}
