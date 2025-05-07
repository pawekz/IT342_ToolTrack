package edu.cit.tooltrack.controller;

import edu.cit.tooltrack.dto.BorrowToolDTO;
import edu.cit.tooltrack.dto.TransactionApproval;
import edu.cit.tooltrack.dto.TransactionsDTO;
import edu.cit.tooltrack.entity.ToolTransaction;
import edu.cit.tooltrack.service.ToolTransactionService;
import jakarta.transaction.Transaction;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/transaction")
@CrossOrigin(origins = {"http://localhost:5173", "https://tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net"})
public class TransactionController {
    @Autowired
    private ToolTransactionService toolTransactionService;
    @Autowired
    private TransactionDefinition transactionDefinition;


    @GetMapping("/getAllTransactions")
    public ResponseEntity<?> getAllTransactions() {
        List<TransactionsDTO> transactions = toolTransactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }
        return ResponseEntity.ok(Map.of("transactions", transactions));
    }

    @GetMapping("/getAllPendings")
    public ResponseEntity<?> getAllPendingsTransactions() {
        List<TransactionsDTO> transactions = toolTransactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }
        transactions.removeIf(transaction ->
                transaction.getStatus().equals(ToolTransaction.Status.approved) ||
                transaction.getStatus().equals(ToolTransaction.Status.rejected));
        return ResponseEntity.ok(Map.of("transactions", transactions));
    }

    @GetMapping("/getAllProcessed")
    public ResponseEntity<?> getAllProcessedTransactions() {
        List<TransactionsDTO> transactions = toolTransactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }
        transactions.removeIf(transaction ->
                transaction.getStatus().equals(ToolTransaction.Status.pending));
        return ResponseEntity.ok(Map.of("transactions", transactions));
    }

    @GetMapping("/getAllBorrowed")
    public ResponseEntity<?> getBorrowed(){
        List<TransactionsDTO> transactions = toolTransactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }
        transactions.removeIf(transaction ->
                transaction.getStatus().equals(ToolTransaction.TransactionType.returned));
        return ResponseEntity.ok(Map.of("transactions", transactions));
    }

        @GetMapping("/getAllPopularTool")
    public ResponseEntity<?> getPopularTool() {
        List<TransactionsDTO> transactions = toolTransactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No transactions found"));
        }
        HashMap<String, Integer> popularTool = new HashMap<>();

        for (TransactionsDTO transaction : transactions) {
            if (transaction.getStatus() == ToolTransaction.Status.approved &&
                    transaction.getTransaction_type() == ToolTransaction.TransactionType.borrow) {
                popularTool.put(transaction.getTool_name(), popularTool.getOrDefault(transaction.getTool_name(), 0) + 1);
            }
        }

        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : popularTool.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 5) {
                minHeap.poll(); // Remove the smallest
            }
        }

        // Extract, sort in descending order, and convert to Map
        List<Map.Entry<String, Integer>> result = new ArrayList<>(minHeap);
        result.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> topPopularTools = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : result) {
            topPopularTools.put(entry.getKey(), entry.getValue());
        }

        return ResponseEntity.ok(Map.of("popularTool", topPopularTools));
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
