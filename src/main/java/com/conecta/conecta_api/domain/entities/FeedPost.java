
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
public class FeedPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AppUser creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Course course;
    private String title;
    private String subtitle;
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime editDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedPost)) return false;
        FeedPost feedPost = (FeedPost) o;
        return Objects.equals(id, feedPost.id) && Objects.equals(creator, feedPost.creator) && Objects.equals(course, feedPost.course) && Objects.equals(title, feedPost.title) && Objects.equals(subtitle, feedPost.subtitle) && Objects.equals(content, feedPost.content) && Objects.equals(creationDate, feedPost.creationDate) && Objects.equals(editDate, feedPost.editDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creator, course, title, subtitle, content, creationDate, editDate);
    }
}