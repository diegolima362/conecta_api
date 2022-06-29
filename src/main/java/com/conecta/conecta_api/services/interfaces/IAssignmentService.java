package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.Assignment;

import java.util.Collection;
import java.util.Optional;

public interface IAssignmentService {
    Collection<Assignment> getCourseAssignments(Long courseId);

    Optional<Assignment> getAssignment(Long assignmentId);

    Assignment saveAssignment(Assignment assignment);

    void deleteAssignment(Long assignmentId);
}
