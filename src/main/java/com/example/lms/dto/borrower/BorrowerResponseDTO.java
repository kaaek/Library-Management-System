package com.example.lms.dto.borrower;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BorrowerResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private Set<UUID> transactionIds;
}
