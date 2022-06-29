package com.conecta.conecta_api.api;

import com.conecta.conecta_api.domain.dtos.CourseInfo;
import com.conecta.conecta_api.domain.dtos.CourseRegistrationInfo;
import com.conecta.conecta_api.domain.entities.Course;
import com.conecta.conecta_api.domain.entities.CourseRegistration;
import com.conecta.conecta_api.security.utils.TokenUtils;
import com.conecta.conecta_api.services.interfaces.IAppUserService;
import com.conecta.conecta_api.services.interfaces.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.conecta.conecta_api.api.AppUserResource.getTokenInfo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
@RequiredArgsConstructor
public class CourseResource {
    private final ICourseService courseService;
    private final IAppUserService userService;
    private final TokenUtils jwtUtils;

    public static String generateCode() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .toUpperCase();
    }

    @GetMapping(path = "/courses")
    public ResponseEntity<List<CourseInfo>> getCourses() {
        return ResponseEntity
                .ok()
                .body(courseService
                        .getCourses()
                        .stream()
                        .map(CourseInfo::fromCourse)
                        .collect(Collectors.toList()));
    }

    @GetMapping(path = "/users/me/courses")
    public ResponseEntity<List<CourseInfo>> getMyCourses(HttpServletRequest request) {
        TokenInfo info = extractInfo(request.getHeader(AUTHORIZATION));
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .ok()
                .body(courseService
                        .getCourses(user.get())
                        .stream()
                        .map(CourseInfo::fromCourse)
                        .collect(Collectors.toList()));
    }

    @PostMapping(path = "/courses/{code}/join")
    public ResponseEntity<CourseRegistration> joinCourse(HttpServletRequest request, @PathVariable String code) {
        TokenInfo info = extractInfo(request.getHeader(AUTHORIZATION));
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        courseService.joinCourse(code, user.get());

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/courses/{courseId}")
    public ResponseEntity<CourseInfo> getCourse(@PathVariable Long courseId) {
        return courseService.getCourse(courseId)
                .map(course -> ResponseEntity
                        .ok()
                        .body(CourseInfo.fromCourse(course)))
                .orElseGet(() -> ResponseEntity
                        .notFound()
                        .build());
    }

    @GetMapping(path = "/courses/{courseId}/registrations")
    public ResponseEntity<List<CourseRegistrationInfo>> getCourseRegistrations(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(
                courseService.getCourseRegistrations(courseId)
                        .stream()
                        .map(CourseRegistrationInfo::fromCourseRegistration)
                        .collect(Collectors.toList()));

    }

    @PostMapping(path = "/courses/{courseId}/registrations/{studentId}")
    public ResponseEntity<CourseRegistrationInfo> registerStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        var student = userService.getUserById(studentId);
        if (student.isEmpty())
            return ResponseEntity.notFound().build();

        try {
            var result = courseService.registerStudent(courseId, student.get());
            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity
                    .ok()
                    .body(CourseRegistrationInfo.fromCourseRegistration(result.get()));

        } catch (EntityNotFoundException e) {
            log.error("Error ao registrar aluno: " + e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "/courses/{courseId}/registrations/{registrationId}")
    public ResponseEntity<CourseRegistrationInfo> removeStudent(@PathVariable Long courseId, @PathVariable Long registrationId) {
        try {
            var result = courseService.removeStudent(courseId, registrationId);
            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity
                    .ok()
                    .body(CourseRegistrationInfo.fromCourseRegistration(result.get()));

        } catch (EntityNotFoundException e) {
            log.error("Erro ao remover aluno: " + e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(path = "/courses/register")
    public ResponseEntity<CourseInfo> saveCourse(@RequestBody CourseInfo course) {
        Course toSave = new Course();
        toSave.setCode(generateCode());
        toSave.setName(course.getName());

        var professor = userService.getUserById(course.getProfessorId());

        if (professor.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setProfessor(professor.get());

        var courseInfo = CourseInfo.fromCourse(courseService.saveCourse(toSave));

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/courses/register").toUriString());

        return ResponseEntity.created(uri).body(courseInfo);
    }

    @PutMapping(path = "/courses/{courseId}")
    public ResponseEntity<CourseInfo> editCourse(@RequestBody CourseInfo courseInfo, @PathVariable Long courseId) {
        var optionalCourse = courseService.getCourse(courseId);
        if (optionalCourse.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var course = optionalCourse.get();

        course.setName(courseInfo.getName());

        var saved = courseService.saveCourse(course);

        return ResponseEntity.ok().body(CourseInfo.fromCourse(saved));
    }

    @DeleteMapping(path = "/courses/{courseId}")
    public ResponseEntity<CourseInfo> deleteCourse(@PathVariable Long courseId) {
        try {
            var result = courseService.deleteCourse(courseId);

            return result
                    .map(course -> ResponseEntity.ok().body(CourseInfo.fromCourse(course)))
                    .orElseGet(() -> ResponseEntity.unprocessableEntity().build());
        } catch (Exception e) {
            log.error("Error ao deletar curso: %s".formatted(e.toString()));
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    TokenInfo extractInfo(String authorizationHeader) {
        return getTokenInfo(authorizationHeader, jwtUtils);
    }
}



