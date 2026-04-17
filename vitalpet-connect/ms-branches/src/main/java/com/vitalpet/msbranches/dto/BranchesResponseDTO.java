package com.vitalpet.msbranches.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchesResponseDTO {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
    private String cityName;

}
