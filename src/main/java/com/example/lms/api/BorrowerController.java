package com.example.lms.api;

import com.example.lms.dto.borrower.BorrowerRequestDTO;
import com.example.lms.dto.borrower.BorrowerResponseDTO;
import com.example.lms.dto.borrower.BorrowerUpdateDTO;
import com.example.lms.service.BorrowerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping("/new")
    public BorrowerResponseDTO createBorrower(@RequestBody BorrowerRequestDTO borrowerRequestDTO) {
        return borrowerService.createBorrower(borrowerRequestDTO);
    }

    @GetMapping("/all")
    public List<BorrowerResponseDTO> getAllBorrowers() {
        return borrowerService.getAllBorrowers();
    }

    @GetMapping("/{id}")
    public BorrowerResponseDTO getBorrowerById(@PathVariable UUID id){
        return borrowerService.getBorrowerById(id);
    }

    @PutMapping("/{id}")
    public BorrowerResponseDTO update(@PathVariable UUID id, @RequestBody BorrowerUpdateDTO borrowerUpdateDTO) {
        return borrowerService.update(id, borrowerUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBorrower(@PathVariable UUID id){
        borrowerService.deleteBorrowerById(id);
    }

    @DeleteMapping("/all")
    public void deleteAll(){
        borrowerService.deleteAllBorrowers();
    }

}
