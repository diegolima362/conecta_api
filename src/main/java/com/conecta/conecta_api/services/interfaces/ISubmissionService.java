package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.AssignmentSubmission;

import java.util.Collection;
import java.util.Optional;

public interface ISubmissionService {
    Collection<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId);

    AssignmentSubmission saveSubmission(AssignmentSubmission submission);

    void deleteSubmission(Long submissionId);

    void deleteAllByAssignmentId(Long assignmentId);

    Optional<AssignmentSubmission> findById(Long submissionId);

    Collection<AssignmentSubmission> getUserAssignments(Long studentId);

    Optional<AssignmentSubmission> getStudentSubmission(Long assignmentId, Long studentId);

    void deleteAllByCourseId(Long courseId);

    void deleteAllByUserIdAtCourse(Long userId, Long courseId);
}
