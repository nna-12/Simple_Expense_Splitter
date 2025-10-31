package com.expense.splitter.repository;

import com.expense.splitter.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    List<Expense> findAllByGroupId(Long groupId);
    
    List<Expense> findAllByPaidById(Long userId);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.paidBy.id = :userId AND e.group.id = :groupId")
    BigDecimal sumAmountByUserAndGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
