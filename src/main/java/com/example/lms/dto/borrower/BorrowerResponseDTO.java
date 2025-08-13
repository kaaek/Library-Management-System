package com.example.lms.dto.borrower;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
}
