package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.AssignmentSubmissionRepository;
import com.conecta.conecta_api.domain.entities.AssignmentSubmission;
import com.conecta.conecta_api.services.interfaces.ISubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService implements ISubmissionService {
    private final AssignmentSubmissionRepository submissionRepository;

    @Override
    public Collection<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    @Override
    public AssignmentSubmission saveSubmission(AssignmentSubmission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public void deleteSubmission(Long submissionId) {
        submissionRepository.deleteById(submissionId);
    }

    @Override
    public void deleteAllByAssignmentId(Long assignmentId) {
        submissionRepository.deleteAllByAssignmentId(assignmentId);
    }

    @Override
    public Optional<AssignmentSubmission> findById(Long submissionId) {
        return submissionRepository.findById(submissionId);
    }

    @Override
    public Collection<AssignmentSubmission> getUserAssignments(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<AssignmentSubmission> getStudentSubmission(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    @Override
    public void deleteAllByCourseId(Long courseId) {
        submissionRepository.deleteAllByAssignmentCourseId(courseId);
    }

    @Override
    public void deleteAllByUserIdAtCourse(Long userId, Long courseId) {
        submissionRepository.deleteAllByStudentIdAndAssignmentCourseId(userId, courseId);
    }
}
