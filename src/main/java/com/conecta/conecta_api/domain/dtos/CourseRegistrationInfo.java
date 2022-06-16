package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.CourseRegistration;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseRegistrationInfo implements Serializable {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private String studentEmail;
    private Long courseId;
    private String courseName;
    private LocalDateTime registeredAt;

    static public CourseRegistrationInfo fromCourseRegistration(CourseRegistration registration) {

        return new CourseRegistrationInfo(
                registration.getId(),
                registration.getStudent().getId(),
                registration.getStudent().getName(),
                registration.getStudent().getUsername(),
                registration.getStudent().getEmail(),
                registration.getCourse().getId(),
                registration.getCourse().getName(),
                registration.getRegisteredAt()
        );
    }
}
