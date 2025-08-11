package com.example.lms.dto.borrower;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BorrowerUpdateDTO {
    private String name;
    private String email;
    private String phoneNumber;
}
