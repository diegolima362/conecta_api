package com.conecta.conecta_api.domain.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AppUser author;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private FeedPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Comment replyTo;

    @OneToMany(mappedBy = "replyTo", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Comment> replies;

    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime editDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return id != null && Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}