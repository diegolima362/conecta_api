package com.conecta.conecta_api.domain.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AppUser student;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Assignment assignment;

    private LocalDateTime finishDate;

    private LocalDateTime editaDate;

    private Integer grade;

    private String status;
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAssignment)) return false;
        UserAssignment that = (UserAssignment) o;
        return Objects.equals(id, that.id) && Objects.equals(student, that.student) && Objects.equals(assignment, that.assignment) && Objects.equals(finishDate, that.finishDate) && Objects.equals(editaDate, that.editaDate) && Objects.equals(grade, that.grade) && Objects.equals(status, that.status) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, assignment, finishDate, editaDate, grade, status, content);
    }
}