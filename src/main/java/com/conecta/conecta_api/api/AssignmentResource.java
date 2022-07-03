package com.conecta.conecta_api.api;

import com.conecta.conecta_api.domain.dtos.AssignmentInfo;
import com.conecta.conecta_api.domain.dtos.AssignmentSubmissionInfo;
import com.conecta.conecta_api.domain.entities.Assignment;
import com.conecta.conecta_api.domain.entities.AssignmentSubmission;
import com.conecta.conecta_api.domain.entities.AssignmentSubmissionStatus;
import com.conecta.conecta_api.domain.entities.Course;
import com.conecta.conecta_api.security.utils.TokenUtils;
import com.conecta.conecta_api.services.interfaces.IAppUserService;
import com.conecta.conecta_api.services.interfaces.IAssignmentService;
import com.conecta.conecta_api.services.interfaces.ICourseService;
import com.conecta.conecta_api.services.interfaces.ISubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.conecta.conecta_api.api.AppUserResource.getTokenInfo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class AssignmentResource {
    private final IAssignmentService assignmentService;

    private final IAppUserService userService;
    private final ICourseService courseService;
    private final IAppUserService appUserService;
    private final ISubmissionService submissionService;

    private final TokenUtils jwtUtils;

    @GetMapping(path = "/courses/{courseId}/assignments")
    public ResponseEntity<List<AssignmentInfo>> getCoursesAssignments(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(assignmentService.getCourseAssignments(courseId).stream()
                .map(AssignmentInfo::fromAssignment).collect(Collectors.toList()));
    }

    @GetMapping(path = "/assignments/{assignmentId}")
    public ResponseEntity<AssignmentInfo> getAssignment(@PathVariable Long assignmentId) {
        var optionalAssignment = assignmentService.getAssignment(assignmentId);
        if (optionalAssignment.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok().body(AssignmentInfo.fromAssignment(optionalAssignment.get()));
    }

    @DeleteMapping(path = "/assignments/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long assignmentId) {
        submissionService.deleteAllByAssignmentId(assignmentId);
        assignmentService.deleteAssignment(assignmentId);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/assignments/{assignmentId}/submissions")
    public ResponseEntity<List<AssignmentSubmissionInfo>> getAssignmentsSubmissions(@PathVariable Long assignmentId) {
        return ResponseEntity.ok().body(submissionService.getAssignmentSubmissions(assignmentId).stream()
                .map(AssignmentSubmissionInfo::fromSubmission).collect(Collectors.toList()));
    }

    @PutMapping(path = "/assignments/{assignmentId}")
    public ResponseEntity<AssignmentInfo> editAssignment(@RequestBody AssignmentInfo assignmentInfo,
                                                         @PathVariable Long assignmentId) {
        var optionalAssignment = assignmentService.getAssignment(assignmentId);
        if (optionalAssignment.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        var assignment = optionalAssignment.get();

        assignment.setEditDate(LocalDateTime.now());

        assignment.setTitle(assignmentInfo.getTitle());
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
        toSave.setContent(assignmentInfo.getContent());
        toSave.setGrade(assignmentInfo.getGrade());
        toSave.setDueDate(assignmentInfo.getDueDate());
        toSave.setCreationDate(LocalDateTime.now());

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

        URI uri = URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/assignments/create").toUriString());

        var assignment = assignmentService.saveAssignment(toSave);

        createStudentsAssignment(course.get(), assignment);

        // if (createPost) createPostFromAssignment(assignment);

        return ResponseEntity.created(uri).body(AssignmentInfo.fromAssignment(assignment));
    }

    private void createStudentsAssignment(Course course, Assignment assignment) {
        var students = course.getRegistrations();
        for (var i : students) {
            var submission = new AssignmentSubmission();
            submission.setStatus(AssignmentSubmissionStatus.PENDING);
            submission.setStudent(i.getStudent());
            submission.setAssignment(assignment);
            submissionService.saveSubmission(submission);
        }
    }

    @PostMapping(path = "/assignments/{assignmentId}/submit")
    public ResponseEntity<AssignmentSubmissionInfo> submit(
            HttpServletRequest request,
            @PathVariable Long assignmentId,
            @RequestBody String content) {
        TokenInfo info = getTokenInfo(request.getHeader(AUTHORIZATION), jwtUtils);
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var assignment = assignmentService.getAssignment(assignmentId);
        if (assignment.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        var optionalSubmission = submissionService.getStudentSubmission(assignmentId, user.get().getId());
        if (optionalSubmission.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        var submission = optionalSubmission.get();

        var dueDate = assignment.get().getDueDate();
        var doneDate = LocalDateTime.now();
        if (doneDate.isAfter(dueDate)) {
            submission.setStatus(AssignmentSubmissionStatus.DONE_LATE);
        } else {
            submission.setStatus(AssignmentSubmissionStatus.DONE);
        }
        submission.setFinishDate(doneDate);
        submission.setContent(content);

        URI uri = URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/assignments/submit").toUriString());

        var saved = submissionService.saveSubmission(submission);

        return ResponseEntity.created(uri).body(AssignmentSubmissionInfo.fromSubmission(saved));
    }

    @GetMapping(path = "/assignments/{assignmentId}/submission")
    public ResponseEntity<AssignmentSubmissionInfo> getUserSubmission(
            HttpServletRequest request,
            @PathVariable Long assignmentId) {
        TokenInfo info = getTokenInfo(request.getHeader(AUTHORIZATION), jwtUtils);
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return submissionService.getStudentSubmission(assignmentId, user.get().getId())
                .map(submission -> ResponseEntity.ok().body(AssignmentSubmissionInfo.fromSubmission(submission)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/users/me/assignments")
    public ResponseEntity<List<AssignmentSubmissionInfo>> getUserAssignments(HttpServletRequest request) {
        TokenInfo info = getTokenInfo(request.getHeader(AUTHORIZATION), jwtUtils);
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .body(submissionService
                        .getUserAssignments(user
                                .get()
                                .getId())
                        .stream()
                        .map(AssignmentSubmissionInfo::fromSubmission)
                        .collect(Collectors.toList()));
    }

    @PostMapping(path = "/assignments/{assignmentId}/complete")
    public ResponseEntity<?> markAsDone(HttpServletRequest request, @PathVariable Long assignmentId) {
        TokenInfo info = getTokenInfo(request.getHeader(AUTHORIZATION), jwtUtils);
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var assignment = assignmentService.getAssignment(assignmentId);
        if (assignment.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        var optionalSubmission = submissionService.getStudentSubmission(assignmentId, user.get().getId());
        if (optionalSubmission.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        var submission = optionalSubmission.get();

        var dueDate = assignment.get().getDueDate();
        var doneDate = LocalDateTime.now();
        if (doneDate.isAfter(dueDate)) {
            submission.setStatus(AssignmentSubmissionStatus.DONE_LATE);
        } else {
            submission.setStatus(AssignmentSubmissionStatus.DONE);
        }
        submission.setFinishDate(doneDate);

        submissionService.saveSubmission(submission);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/assignments/{assignmentId}/cancel")
    public ResponseEntity<?> cancelSubmission(HttpServletRequest request, @PathVariable Long assignmentId) {
        TokenInfo info = getTokenInfo(request.getHeader(AUTHORIZATION), jwtUtils);
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var optionalSubmission = submissionService.getStudentSubmission(assignmentId, user.get().getId());
        if (optionalSubmission.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        var toSave = optionalSubmission.get();

        toSave.setStatus(AssignmentSubmissionStatus.PENDING);
        toSave.setContent(null);
        toSave.setFinishDate(null);
        toSave.setGrade(null);

        submissionService.saveSubmission(toSave);

        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/assignments/submissions/{submissionId}/return")
    public ResponseEntity<AssignmentSubmissionInfo> returnSubmission(
            @PathVariable Long submissionId,
            @RequestBody AssignmentSubmissionInfo submissionInfo) {
        var optionalSubmission = submissionService.findById(submissionId);
        if (optionalSubmission.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        var submission = optionalSubmission.get();

        submission.setGrade(submissionInfo.getGrade());
        submission.setStatus(AssignmentSubmissionStatus.RETURNED);

        var saved = submissionService.saveSubmission(submission);

        return ResponseEntity.ok().body(AssignmentSubmissionInfo.fromSubmission(saved));
    }

    @DeleteMapping(path = "/assignments/submissions/{submissionId}")
    public ResponseEntity<?> deleteSubmission(@PathVariable Long submissionId) {
        submissionService.deleteSubmission(submissionId);
        return ResponseEntity.ok().build();
    }

}