package com.conecta.conecta_api.api;

import com.conecta.conecta_api.domain.dtos.CommentInfo;
import com.conecta.conecta_api.domain.dtos.FeedPostInfo;
import com.conecta.conecta_api.domain.entities.Comment;
import com.conecta.conecta_api.domain.entities.FeedPost;
import com.conecta.conecta_api.services.interfaces.IAppUserService;
import com.conecta.conecta_api.services.interfaces.ICommentService;
import com.conecta.conecta_api.services.interfaces.ICourseService;
import com.conecta.conecta_api.services.interfaces.IFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class FeedResource {
    private final IFeedService feedService;
    private final IAppUserService appUserService;
    private final ICourseService courseService;
    private final ICommentService commentService;


    @GetMapping(path = "/courses/{courseId}/feed")
    public ResponseEntity<List<FeedPostInfo>> getCourseFeed(@PathVariable Long courseId) {
        return ResponseEntity.ok().body(
                feedService.getCourseFeed(courseId)
                        .stream()
                        .map(FeedPostInfo::fromPost)
                        .collect(Collectors.toList()));
    }

    @PostMapping(path = "/courses/feed")
    public ResponseEntity<FeedPostInfo> createPost(@RequestBody FeedPostInfo postInfo) {
        FeedPost toSave = new FeedPost();

        toSave.setTitle(postInfo.getTitle());
        toSave.setContent(postInfo.getContent());
        toSave.setCreationDate(LocalDateTime.now());

        toSave.setComments(new ArrayList<>());


        var course = courseService.getCourse(postInfo.getCourseId());
        if (course.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setCourse(course.get());

        var author = appUserService.getUserById(postInfo.getAuthorId());
        if (author.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setAuthor(author.get());

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/feed/create").toUriString());

        var post = feedService.saveFeedPost(toSave);

        return ResponseEntity.created(uri).body(FeedPostInfo.fromPost(post));
    }

    @PostMapping(path = "/courses/feed/comments")
    public ResponseEntity<CommentInfo> addComment(@RequestBody CommentInfo commentInfo) {
        Comment toSave = new Comment();

        toSave.setContent(commentInfo.getContent());
        toSave.setCreationDate(LocalDateTime.now());

        if (commentInfo.getReplyTo() != null) {
            var reply = commentService.getComment(commentInfo.getReplyTo());
            if (reply.isEmpty()) {
                return ResponseEntity.unprocessableEntity().build();
            }
            toSave.setReplyTo(reply.get());
        }

        var post = feedService.getPost(commentInfo.getPostId());
        if (post.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setPost(post.get());

        var author = appUserService.getUserById(commentInfo.getAuthorId());
        if (author.isEmpty()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        toSave.setAuthor(author.get());

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/comments/create").toUriString());

        var comment = commentService.saveComment(toSave);

        return ResponseEntity.created(uri).body(CommentInfo.fromComment(comment));
    }

    @DeleteMapping(path = "/courses/feed/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
