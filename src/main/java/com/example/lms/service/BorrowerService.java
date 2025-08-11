package com.example.lms.service;

import com.example.lms.dto.borrower.BorrowerRequestDTO;
import com.example.lms.dto.borrower.BorrowerResponseDTO;
import com.example.lms.dto.borrower.BorrowerUpdateDTO;
import com.example.lms.model.Borrower;
import com.example.lms.model.BorrowingTransaction;
import com.example.lms.repository.BorrowerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BorrowerService {

    private final String borrowerNotFoundMsg = "Borrower not found with ID: ";
    private final String emailExistsMsg = "E-mail already exists: ";
    private final String phoneNumberExistsMsg = "Phone number already exists: ";

    private BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository){
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowerResponseDTO createBorrower(BorrowerRequestDTO borrowerRequestDTO) {
        // Fields
        String newName = borrowerRequestDTO.getName();
        String newEmail = borrowerRequestDTO.getEmail();
        String newPhoneNumber = borrowerRequestDTO.getPhoneNumber();

//        // Check if e-mail exists
//        if(borrowerRepository.existsByEmail(newEmail)){
//            throw new IllegalArgumentException("E-mail already exists: "+newEmail);
//        }
//        // Check if phone number exists
//        if(borrowerRepository.existsByPhoneNumber(newPhoneNumber)){
//            throw new IllegalArgumentException("Phone number already exists: "+newPhoneNumber);
//        }

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

        return new BorrowerResponseDTO(
                newBorrower.getId(),
                newBorrower.getName(),
                newBorrower.getEmail(),
                newBorrower.getPhoneNumber(),
                newBorrower.getTransactions()
                        .stream()
                        .map(BorrowingTransaction::getId)
                        .collect(Collectors.toSet())
        );
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
                        borrower.getPhoneNumber(),
                        borrower.getTransactions().stream()
                                .map(BorrowingTransaction::getId)
                                .collect(Collectors.toSet())
                )).collect(Collectors.toList());
    }

    public BorrowerResponseDTO getBorrowerById(UUID id){
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new BorrowerNotFoundException(borrowerNotFoundMsg + id));

        return new BorrowerResponseDTO(
                borrower.getId(),
                borrower.getName(),
                borrower.getEmail(),
                borrower.getPhoneNumber(),
                borrower.getTransactions().stream()
                        .map(BorrowingTransaction::getId)
                        .collect(Collectors.toSet())
        );
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
                .orElseThrow(() -> new BorrowerNotFoundException(borrowerNotFoundMsg + id));
        borrower.setName(newName);
        borrower.setEmail(newEmail);
        borrower.setPhoneNumber(newPhoneNumber);

        borrowerRepository.save(borrower);

        return new BorrowerResponseDTO(
                borrower.getId(),
                borrower.getName(),
                borrower.getEmail(),
                borrower.getPhoneNumber(),
                borrower.getTransactions().stream().map(BorrowingTransaction::getId).collect(Collectors.toSet())
        );
    }

    public void deleteBorrowerById(UUID id){
        borrowerRepository.deleteById(id);
    }

    public void deleteAllBorrowers(){
        borrowerRepository.deleteAll();
    }

}
