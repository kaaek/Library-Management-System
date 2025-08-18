package com.example.lms.dto.debit;

import java.math.BigDecimal;
import com.example.lms.model.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitRequestDTO {
    private String cardNumber;
    private BigDecimal amount;
    private Currency currency;
}
