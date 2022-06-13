package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.Assignment;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentInfo implements Serializable {
    private Long id;
    private Long professorId;
    private String professorName;
    private Long courseId;
    private String courseName;
    private String title;
    private String subtitle;
    private String content;
    private Integer grade;
    private LocalDateTime creationDate;
    private LocalDateTime editDate;
    private LocalDateTime dueDate;


    public static AssignmentInfo fromAssignment(Assignment assignment) {
        return new AssignmentInfo(
                assignment.getId(),
                assignment.getProfessor().getId(),
                assignment.getProfessor().getName(),
                assignment.getCourse().getId(),
                assignment.getCourse().getName(),
                assignment.getTitle(),
                assignment.getSubtitle(),
                assignment.getContent(),
                assignment.getGrade(),
                assignment.getCreationDate(),
                assignment.getEditDate(),
                assignment.getDueDate()
        );
    }

}
