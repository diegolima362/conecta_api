package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.AssignmentRepository;
import com.conecta.conecta_api.domain.entities.Assignment;
import com.conecta.conecta_api.services.interfaces.IAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AssignmentService implements IAssignmentService {
    private final AssignmentRepository assignmentRepository;

    @Override
    public Collection<Assignment> getCourseAssignments(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    @Override
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    public void deleteAssignment(Long assignmentId) {
        assignmentRepository.deleteById(assignmentId);
    }
}
