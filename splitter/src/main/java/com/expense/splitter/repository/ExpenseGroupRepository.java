package com.expense.splitter.repository;

import com.expense.splitter.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {
    
    @Query("SELECT g FROM ExpenseGroup g JOIN g.members m WHERE m.id = :userId")
    List<ExpenseGroup> findAllByMemberId(@Param("userId") Long userId);
    
    List<ExpenseGroup> findAllByCreatedById(Long userId);
}
