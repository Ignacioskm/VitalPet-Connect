package com.vitalpet.msusers.assembler;

import com.vitalpet.msusers.controller.UserController;
import com.vitalpet.msusers.dto.UserResponseDTO;
import com.vitalpet.msusers.model.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>> {

    @Override
    public EntityModel<UserResponseDTO> toModel(UserResponseDTO userDTO){
        return EntityModel.of(userDTO,
                linkTo(methodOn(UserController.class).getById(userDTO.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("all_users")
        );
    }
}
