package edu.cit.tooltrack.repository;

import edu.cit.tooltrack.entity.ToolTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.tools.Tool;
import java.sql.Timestamp;
import java.util.List;


@Repository
public interface ToolTransactionRepository extends JpaRepository<ToolTransaction, Integer> {


    @Query(value = "SELECT borrow_date FROM tool_transactions WHERE borrow_date >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 6 MONTH)", nativeQuery = true)
    List<Timestamp> getLastSixMonths();

    @Query(value = "SELECT borrow_date FROM tool_transactions WHERE YEAR(borrow_date) = :year", nativeQuery = true)
    List<Timestamp> getLastYear(@Param("year") int year);

    @Query(value = "SELECT borrow_date FROM tool_transactions", nativeQuery = true)
    List<Timestamp> getAllYear();

    @Query(value = "SELECT * FROM tool_transactions tt JOIN users u ON tt.fk_tool_transactions_user = u.user_id WHERE u.email = :email", nativeQuery = true)
    List<ToolTransaction> findTransactionsByEmail(@Param("email") String email);

} 
