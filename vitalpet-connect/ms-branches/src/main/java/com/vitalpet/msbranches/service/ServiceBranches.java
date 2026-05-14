package com.vitalpet.msbranches.service;

import com.vitalpet.msbranches.dto.BranchesRequestDTO;
import com.vitalpet.msbranches.dto.BranchesResponseDTO;
import com.vitalpet.msbranches.dto.CityResponseDTO;
import com.vitalpet.msbranches.exception.ResourceNotFoundException;
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

    @Autowired private RepositoryBranches repositoryBranches;
    @Autowired private RepositoryCity repositoryCity;

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

    private CityResponseDTO cityToDTO(City city){
        CityResponseDTO dto = new CityResponseDTO();
        dto.setId(city.getId());
        dto.setName(city.getName());
        return dto;
    }

    public List<BranchesResponseDTO> getAll() {
        return repositoryBranches.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CityResponseDTO> getAllCities(){
        return repositoryCity.findAll().stream().map(this::cityToDTO).toList();
    }


    //Buscar por ID de sucursal
    public BranchesResponseDTO getById(Long id) {
        Branch branch = repositoryBranches.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna sucursal con el ID: " + id));
        return toDTO(branch);
    }


    //Crear Sucursal
    public BranchesResponseDTO create(BranchesRequestDTO dto) {
        if (repositoryBranches.existsByAddress(dto.getAddress())) {
            throw new IllegalArgumentException("La dirección ya está registrada en una sucursal.");
        }

        City city = repositoryCity.findByName(dto.getCityName()).orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada" + dto.getCityName()));

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
        Branch existing = repositoryBranches.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna sucursal con el ID: " + id));

        City city = repositoryCity.findByName(dto.getCityName()).orElseThrow(() -> new ResourceNotFoundException("Ciudad no encontrada" + dto.getCityName()));

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setCity(city);

        return toDTO(repositoryBranches.save(existing));
    }

    //Desactivar sucursal
    public void deactivate(Long id) {
        Branch branch = repositoryBranches.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ninguna sucursal con el ID: " + id));

        branch.setActive(false);
        repositoryBranches.save(branch);
    }

    //Traer sucursales por ciudad
    public List<BranchesResponseDTO> findByCityId(Long cityId){
        boolean cityExists = repositoryCity.existsById(cityId);

        if(!cityExists){
            throw new ResourceNotFoundException("La ciudad : " + cityId + " no existe.");
        }

        return repositoryBranches.findByCityIdAndActiveTrue(cityId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    //Verificar si la sucursal existe
    public boolean branchExistsById(Long id){
        return repositoryBranches.existsById(id);
    }
}
