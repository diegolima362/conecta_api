package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.Comment;
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
public class CommentInfo implements Serializable {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorUsername;
    private Long postId;
    private Long postCourseId;
    private String postCourseName;
    private Long replyTo;
    private List<CommentDto> replies = new ArrayList<>();
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime editDate;


    public static CommentInfo fromComment(Comment comment) {

        return new CommentInfo(comment.getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getAuthor().getUsername(),
                comment.getPost().getId(),
                comment.getPost().getCourse().getId(),
                comment.getPost().getCourse().getName(),
                comment.getReplyTo() != null ? comment.getReplyTo().getId() : null,
                comment.getReplies() != null ? comment.getReplies().stream().map(CommentDto::fromComment).toList() : null,
                comment.getContent(),
                comment.getCreationDate(),
                comment.getEditDate());
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class CommentDto implements Serializable {
        private Long id;
        private Long creatorId;
        private String creatorName;
        private String creatorUsername;
        private String content;

        public static CommentDto fromComment(Comment comment) {
            return new CommentDto(
                    comment.getId(),
                    comment.getAuthor().getId(),
                    comment.getAuthor().getName(),
                    comment.getAuthor().getUsername(),
                    comment.getContent()
            );
        }
    }


}
