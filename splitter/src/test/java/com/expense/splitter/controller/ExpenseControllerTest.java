package com.expense.splitter.controller;

import com.expense.splitter.dto.ExpenseDTO;
import com.expense.splitter.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Simple unit test for ExpenseController without web layer
 */
@ExtendWith(MockitoExtension.class)
public class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    public void testGetAllExpenses() {
        // Arrange
        ExpenseDTO expense1 = new ExpenseDTO();
        expense1.setId(1L);
        expense1.setDescription("Lunch");
        expense1.setAmount(new BigDecimal("50.00"));

        List<ExpenseDTO> expenses = Arrays.asList(expense1);
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        // Act
        ResponseEntity<List<ExpenseDTO>> response = expenseController.getAllExpenses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Lunch", response.getBody().get(0).getDescription());
        verify(expenseService, times(1)).getAllExpenses();
    }

    @Test
    public void testCreateExpense() {
        // Arrange
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setDescription("Coffee");
        expenseDTO.setAmount(new BigDecimal("15.50"));
        expenseDTO.setPaidById(1L);
        expenseDTO.setGroupId(1L);

        ExpenseDTO savedExpense = new ExpenseDTO();
        savedExpense.setId(1L);
        savedExpense.setDescription("Coffee");
        savedExpense.setAmount(new BigDecimal("15.50"));

        when(expenseService.createExpense(any(ExpenseDTO.class))).thenReturn(savedExpense);

        // Act
        ResponseEntity<ExpenseDTO> response = expenseController.createExpense(expenseDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Coffee", response.getBody().getDescription());
        verify(expenseService, times(1)).createExpense(any(ExpenseDTO.class));
    }
}
