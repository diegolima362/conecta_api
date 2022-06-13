package com.conecta.conecta_api.api;

import com.conecta.conecta_api.domain.dtos.AssignmentInfo;
import com.conecta.conecta_api.domain.entities.Assignment;
import com.conecta.conecta_api.services.AppUserService;
import com.conecta.conecta_api.services.AssignmentService;
import com.conecta.conecta_api.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class AssignmentResource {
    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final AppUserService appUserService;

    @GetMapping(path = "/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentInfo>> getCoursesAssignments(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(
                assignmentService.getCourseAssignments(courseId)
                        .stream()
                        .map(AssignmentInfo::fromAssignment)
                        .collect(Collectors.toList()));
    }

    @DeleteMapping(path = "/assignments/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long assignmentId) {
        assignmentService.deleteAssignment(assignmentId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/assignments/{assignmentId}")
    public ResponseEntity<AssignmentInfo> editAssignment(@RequestBody AssignmentInfo assignmentInfo, @PathVariable Long assignmentId) {
        var optionalAssignment = assignmentService.getAssignment(assignmentId);
        if (optionalAssignment.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        var assignment = optionalAssignment.get();

        assignment.setEditDate(LocalDateTime.now());

        assignment.setTitle(assignmentInfo.getTitle());
        assignment.setSubtitle(assignmentInfo.getSubtitle());
        assignment.setContent(assignmentInfo.getContent());
        assignment.setGrade(assignmentInfo.getGrade());
        assignment.setDueDate(assignmentInfo.getDueDate());

        var saved = assignmentService.saveAssignment(assignment);

        return ResponseEntity.ok().body(AssignmentInfo.fromAssignment(saved));
    }

    @PostMapping(path = "/assignments/create")
    public ResponseEntity<AssignmentInfo> createAssignment(@RequestBody AssignmentInfo assignmentInfo) {
        Assignment toSave = new Assignment();

        toSave.setTitle(assignmentInfo.getTitle());
        toSave.setSubtitle(assignmentInfo.getSubtitle());
        toSave.setContent(assignmentInfo.getContent());
        toSave.setGrade(assignmentInfo.getGrade());
        toSave.setDueDate(assignmentInfo.getDueDate());

        var course = courseService.getCourse(assignmentInfo.getCourseId());
        if (course.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setCourse(course.get());

        var professor = appUserService.getUserById(assignmentInfo.getProfessorId());
        if (professor.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setProfessor(professor.get());

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/assignments/create").toUriString());

        var assignment = assignmentService.saveAssignment(toSave);

        return ResponseEntity.created(uri).body(AssignmentInfo.fromAssignment(assignment));
    }


}