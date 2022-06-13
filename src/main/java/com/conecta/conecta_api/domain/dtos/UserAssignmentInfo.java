package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.UserAssignment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignmentInfo implements Serializable {
    private Long id;
    private Long studentId;
    private Long assignmentId;
    private Long assignmentCourseId;
    private LocalDateTime finishDate;
    private LocalDateTime editaDate;
    private Integer grade;
    private String content;
    private String status;

    public static UserAssignmentInfo fromUserAssignment(UserAssignment userAssignment) {
        return new UserAssignmentInfo(
                userAssignment.getId(),
                userAssignment.getStudent().getId(),
                userAssignment.getAssignment().getId(),
                userAssignment.getAssignment().getCourse().getId(),
                userAssignment.getFinishDate(),
                userAssignment.getEditaDate(),
                userAssignment.getGrade(),
                userAssignment.getContent(),
                userAssignment.getStatus()
        );
    }
}
