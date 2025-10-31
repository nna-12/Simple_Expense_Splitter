package com.expense.splitter.service;

import com.expense.splitter.dto.ExpenseDTO;
import com.expense.splitter.dto.SettlementDTO;
import com.expense.splitter.entity.Expense;
import com.expense.splitter.entity.ExpenseGroup;
import com.expense.splitter.entity.User;
import com.expense.splitter.exception.BadRequestException;
import com.expense.splitter.exception.ResourceNotFoundException;
import com.expense.splitter.repository.ExpenseGroupRepository;
import com.expense.splitter.repository.ExpenseRepository;
import com.expense.splitter.repository.UserRepository;
import com.expense.splitter.util.SettlementCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseGroupRepository groupRepository;

    @Autowired
    private SettlementCalculator settlementCalculator;

    public List<ExpenseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        return convertToDTO(expense);
    }

    public List<ExpenseDTO> getExpensesByGroupId(Long groupId) {
        return expenseRepository.findAllByGroupId(groupId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new expense with selective participants
     */
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        User paidBy = userRepository.findById(expenseDTO.getPaidById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", expenseDTO.getPaidById()));

        ExpenseGroup group = groupRepository.findById(expenseDTO.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseGroup", "id", expenseDTO.getGroupId()));

        Expense expense = new Expense();
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setPaidBy(paidBy);
        expense.setGroup(group);
        expense.setCategory(expenseDTO.getCategory());
        expense.setExpenseDate(expenseDTO.getExpenseDate() != null ? 
                expenseDTO.getExpenseDate() : LocalDateTime.now());

        // Handle participants
        Set<User> participants = new HashSet<>();
        
        if (expenseDTO.getParticipantIds() != null && !expenseDTO.getParticipantIds().isEmpty()) {
            // Use specified participants
            for (Long participantId : expenseDTO.getParticipantIds()) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", participantId));
                
                // Verify participant is a member of the group
                if (!group.getMembers().contains(participant)) {
                    throw new BadRequestException("User " + participant.getName() + 
                            " is not a member of group " + group.getName());
                }
                participants.add(participant);
            }
            
            // Always include the payer as a participant
            if (!participants.contains(paidBy)) {
                participants.add(paidBy);
            }
        } else {
            // Default to all group members if no participants specified
            participants = new HashSet<>(group.getMembers());
        }

        expense.setParticipants(participants);

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    /**
     * Update expense with participants
     */
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setCategory(expenseDTO.getCategory());

        if (expenseDTO.getPaidById() != null) {
            User paidBy = userRepository.findById(expenseDTO.getPaidById())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", expenseDTO.getPaidById()));
            expense.setPaidBy(paidBy);
        }

        // Update participants if provided
        if (expenseDTO.getParticipantIds() != null && !expenseDTO.getParticipantIds().isEmpty()) {
            Set<User> newParticipants = new HashSet<>();
            ExpenseGroup group = expense.getGroup();
            
            for (Long participantId : expenseDTO.getParticipantIds()) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", participantId));
                
                if (!group.getMembers().contains(participant)) {
                    throw new BadRequestException("User " + participant.getName() + 
                            " is not a member of group " + group.getName());
                }
                newParticipants.add(participant);
            }
            
            // Ensure payer is included
            if (!newParticipants.contains(expense.getPaidBy())) {
                newParticipants.add(expense.getPaidBy());
            }
            
            expense.setParticipants(newParticipants);
        }

        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        expenseRepository.delete(expense);
    }

    /**
     * Calculate settlements for a group
     */
    public List<SettlementDTO> calculateSettlements(Long groupId) {
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseGroup", "id", groupId));

        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);
        return settlementCalculator.calculateSettlements(group, expenses);
    }

    /**
     * Convert Expense entity to DTO
     */
    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setPaidById(expense.getPaidBy().getId());
        dto.setPaidByName(expense.getPaidBy().getName());
        dto.setGroupId(expense.getGroup().getId());
        dto.setCategory(expense.getCategory());
        dto.setExpenseDate(expense.getExpenseDate());
        
        // Include participant IDs
        if (expense.getParticipants() != null && !expense.getParticipants().isEmpty()) {
            Set<Long> participantIds = expense.getParticipants().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            dto.setParticipantIds(participantIds);
        }
        
        return dto;
    }
}
