package com.example.lms.dto.borrower;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowerUpdateDTO {
    private String name;
    private String email;
    private String phoneNumber;
}
