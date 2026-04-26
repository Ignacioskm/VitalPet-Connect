package com.vitalpet.msbranches.service;

import com.vitalpet.msbranches.dto.BranchesRequestDTO;
import com.vitalpet.msbranches.dto.BranchesResponseDTO;
import com.vitalpet.msbranches.model.Branch;
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

    private BranchesResponseDTO toDTO(Branch branch) {
        BranchesResponseDTO dto = new BranchesResponseDTO();
        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setAddress(branch.getAddress());
        dto.setPhone(branch.getPhone());
        dto.setEmail(branch.getEmail());
        dto.setActive(branch.getActive());
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setCityName(branch.getCity().getName());
        return dto;
    }


    public List<BranchesResponseDTO> getAll() {
        return repositoryBranches.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }


    //Buscar por ID de sucursal
    public BranchesResponseDTO getById(Long id) {
        Branch branch = repositoryBranches.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada."));
        return toDTO(branch);
    }


    //Crear Sucursal
    public BranchesResponseDTO create(BranchesRequestDTO dto) {
        if (repositoryBranches.existsByAddress(dto.getAddress())) {
            throw new RuntimeException("La dirección ya está registrada en una sucursal.");
        }

        City city = repositoryCity.findByName(dto.getCityName()).orElseThrow(() -> new RuntimeException("Ciudad no encontrada" + dto.getCityName()));

        Branch branch = new Branch();
        branch.setName(dto.getName());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());
        branch.setEmail(dto.getEmail());
        branch.setCity(city);

        return toDTO(repositoryBranches.save(branch));
    }

    //Actualizar sucursal
    public BranchesResponseDTO update(Long id, BranchesRequestDTO dto) {
        Branch existing = repositoryBranches.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada."));

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
        Branch branch = repositoryBranches.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada."));

        branch.setActive(false);
        repositoryBranches.save(branch);
    }

    //Traer sucursales por ciudad
    public List<BranchesResponseDTO> findByCityId(Long cityId){
        boolean cityExists = repositoryCity.existsById(cityId);

        if(!cityExists){
            throw new RuntimeException("La ciudad : " + cityId + " no existe.");
        }

        return repositoryBranches.findByCityIdAndActiveTrue(cityId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    //Verificar si la sucursal existe
    public boolean branchExistsById(Long id){
        return repositoryBranches.existsById(id);
    }
}
