package com.conecta.conecta_api.domain.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.conecta.conecta_api.domain.entities.AssignmentSubmission;
import com.conecta.conecta_api.domain.entities.AssignmentSubmissionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentSubmissionInfo implements Serializable {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long assignmentId;
    private Long courseId;
    private LocalDateTime finishDate;
    private LocalDateTime editDate;
    private Integer grade;
    private String content;
    private AssignmentSubmissionStatus status;

    public static AssignmentSubmissionInfo fromSubmission(AssignmentSubmission userAssignment) {
        return new AssignmentSubmissionInfo(
                userAssignment.getId(),
                userAssignment.getStudent().getId(),
                userAssignment.getStudent().getName(),
                userAssignment.getAssignment().getId(),
                userAssignment.getAssignment().getCourse().getId(),
                userAssignment.getFinishDate(),
                userAssignment.getEditDate(),
                userAssignment.getGrade(),
                userAssignment.getContent(),
                userAssignment.getStatus()
        );
    }
}
