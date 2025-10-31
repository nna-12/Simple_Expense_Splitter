package com.expense.splitter.service;

import com.expense.splitter.dto.ExpenseDTO;
import com.expense.splitter.entity.Expense;
import com.expense.splitter.entity.ExpenseGroup;
import com.expense.splitter.entity.User;
import com.expense.splitter.repository.ExpenseGroupRepository;
import com.expense.splitter.repository.ExpenseRepository;
import com.expense.splitter.repository.UserRepository;
import com.expense.splitter.util.SettlementCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExpenseService
 */
@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private SettlementCalculator settlementCalculator;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private ExpenseGroup testGroup;
    private Expense testExpense;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testGroup = new ExpenseGroup();
        testGroup.setId(1L);
        testGroup.setName("Trip Group");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setDescription("Hotel");
        testExpense.setAmount(new BigDecimal("200.00"));
        testExpense.setPaidBy(testUser);
        testExpense.setGroup(testGroup);
    }

    @Test
    public void testCreateExpense() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setDescription("Hotel");
        expenseDTO.setAmount(new BigDecimal("200.00"));
        expenseDTO.setPaidById(1L);
        expenseDTO.setGroupId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        ExpenseDTO result = expenseService.createExpense(expenseDTO);

        assertNotNull(result);
        assertEquals("Hotel", result.getDescription());
        assertEquals(new BigDecimal("200.00"), result.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
}
