package com.conecta.conecta_api.api;

import com.conecta.conecta_api.domain.dtos.AssignmentInfo;
import com.conecta.conecta_api.domain.entities.Assignment;
import com.conecta.conecta_api.security.utils.TokenUtils;
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
    private final TokenUtils jwtUtils;


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

    @PostMapping(path = "/assignments/create")
    public ResponseEntity<AssignmentInfo> getCoursesAssignments(@RequestBody AssignmentInfo assignmentInfo) {
        Assignment toSave = new Assignment();

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

        toSave.setTitle(assignmentInfo.getTitle());
        toSave.setTitle(assignmentInfo.getSubtitle());
        toSave.setTitle(assignmentInfo.getContent());
        toSave.setGrade(assignmentInfo.getGrade());
        toSave.setCreationDate(LocalDateTime.now());
        toSave.setDueDate(assignmentInfo.getDueDate());

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/assignments/create").toUriString());

        var assignment = assignmentService.saveAssignment(toSave);

        return ResponseEntity.created(uri).body(AssignmentInfo.fromAssignment(assignment));
    }

}
