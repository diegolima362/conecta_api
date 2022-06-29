package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.AssignmentRepository;
import com.conecta.conecta_api.data.FeedRepository;
import com.conecta.conecta_api.domain.entities.FeedPost;
import com.conecta.conecta_api.services.interfaces.IFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FeedService implements IFeedService {
    private final FeedRepository feedRepository;
    private final AssignmentRepository assignmentRepository;

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
        feedRepository.deleteById(postId);
    }
}