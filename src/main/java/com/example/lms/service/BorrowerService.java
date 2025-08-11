package com.example.lms.service;

import com.example.lms.repository.BorrowerRepository;
import org.springframework.stereotype.Service;

@Service
public class BorrowerService {

    private BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository){
        this.borrowerRepository = borrowerRepository;
    }

}
