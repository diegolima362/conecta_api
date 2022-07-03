package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.Comment;

import java.util.Collection;
import java.util.Optional;

public interface ICommentService {
    Collection<Comment> getPostComments(Long postId);

    Comment saveComment(Comment comment);

    Optional<Comment> getComment(Long commentId);

    void deleteComment(Long commentId);

    void deleteAllByCourseId(Long courseId);

    void deleteAllByUserIdAtCourse(Long userId, Long courseId);
}
