package com.vitalpet.msbranches.repository;

import com.vitalpet.msbranches.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositoryCity extends JpaRepository<City,Long> {
    Optional<City> findByName(String name);
    boolean existByName(String name);


}
