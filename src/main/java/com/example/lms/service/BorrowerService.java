package com.example.lms.service;

import com.example.lms.dto.borrower.BorrowerRequestDTO;
import com.example.lms.dto.borrower.BorrowerResponseDTO;
import com.example.lms.dto.borrower.BorrowerUpdateDTO;
import com.example.lms.model.Borrower;
import com.example.lms.repository.BorrowerRepository;
import com.example.lms.repository.BorrowingTransactionRepository;

import jakarta.persistence.EntityNotFoundException;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BorrowerService {

    private final String borrowerNotFoundMsg = "Borrower not found with ID: ";
    private final String emailExistsMsg = "E-mail already exists: ";
    private final String phoneNumberExistsMsg = "Phone number already exists: ";

    private BorrowerRepository borrowerRepository;
    private ModelMapper mapper;

    public BorrowerService(BorrowerRepository borrowerRepository, BorrowingTransactionRepository borrowingTransactionRepository, ModelMapper mapper){
        this.borrowerRepository = borrowerRepository;
        this.mapper = mapper;
    }

    public BorrowerResponseDTO createBorrower(BorrowerRequestDTO borrowerRequestDTO) {
        // Fields
        String newName = borrowerRequestDTO.getName();
        String newEmail = borrowerRequestDTO.getEmail();
        String newPhoneNumber = borrowerRequestDTO.getPhoneNumber();


        if(emailExists(newEmail)){
            throw new IllegalArgumentException(emailExistsMsg+newEmail);
        }
        if(phoneNumberExists(newPhoneNumber)){
            throw new IllegalArgumentException(phoneNumberExistsMsg+newPhoneNumber);
        }

        Borrower newBorrower = new Borrower(
                newName,
                newEmail,
                newPhoneNumber
        );

        borrowerRepository.save(newBorrower);

        return mapper.map(newBorrower, BorrowerResponseDTO.class);
    }

    public boolean emailExists(String email) {
        return borrowerRepository.existsByEmail(email);
    }

    public boolean phoneNumberExists(String phoneNumber) {
        return borrowerRepository.existsByPhoneNumber(phoneNumber);
    }

    public List<BorrowerResponseDTO> getAllBorrowers(){
        return borrowerRepository.findAll().stream()
                .map(borrower -> new BorrowerResponseDTO(
                        borrower.getId(),
                        borrower.getName(),
                        borrower.getEmail(),
                        borrower.getPhoneNumber()
                )).toList();
    }

    public BorrowerResponseDTO getBorrowerById(UUID id){
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(borrowerNotFoundMsg + id));

        return mapper.map(borrower, BorrowerResponseDTO.class);
    }

    

    public BorrowerResponseDTO update(UUID id, BorrowerUpdateDTO borrowerUpdateDTO) {
        // Fields
        String newName = borrowerUpdateDTO.getName();
        String newEmail = borrowerUpdateDTO.getEmail();
        String newPhoneNumber = borrowerUpdateDTO.getPhoneNumber();

        if(emailExists(newEmail)){
            throw new IllegalArgumentException(emailExistsMsg+newEmail);
        }
        if(phoneNumberExists(newPhoneNumber)){
            throw new IllegalArgumentException(phoneNumberExistsMsg+newPhoneNumber);
        }

        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(borrowerNotFoundMsg + id));
        borrower.setName(newName);
        borrower.setEmail(newEmail);
        borrower.setPhoneNumber(newPhoneNumber);

        borrowerRepository.save(borrower);

        return mapper.map(borrower, BorrowerResponseDTO.class);
    }

    public void deleteBorrowerById(UUID id){
        borrowerRepository.deleteById(id);
    }

    public void deleteAllBorrowers(){
        borrowerRepository.deleteAll();
    }

}
