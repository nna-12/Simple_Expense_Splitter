package com.expense.splitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseGroupDTO {
    
    private Long id;
    
    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 255, message = "Group name must be between 3 and 255 characters")
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Creator ID is required")
    private Long createdById;
    
    private String createdByName;
    
    private Set<Long> memberIds;
}
