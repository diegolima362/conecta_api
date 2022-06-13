package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.AppUser;
import com.conecta.conecta_api.domain.entities.Course;
import com.conecta.conecta_api.domain.entities.CourseRegistration;

import java.util.List;
import java.util.Optional;

public interface ICourseService {
    List<Course> getCourses();

    List<Course> getCourses(AppUser user);

    Course saveCourse(Course course);

    Optional<Course> getCourse(Long courseId);

    Optional<Course> getCourseByCode(String code);

    void joinCourse(String code, AppUser user);

    List<CourseRegistration> getCourseRegistrations(Long courseId);
}
