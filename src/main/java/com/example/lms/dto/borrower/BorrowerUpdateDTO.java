package com.example.lms.dto.borrower;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerUpdateDTO {
    private String name;
    @Email
    private String email;
    private String phoneNumber;
}
