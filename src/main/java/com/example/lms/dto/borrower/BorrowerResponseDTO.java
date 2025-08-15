package com.example.lms.dto.borrower;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import jakarta.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerResponseDTO {
    private UUID id;
    private String name;
    @Email
    private String email;
    private String phoneNumber;
}
