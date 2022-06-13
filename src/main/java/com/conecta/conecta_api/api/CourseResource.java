package com.conecta.conecta_api.api;

import com.conecta.conecta_api.domain.dtos.AppUserInfo;
import com.conecta.conecta_api.domain.dtos.CourseInfo;
import com.conecta.conecta_api.domain.entities.Course;
import com.conecta.conecta_api.domain.entities.CourseRegistration;
import com.conecta.conecta_api.security.utils.TokenUtils;
import com.conecta.conecta_api.services.AppUserService;
import com.conecta.conecta_api.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.conecta.conecta_api.api.AppUserResource.getTokenInfo;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class CourseResource {
    private final CourseService courseService;
    private final AppUserService userService;
    private final TokenUtils jwtUtils;

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

    @GetMapping(path = "/courses/{courseId}/students")
    public ResponseEntity<List<AppUserInfo>> getCourseRegistrations(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(
                courseService.getCourseRegistrations(courseId)
                        .stream()
                        .map(r -> AppUserInfo.fromAppUser(r.getStudent()))
                        .collect(Collectors.toList()));

    }

    @PostMapping(path = "/courses/register")
    public ResponseEntity<CourseInfo> saveCourse(@RequestBody CourseInfo course) {
        Course toSave = new Course();
        toSave.setCode(course.getCode());
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

    TokenInfo extractInfo(String authorizationHeader) {
        return getTokenInfo(authorizationHeader, jwtUtils);
    }
}



