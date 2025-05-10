package edu.cit.tooltrack.service;

import edu.cit.tooltrack.dto.TransactionsDTO;
import edu.cit.tooltrack.entity.ToolTransaction;
import edu.cit.tooltrack.repository.ToolTransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ToolTransactionServiceTest {

    @Mock
    private ToolTransactionRepository toolTransactionRepo;

    @InjectMocks
    private ToolTransactionService toolTransactionService;

    public ToolTransactionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCountAllTools_WhenNoTransactions_ReturnsEmptyMap() {
        when(toolTransactionRepo.findAll()).thenReturn(Collections.emptyList());

        Map<String, Integer> result = toolTransactionService.countAllTools();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(toolTransactionRepo, times(1)).findAll();
    }

    @Test
    void testCountAllTools_WithValidTransactions_ReturnsCorrectCounts() {
        TransactionsDTO tool1 = new TransactionsDTO(1, 1, "John", "Doe", "john.doe@example.com", 1, "Hammer",
                null, null, null, ToolTransaction.TransactionType.borrow, ToolTransaction.Status.approved);
        TransactionsDTO tool2 = new TransactionsDTO(2, 2, "Jane", "Smith", "jane.smith@example.com", 2, "Wrench",
                null, null, null, ToolTransaction.TransactionType.borrow, ToolTransaction.Status.approved);
        TransactionsDTO tool3 = new TransactionsDTO(3, 1, "John", "Doe", "john.doe@example.com", 1, "Hammer",
                null, null, null, ToolTransaction.TransactionType.borrow, ToolTransaction.Status.approved);

        List<ToolTransaction> transactions = Arrays.asList(
                mockTransaction(tool1), mockTransaction(tool2), mockTransaction(tool3)
        );

        when(toolTransactionRepo.findAll()).thenReturn(transactions);

        Map<String, Integer> result = toolTransactionService.countAllTools();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get("Hammer"));
        assertEquals(1, result.get("Wrench"));
        verify(toolTransactionRepo, times(1)).findAll();
    }

    private ToolTransaction mockTransaction(TransactionsDTO dto) {
        ToolTransaction transaction = new ToolTransaction();
        transaction.setTransaction_id(dto.getTransaction_id());
        return transaction;
    }
}