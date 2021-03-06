package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.AssignmentRepository;
import com.conecta.conecta_api.domain.entities.Assignment;
import com.conecta.conecta_api.services.interfaces.IAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService implements IAssignmentService {
    private final AssignmentRepository assignmentRepository;

    @Override
    public Collection<Assignment> getCourseAssignments(Long courseId) {
        return assignmentRepository.findAllByCourseId(courseId);
    }

    @Override
    public Optional<Assignment> getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId);
    }

    @Override
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    public void deleteAssignment(Long assignmentId) {
        assignmentRepository.deleteById(assignmentId);
    }

    @Override
    public void deleteAllByCourseId(Long courseId) {
        assignmentRepository.deleteAllByCourseId(courseId);
    }

}
