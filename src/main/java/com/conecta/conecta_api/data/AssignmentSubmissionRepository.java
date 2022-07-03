package com.conecta.conecta_api.data;

import com.conecta.conecta_api.domain.entities.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    Collection<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    Collection<AssignmentSubmission> findByStudentId(Long studentId);

    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    void deleteAllByAssignmentId(Long assignmentId);

    void deleteAllByAssignmentCourseId(Long courseId);

    void deleteAllByStudentId(Long studentId);

    void deleteAllByStudentIdAndAssignmentCourseId(Long studentId, Long courseId);
}