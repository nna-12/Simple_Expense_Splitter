package com.expense.splitter.controller;

import com.expense.splitter.dto.ExpenseGroupDTO;
import com.expense.splitter.service.ExpenseGroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for ExpenseGroup CRUD operations
 */
@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class ExpenseGroupController {

    @Autowired
    private ExpenseGroupService groupService;

    /**
     * Get all groups
     * GET /api/groups
     */
    @GetMapping
    public ResponseEntity<List<ExpenseGroupDTO>> getAllGroups() {
        List<ExpenseGroupDTO> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * Get group by ID
     * GET /api/groups/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseGroupDTO> getGroupById(@PathVariable Long id) {
        ExpenseGroupDTO group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    /**
     * Get groups by user ID
     * GET /api/groups/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseGroupDTO>> getGroupsByUserId(@PathVariable Long userId) {
        List<ExpenseGroupDTO> groups = groupService.getGroupsByUserId(userId);
        return ResponseEntity.ok(groups);
    }

    /**
     * Create new group
     * POST /api/groups
     */
    @PostMapping
    public ResponseEntity<ExpenseGroupDTO> createGroup(@Valid @RequestBody ExpenseGroupDTO groupDTO) {
        ExpenseGroupDTO createdGroup = groupService.createGroup(groupDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    /**
     * Update group
     * PUT /api/groups/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseGroupDTO> updateGroup(@PathVariable Long id, 
                                                        @Valid @RequestBody ExpenseGroupDTO groupDTO) {
        ExpenseGroupDTO updatedGroup = groupService.updateGroup(id, groupDTO);
        return ResponseEntity.ok(updatedGroup);
    }

    /**
     * Delete group
     * DELETE /api/groups/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
