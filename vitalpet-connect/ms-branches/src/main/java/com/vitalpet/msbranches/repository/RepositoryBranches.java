package com.vitalpet.msbranches.repository;

import com.vitalpet.msbranches.model.Branches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryBranches extends JpaRepository<Branches,Long> {

    Optional<Branches> findbyAddress(String address);
    boolean existsByAddress(String address);

    List<Branches> findByActiveTrue();
    List<Branches> findByCityId(Long cityId);

}
