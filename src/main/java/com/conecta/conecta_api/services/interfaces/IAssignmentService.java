package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.Assignment;

import java.util.Collection;

public interface IAssignmentService {
    Collection<Assignment> getCourseAssignments(Long courseId);

    Assignment saveAssignment(Assignment assignment);

    void deleteAssignment(Long assignmentId);
}
