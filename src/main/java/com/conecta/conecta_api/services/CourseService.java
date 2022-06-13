package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.CourseRepository;
import com.conecta.conecta_api.data.RegistrationRepository;
import com.conecta.conecta_api.domain.entities.AppUser;
import com.conecta.conecta_api.domain.entities.Course;
import com.conecta.conecta_api.domain.entities.CourseRegistration;
import com.conecta.conecta_api.services.interfaces.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseService implements ICourseService {
    private final CourseRepository courseRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    public List<Course> getCourses() {
        log.info("Buscando todos os Cursos.");

        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCourses(AppUser user) {
        log.info("Buscando todos os Cursos do usuario {}.", user.getUsername());

        var courses = user.getTeachingCourses();
        var registrations = user.getRegistrations().stream().map(CourseRegistration::getCourse).toList();

        courses.addAll(registrations);

        return courses.stream().toList();
    }

    @Override
    public Course saveCourse(Course course) {
        log.info("Salvando novo curso {} no banco de dados.", course.getName());
        return courseRepository.save(course);
    }

    @Override
    public Optional<Course> getCourse(Long courseId) {
        log.info("Buscando o curso {}.", courseId);
        return courseRepository.findById(courseId);
    }

    @Override
    public Optional<Course> getCourseByCode(String code) {
        log.info("Buscando o curso {}.", code);

        return courseRepository.findByCode(code);
    }

    @Override
    public void joinCourse(String code, AppUser user) {
        log.info("Buscando o curso {}.", code);
        Optional<Course> course = courseRepository.findByCode(code);

        if (course.isPresent()) {
            log.info("Adicionando user {} ao curso {}.", user.getUsername(), code);
            var registration = new CourseRegistration();
            registration.setCourse(course.get());
            registration.setStudent(user);
            registration.setRegisteredAt(LocalDateTime.now());

            registrationRepository.save(registration);

            course.get().getRegistrations().add(registration);
        }
    }

    @Override
    public List<CourseRegistration> getCourseRegistrations(Long courseId) {
        log.info("Buscando o curso {}.", courseId);

        var course = courseRepository.findById(courseId);

        if (course.isEmpty())
            return new ArrayList<>();

        return course.get().getRegistrations();
    }
}
