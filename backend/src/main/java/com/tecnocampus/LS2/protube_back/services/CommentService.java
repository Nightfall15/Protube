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

    public CommentDTO toDTO(Comment c) {
        CommentDTO dto = new CommentDTO();
        dto.setId(c.getId());
        dto.setAuthor(c.getAuthor());
        dto.setText(c.getText());
        dto.setCreatedAt(c.getCreatedAt().toString());

        dto.setReplies(
                c.getReplies()
                        .stream()
                        .map(this::toDTO)
                        .toList()
        );

        return dto;
    }

    public CommentService(ICommentRepository commentRepository, IVideoFileRepository videoFileRepository) {
        this.commentRepository = commentRepository;
        this.videoFileRepository = videoFileRepository;
    }

    public List<CommentDTO> getCommentsForVideo(Long videoId) {
        return commentRepository
                .findByVideoIdAndParentIsNullOrderByCreatedAtAsc(videoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Comment addComment(Long videoId, String author, String text, Long parentId) {
        VideoFile video = videoFileRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Err 404: Video not found"));

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setText(text);
        comment.setVideo(video);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            comment.setParent(parent);
        }

        return commentRepository.save(comment);
    }

}
