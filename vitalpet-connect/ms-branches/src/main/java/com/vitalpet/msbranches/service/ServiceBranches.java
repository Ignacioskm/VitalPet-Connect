package com.vitalpet.msbranches.service;

import com.vitalpet.msbranches.dto.BranchesRequestDTO;
import com.vitalpet.msbranches.dto.BranchesResponseDTO;
import com.vitalpet.msbranches.model.Branches;
import com.vitalpet.msbranches.model.City;
import com.vitalpet.msbranches.repository.RepositoryBranches;
import com.vitalpet.msbranches.repository.RepositoryCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceBranches {
    @Autowired
    private RepositoryBranches repositoryBranches;

    @Autowired
    private RepositoryCity repositoryCity;

    private BranchesResponseDTO toDTO(Branches branches) {
        BranchesResponseDTO dto = new BranchesResponseDTO();
        dto.setId(branches.getId());
        dto.setName(branches.getName());
        dto.setAddress(branches.getAddress());
        dto.setPhone(branches.getPhone());
        dto.setEmail(branches.getEmail());
        dto.setActive(branches.getActive());
        dto.setCreatedAt(branches.getCreatedAt());
        dto.setCityName(branches.getCity().getName());
        return dto;
    }


    public List<BranchesResponseDTO> getAll() {
        return repositoryBranches.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }


    //Buscar por ID de sucursal
    public BranchesResponseDTO getById(Long id) {
        Branches branches = repositoryBranches.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada."));
        return toDTO(branches);
    }


    //Crear Sucursal
    public BranchesResponseDTO create(BranchesRequestDTO dto) {
        if (repositoryBranches.existsByAddress(dto.getAddress())) {
            throw new RuntimeException("La dirección ya está registrada en una sucursal.");
        }

        City city = repositoryCity.findByName(dto.getCityName()).orElseThrow(() -> new RuntimeException("Ciudad no encontrada" + dto.getCityName()));

        Branches branches = new Branches();
        branches.setName(dto.getName());
        branches.setAddress(dto.getAddress());
        branches.setPhone(dto.getPhone());
        branches.setEmail(dto.getEmail());
        branches.setCity(city);

        return toDTO(repositoryBranches.save(branches));
    }

    //Actualizar sucursal
    public BranchesResponseDTO update(Long id, BranchesRequestDTO dto) {
        Branches existing = repositoryBranches.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada."));

        City city = repositoryCity.findByName(dto.getCityName()).orElseThrow(() -> new RuntimeException("Ciudad no encontrada" + dto.getCityName()));

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setCity(city);

        return toDTO(repositoryBranches.save(existing));
    }

    //Desactivar sucursal
    public void deactivate(Long id) {
        Branches branches = repositoryBranches.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada."));

        branches.setActive(false);
        repositoryBranches.save(branches);
    }
}
