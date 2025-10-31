package com.expense.splitter.service;

import com.expense.splitter.dto.ExpenseGroupDTO;
import com.expense.splitter.entity.ExpenseGroup;
import com.expense.splitter.entity.User;
import com.expense.splitter.exception.ResourceNotFoundException;
import com.expense.splitter.repository.ExpenseGroupRepository;
import com.expense.splitter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseGroupService {

    @Autowired
    private ExpenseGroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ExpenseGroupDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseGroupDTO getGroupById(Long id) {
        ExpenseGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseGroup", "id", id));
        return convertToDTO(group);
    }

    public List<ExpenseGroupDTO> getGroupsByUserId(Long userId) {
        return groupRepository.findAllByMemberId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new group - FIXED VERSION
     */
    public ExpenseGroupDTO createGroup(ExpenseGroupDTO groupDTO) {
        User creator = userRepository.findById(groupDTO.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", groupDTO.getCreatedById()));

        // Create new group entity
        ExpenseGroup group = new ExpenseGroup();
        group.setName(groupDTO.getName());
        group.setDescription(groupDTO.getDescription());
        group.setCreatedBy(creator);

        // Create a NEW HashSet for members to avoid concurrent modification
        Set<User> members = new HashSet<>();
        
        // Add creator first
        members.add(creator);
        
        // Add additional members if provided
        if (groupDTO.getMemberIds() != null && !groupDTO.getMemberIds().isEmpty()) {
            for (Long memberId : groupDTO.getMemberIds()) {
                // Skip if member is already the creator
                if (!memberId.equals(creator.getId())) {
                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", memberId));
                    members.add(member);
                }
            }
        }
        
        // Set the members at once (not one by one)
        group.setMembers(members);

        // Save and return
        ExpenseGroup savedGroup = groupRepository.save(group);
        return convertToDTO(savedGroup);
    }

    /**
     * Update group - FIXED VERSION
     */
    public ExpenseGroupDTO updateGroup(Long id, ExpenseGroupDTO groupDTO) {
        ExpenseGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseGroup", "id", id));

        group.setName(groupDTO.getName());
        group.setDescription(groupDTO.getDescription());

        // Update members with new HashSet
        if (groupDTO.getMemberIds() != null && !groupDTO.getMemberIds().isEmpty()) {
            Set<User> newMembers = new HashSet<>();
            
            // Always include creator
            newMembers.add(group.getCreatedBy());
            
            // Add other members
            for (Long memberId : groupDTO.getMemberIds()) {
                if (!memberId.equals(group.getCreatedBy().getId())) {
                    User member = userRepository.findById(memberId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", memberId));
                    newMembers.add(member);
                }
            }
            
            // Clear old members and set new ones
            group.getMembers().clear();
            group.setMembers(newMembers);
        }

        ExpenseGroup updatedGroup = groupRepository.save(group);
        return convertToDTO(updatedGroup);
    }

    public void deleteGroup(Long id) {
        ExpenseGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ExpenseGroup", "id", id));
        groupRepository.delete(group);
    }

    /**
     * Convert ExpenseGroup entity to DTO - FIXED VERSION
     */
    private ExpenseGroupDTO convertToDTO(ExpenseGroup group) {
        ExpenseGroupDTO dto = new ExpenseGroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreatedById(group.getCreatedBy().getId());
        dto.setCreatedByName(group.getCreatedBy().getName());
        
        // Safely convert members to IDs
        if (group.getMembers() != null && !group.getMembers().isEmpty()) {
            Set<Long> memberIds = new HashSet<>();
            for (User member : group.getMembers()) {
                memberIds.add(member.getId());
            }
            dto.setMemberIds(memberIds);
        }
        
        return dto;
    }
}
