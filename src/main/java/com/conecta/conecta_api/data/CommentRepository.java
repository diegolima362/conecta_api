package com.conecta.conecta_api.data;

import com.conecta.conecta_api.domain.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByPostId(Long postId);

    void deleteAllByPostId(Long postId);

    void deleteAllByPostCourseId(Long courseId);

    void deleteAllByAuthorIdAndPostCourseId(Long authorId, Long courseId);
}