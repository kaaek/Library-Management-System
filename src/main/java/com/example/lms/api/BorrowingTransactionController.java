package com.example.lms.api;

import com.example.lms.service.BorrowingTransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/borrowings")
public class BorrowingTransactionController {

    private final BorrowingTransactionService borrowingTransactionService;

    @GetMapping("/all")
    public List<>


}
