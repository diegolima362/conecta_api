package com.conecta.conecta_api.data;

import com.conecta.conecta_api.domain.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Collection<Assignment> findAllByCourseId(Long courseId);
}
