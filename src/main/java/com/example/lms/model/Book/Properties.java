package com.example.lms.model.Book;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Properties {
    private BigDecimal extra_days_rental_price;
    private BigDecimal insurance_fees;   
}
