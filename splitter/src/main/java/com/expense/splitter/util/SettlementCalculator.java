package com.expense.splitter.util;

import com.expense.splitter.dto.SettlementDTO;
import com.expense.splitter.entity.Expense;
import com.expense.splitter.entity.ExpenseGroup;
import com.expense.splitter.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Utility class to calculate settlements between users
 * Now supports selective participants per expense
 */
@Component
public class SettlementCalculator {

    /**
     * Calculate settlements considering each expense's specific participants
     */
    public List<SettlementDTO> calculateSettlements(ExpenseGroup group, List<Expense> expenses) {
        // Track balance for each user across all expenses
        Map<User, BigDecimal> balances = new HashMap<>();
        
        // Initialize balances to zero for all group members
        for (User member : group.getMembers()) {
            balances.put(member, BigDecimal.ZERO);
        }

        // Process each expense individually
        for (Expense expense : expenses) {
            Set<User> participants = expense.getParticipants();
            
            // If no participants specified, default to all group members
            if (participants == null || participants.isEmpty()) {
                participants = group.getMembers();
            }
            
            // Calculate per-person share for THIS expense
            int participantCount = participants.size();
            BigDecimal perPersonShare = expense.getAmount().divide(
                    BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP);
            
            // The payer gets credited the full amount
            User paidBy = expense.getPaidBy();
            balances.put(paidBy, balances.get(paidBy).add(expense.getAmount()));
            
            // Each participant gets debited their share
            for (User participant : participants) {
                balances.put(participant, balances.get(participant).subtract(perPersonShare));
            }
        }

        // Now settle up based on final balances
        List<Map.Entry<User, BigDecimal>> debtors = new ArrayList<>();
        List<Map.Entry<User, BigDecimal>> creditors = new ArrayList<>();

        for (Map.Entry<User, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                // Negative balance = owes money
                debtors.add(new AbstractMap.SimpleEntry<>(
                    entry.getKey(), 
                    entry.getValue().abs()
                ));
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                // Positive balance = is owed money
                creditors.add(new AbstractMap.SimpleEntry<>(
                    entry.getKey(), 
                    entry.getValue()
                ));
            }
        }

        // Sort for optimal matching
        debtors.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        creditors.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Generate settlements
        List<SettlementDTO> settlements = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            Map.Entry<User, BigDecimal> debtor = debtors.get(i);
            Map.Entry<User, BigDecimal> creditor = creditors.get(j);
            
            BigDecimal settlementAmount = debtor.getValue().min(creditor.getValue());

            if (settlementAmount.compareTo(BigDecimal.ZERO) > 0) {
                settlements.add(new SettlementDTO(
                        debtor.getKey().getId(),
                        debtor.getKey().getName(),
                        creditor.getKey().getId(),
                        creditor.getKey().getName(),
                        settlementAmount
                ));
            }

            BigDecimal remainingDebt = debtor.getValue().subtract(settlementAmount);
            BigDecimal remainingCredit = creditor.getValue().subtract(settlementAmount);

            debtor.setValue(remainingDebt);
            creditor.setValue(remainingCredit);

            if (remainingDebt.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            if (remainingCredit.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        return settlements;
    }
}
