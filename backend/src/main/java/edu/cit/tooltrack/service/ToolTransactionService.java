package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.NotificationMessageDTO;
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
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ToolTransactionService {
    @Autowired
    private ToolTransactionRepository toolTransactionRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ToolItemService toolItemService;

    @Autowired
    private NotificationService notificationService;

    public ToolTransaction addTransation(int toolId, String email) {

        //checker for existing transaction
        User user = null;
        ToolItems item = null;
        try {
            user = userService.getUserFullDetails(email);
            item = toolItemService.getToolItem(toolId);

            ToolTransaction transaction = new ToolTransaction();
            transaction.setTool_id(item);
            transaction.setUser_id(user);
            transaction.setBorrow_date(Timestamp.valueOf(LocalDateTime.now()));
            transaction.setStatus(ToolTransaction.Status.pending);
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
            System.out.println(transaction);
            item = transaction.getTool_id();
            if (isApproved) {
                transaction.setDue_date(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
                transaction.setTransaction_type(ToolTransaction.TransactionType.borrow);
                transaction.setStatus(ToolTransaction.Status.approved);
                item.setStatus(ToolItems.Status.BORROWED);


                notificationService.sendNotification(
                        transaction.getUser_id().getEmail(),
                        NotificationMessageDTO.builder()
                                .toolName(item.getName())
                                .message("Your Requested Tool " + item.getName()+"is approved")
                                .status(transaction.getStatus().toString())
                                .borrow_date(transaction.getBorrow_date())
                                .due_date(transaction.getDue_date())
                                .user_email(transaction.getUser_id().getEmail())
                                .build()
                );

//                notificationService.sendNotification(new NotificationMessageDTO(
//                        item.getName(),
//                        "Your Requested Tool " + item.getName()+"is approved",
//                        transaction.getStatus().toString(),
//                        transaction.getBorrow_date(),
//                        transaction.getDue_date(),
//                        transaction.getUser_id().getEmail()
//                ));

                return toolTransactionRepo.save(transaction);
            } else {
                transaction.setStatus(ToolTransaction.Status.rejected);
                return toolTransactionRepo.save(transaction);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
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
                user.getEmail(),
                item.getTool_id(),
                item.getName(),
                transaction.getBorrow_date(),
                transaction.getDue_date(),
                transaction.getReturn_date(),
                transaction.getTransaction_type(),
                transaction.getStatus());
    }

    public  Map<Month, Long> getFormatedDatesSortedBy(String sortBy) {
        System.out.println("sort by: " + sortBy);
        if(sortBy.equals("Alltime")){
            List<Timestamp> timestamps = toolTransactionRepo.getAllYear();
            return null;
        }else if(sortBy.equals("Last6months")){
            List<Timestamp> timestamps = toolTransactionRepo.getLastSixMonths();
            return formateDates(timestamps);
        }else{
            //last year
            int lastyear = LocalDateTime.now().getYear()-1;
            List<Timestamp> timestamps = toolTransactionRepo.getLastYear(lastyear);
            return formateDates(timestamps);
        }
    }

    private static Map<Month, Long> formateDates(List<Timestamp> timestamps) {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        return timestamps.stream()
                .map(Timestamp::toLocalDateTime)
                .collect(Collectors.groupingBy(LocalDateTime::getMonth, Collectors.counting()));
    }
}
