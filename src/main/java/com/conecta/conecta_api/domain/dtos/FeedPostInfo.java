package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.FeedPost;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FeedPostInfo implements Serializable {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorUsername;
    private Long courseId;
    private String courseName;
    private String title;
    private String content;
    private boolean isAssignment;
    private LocalDateTime creationDate;
    private LocalDateTime editDate;
    private List<CommentInfo> comments = new ArrayList<>();

    public static FeedPostInfo fromPost(FeedPost post) {
        return new FeedPostInfo(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getAuthor().getUsername(),
                post.getCourse().getId(),
                post.getCourse().getName(),
                post.getTitle(),
                post.getContent(),
                post.isAssignment(),
                post.getCreationDate(),
                post.getEditDate(),
                post.getComments().stream().map(CommentInfo::fromComment).toList()
        );
    }
}
