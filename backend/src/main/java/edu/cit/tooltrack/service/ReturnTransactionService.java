package edu.cit.tooltrack.service;

import edu.cit.tooltrack.entity.ReturnTransaction;
import edu.cit.tooltrack.repository.ReturnTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnTransactionService {

    @Autowired
    private ReturnTransactionRepository returnTransactionRepo;

    public ReturnTransaction add(ReturnTransaction returnTransaction) {
        return returnTransactionRepo.save(returnTransaction);
    }


    public void delete(int id) {
        if (returnTransactionRepo.existsById(id)) {
            returnTransactionRepo.deleteById(id);
        } else {
            throw new RuntimeException("ReturnTransaction not found");
        }
    }

}
