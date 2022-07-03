package com.conecta.conecta_api.data;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.conecta.conecta_api.domain.entities.FeedPost;

public interface FeedRepository extends JpaRepository<FeedPost, Long> {
    Collection<FeedPost> findAllByCourseId(Long courseId);

    void deleteAllByCourseId(Long courseId);
}
