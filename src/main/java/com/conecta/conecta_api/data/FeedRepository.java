package com.conecta.conecta_api.data;

import com.conecta.conecta_api.domain.entities.FeedPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FeedRepository extends JpaRepository<FeedPost, Long> {
    Collection<FeedPost> findAllByCourseId(Long courseId);
}
