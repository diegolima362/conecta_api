package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.FeedPost;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FeedPostInfo implements Serializable {
    private  Long id;
    private  Long creatorId;
    private  String creatorName;
    private  String creatorUsername;
    private  Long courseId;
    private  String courseName;
    private  String title;
    private  String subtitle;
    private  String content;
    private  LocalDateTime creationDate;
    private  LocalDateTime editDate;

    public static FeedPostInfo fromPost(FeedPost post){
        return new FeedPostInfo(
                post.getId(),
                post.getCreator().getId(),
                post.getCreator().getName(),
                post.getCreator().getUsername(),
                post.getCourse().getId(),
                post.getCourse().getName(),
                post.getTitle(),
                post.getSubtitle(),
                post.getContent(),
                post.getCreationDate(),
                post.getEditDate()
        );
    }
}
