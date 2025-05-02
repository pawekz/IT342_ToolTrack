package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.BorrowToolDTO;
import edu.cit.tooltrack.dto.TransactionApproval;
import edu.cit.tooltrack.dto.TransactionsDTO;
import edu.cit.tooltrack.entity.ToolTransaction;
import edu.cit.tooltrack.service.ToolTransactionService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net"})
public class TransactionController {
    @Autowired
    private ToolTransactionService toolTransactionService;


    @GetMapping("/getAllTransactions")
    public ResponseEntity<?> getAllTransactions() {
        List<TransactionsDTO> transactions = toolTransactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }
        return ResponseEntity.ok(Map.of("transactions", transactions));
    }

    @PutMapping("/approval/validate")
    public ResponseEntity<?> validateTransaction(@RequestBody TransactionApproval approval) {
        ToolTransaction transaction = toolTransactionService.approval(approval.getTransactionId(), approval.getApprovalStatus());
        if(transaction != null){
            return  ResponseEntity.ok(Map.of("transaction", transaction));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/addTransaction")
    public ResponseEntity<?> addTransction(@RequestBody BorrowToolDTO borrowToolDTO){
        ToolTransaction transaction = toolTransactionService.addTransation(borrowToolDTO.getToolId(),borrowToolDTO.getEmail());
        if(transaction != null){
            return  ResponseEntity.ok(Map.of("transaction", transaction));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getSortedDates/{sortedBy}")
    public ResponseEntity<?> getDate(@PathVariable String sortedBy){
        Map<Month, Long> timestamps = toolTransactionService.getFormatedDatesSortedBy(sortedBy);
        if(timestamps.isEmpty()){
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }else{
            return ResponseEntity.ok(Map.of("timestamps", timestamps));
        }
    }
}
