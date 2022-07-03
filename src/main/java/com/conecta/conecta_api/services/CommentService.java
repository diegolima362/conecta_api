package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.CommentRepository;
import com.conecta.conecta_api.domain.entities.Comment;
import com.conecta.conecta_api.services.interfaces.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    @Override
    public Collection<Comment> getPostComments(Long postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteAllByCourseId(Long courseId) {
        commentRepository.deleteAllByPostCourseId(courseId);
    }

    @Override
    public void deleteAllByUserIdAtCourse(Long userId, Long courseId) {
        commentRepository.deleteAllByAuthorIdAndPostCourseId(userId, courseId);
    }
}
