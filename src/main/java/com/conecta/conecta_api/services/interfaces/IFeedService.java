package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.FeedPost;

import java.util.Collection;
import java.util.Optional;

public interface IFeedService {
    Optional<FeedPost> getPost(Long postId);

    Collection<FeedPost> getCourseFeed(Long courseId);

    FeedPost saveFeedPost(FeedPost feedPost);

    void deletePost(Long postId);
}
