package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.models.*;
import com.tecnocampus.LS2.protube_back.repositories.ICommentRepository;
import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final ICommentRepository commentRepository;
    private final IVideoFileRepository videoFileRepository;

    public CommentService(ICommentRepository commentRepository, IVideoFileRepository videoFileRepository) {
        this.commentRepository = commentRepository;
        this.videoFileRepository = videoFileRepository;
    }

    public List<Comment> getCommentsForVideo(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtAsc(videoId);
    }

    public Comment addComment(Long videoId, String author, String text) {
        VideoFile video = videoFileRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Err 404: Video not found"));

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setText(text);
        comment.setVideo(video);

        video.getComments().add(comment);

        return commentRepository.save(comment);
    }

}
