package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.CommentRepository;
import com.conecta.conecta_api.data.FeedRepository;
import com.conecta.conecta_api.domain.entities.FeedPost;
import com.conecta.conecta_api.services.interfaces.IFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedService implements IFeedService {
    private final FeedRepository feedRepository;
    private final CommentRepository commentRepository;

    @Override
    public Optional<FeedPost> getPost(Long postId) {
        return feedRepository.findById(postId);
    }

    @Override
    public Collection<FeedPost> getCourseFeed(Long courseId) {
        return feedRepository.findAllByCourseId(courseId);
    }

    @Override
    public FeedPost saveFeedPost(FeedPost feedPost) {
        return feedRepository.save(feedPost);
    }

    @Override
    public void deletePost(Long postId) {
        commentRepository.findAllByPostId(postId);
        feedRepository.deleteById(postId);
    }

    @Override
    public void deleteAllByCourseId(Long courseId) {
        feedRepository.deleteAllByCourseId(courseId);
    }
}