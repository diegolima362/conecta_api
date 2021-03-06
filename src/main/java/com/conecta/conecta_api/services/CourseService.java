package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.CourseRepository;
import com.conecta.conecta_api.data.FeedRepository;
import com.conecta.conecta_api.data.RegistrationRepository;
import com.conecta.conecta_api.domain.entities.AppUser;
import com.conecta.conecta_api.domain.entities.Course;
import com.conecta.conecta_api.domain.entities.CourseRegistration;
import com.conecta.conecta_api.services.interfaces.ICourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
    private final FeedRepository feedRepository;

    @Override
    public List<Course> getCourses() {
        log.info("Buscando todos os Cursos.");

        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCourses(AppUser user) {
        log.info("Buscando todos os Cursos do usuario {}.", user.getUsername());

        var courses = user.getTeachingCourses();
        var registrations = user.getRegistrations()
                .stream()
                .map(CourseRegistration::getCourse)
                .toList();

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
            var contains = course.get()
                    .getRegistrations()
                    .stream()
                    .anyMatch(e -> e.getStudent()
                            .getId()
                            .equals(user.getId()));

            if (contains) {
                log.info("Aluno {} j?? registrado no curso {}.", user.getUsername(), code);
                return;
            }

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
    public void leaveCourse(Long courseId, Long studentId) {
        log.info("Buscando o curso {}.", courseId);

        registrationRepository.deleteByCourseIdAndStudentId(courseId, studentId);
    }

    @Override
    public List<CourseRegistration> getCourseRegistrations(Long courseId) {
        log.info("Buscando o curso {}.", courseId);

        var course = courseRepository.findById(courseId);

        if (course.isEmpty()) return new ArrayList<>();

        return course.get().getRegistrations();
    }

    @Override
    public Optional<Course> deleteCourse(Long courseId) {
        log.info("Buscando o curso {}.", courseId);

        var course = courseRepository.findById(courseId);

        if (course.isEmpty()) throw new EntityNotFoundException();

        registrationRepository.deleteAllByCourseId(courseId);
        courseRepository.delete(course.get());
        return course;

    }

    @Override
    public Optional<CourseRegistration> registerStudent(Long courseId, AppUser user) {
        var optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            throw new EntityNotFoundException();
        }

        var course = optionalCourse.get();

        var contains = course
                .getRegistrations()
                .stream()
                .anyMatch(e -> e.getStudent()
                        .getId()
                        .equals(user.getId()));

        if (contains) {
            log.info("Aluno {} j?? registrado no curso {}.", user.getUsername(), courseId);
            return Optional.empty();
        }

        log.info("Adicionando user {} ao curso {}.", user.getUsername(), course.getName());
        var registration = new CourseRegistration();
        registration.setCourse(course);
        registration.setStudent(user);
        registration.setRegisteredAt(LocalDateTime.now());

        registrationRepository.save(registration);

        course.getRegistrations().add(registration);

        return Optional.of(registration);
    }

    @Override
    public Optional<CourseRegistration> removeStudent(Long courseId, Long registrationId) {
        var optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            throw new EntityNotFoundException();
        }

        var course = optionalCourse.get();

        var registration = registrationRepository.findById(registrationId);
        if (registration.isEmpty()) {
            throw new EntityNotFoundException();
        }

        course.getRegistrations().remove(registration.get());
        registrationRepository.deleteById(registrationId);

        return registration;
    }

    @Override
    public Optional<CourseRegistration> getRegistration(Long registrationId) {
        return registrationRepository.findById(registrationId);
    }
}
