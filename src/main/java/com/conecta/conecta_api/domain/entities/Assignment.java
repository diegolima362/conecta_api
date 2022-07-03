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
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AppUser professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Course course;

    private String title;

    private String content;

    private Integer grade;

    private LocalDateTime creationDate;

    private LocalDateTime editDate;

    private LocalDateTime dueDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(professor, that.professor) && Objects.equals(course, that.course) && Objects.equals(title, that.title) && Objects.equals(content, that.content) && Objects.equals(grade, that.grade) && Objects.equals(creationDate, that.creationDate) && Objects.equals(editDate, that.editDate) && Objects.equals(dueDate, that.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, professor, course, title, content, grade, creationDate, editDate, dueDate);
    }
}