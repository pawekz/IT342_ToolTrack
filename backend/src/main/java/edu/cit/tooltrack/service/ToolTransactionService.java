package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.TransactionsDTO;
import edu.cit.tooltrack.entity.ToolItems;
import edu.cit.tooltrack.entity.ToolTransaction;
import edu.cit.tooltrack.entity.User;
import edu.cit.tooltrack.repository.ToolTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolTransactionService {
    @Autowired
    private ToolTransactionRepository toolTransactionRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ToolItemService toolItemService;

    public ToolTransaction addTransation(int toolId, String email) {
        User user = null;
        ToolItems item = null;
        try {
            user = userService.getUserFullDetails(email);
            item = toolItemService.getToolItem(toolId);

            ToolTransaction transaction = new ToolTransaction();
            transaction.setTool_id(item);
            transaction.setUser_id(user);
            transaction.setBorrow_date(Timestamp.valueOf(LocalDateTime.now()));
            return toolTransactionRepo.save(transaction);
        } catch (Exception e) {
            return null;
        }
    }

    public ToolTransaction approval(int transaction_id, Boolean isApproved) {
        ToolTransaction transaction = null;
        ToolItems item = null;
        try {
            transaction = toolTransactionRepo.findById(transaction_id).orElse(null);
            item = transaction.getTool_id();
            if (isApproved) {
                transaction.setDue_date(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
                transaction.setTransaction_type(ToolTransaction.TransactionType.borrow);
                transaction.setStatus(ToolTransaction.Status.approved);
                item.setStatus(ToolItems.Status.BORROWED);
                return toolTransactionRepo.save(transaction);
            } else {
                transaction.setStatus(ToolTransaction.Status.rejected);
            }
        } catch (Exception e) {
            return null;
        }
        return transaction;
    }

    public List<TransactionsDTO> getAllTransactions() {
        return toolTransactionRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TransactionsDTO mapToDTO(ToolTransaction transaction) {
        ToolItems item = transaction.getTool_id();
        User user = transaction.getUser_id();
        return new TransactionsDTO(
                transaction.getTransaction_id(),
                user.getUser_id(),
                user.getFirst_name(),
                user.getLast_name(),
                item.getTool_id(),
                item.getName());
    }

}
