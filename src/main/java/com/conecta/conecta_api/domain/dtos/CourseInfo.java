package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.Course;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfo {
    private Long id;
    private String name;
    private Long professorId;
    private String professorName;
    private String code;

    static public CourseInfo fromCourse(Course course) {
        return new CourseInfo(
                course.getId(),
                course.getName(),
                course.getProfessor().getId(),
                course.getProfessor().getName(),
                course.getCode()
        );
    }
}