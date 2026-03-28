package com.socialmediaagent.repository;

import com.socialmediaagent.domain.enums.WorkflowStatus;
import com.socialmediaagent.domain.model.ApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {

    Optional<ApprovalWorkflow> findByPostId(Long postId);

    List<ApprovalWorkflow> findByAssignedToAndStatus(Long assignedTo, WorkflowStatus status);

    List<ApprovalWorkflow> findByStatus(WorkflowStatus status);

    Long countByAssignedToAndStatus(Long assignedTo, WorkflowStatus status);
}
