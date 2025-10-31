package com.expense.splitter.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    
    private Long id;
    
    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount must have at most 8 digits and 2 decimal places")
    private BigDecimal amount;
    
    @NotNull(message = "Payer ID is required")
    private Long paidById;
    
    private String paidByName;
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    private LocalDateTime expenseDate;
    
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
    
    // NEW: List of user IDs who are splitting this expense
    // If null or empty, defaults to all group members
    private Set<Long> participantIds;
}
