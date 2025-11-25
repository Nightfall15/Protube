package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.models.Comment;
import com.tecnocampus.LS2.protube_back.models.CommentDTO;
import com.tecnocampus.LS2.protube_back.services.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("")
    public List<CommentDTO> getComments(@PathVariable Long videoId) {
        return commentService.getCommentsForVideo(videoId);
    }

    @PostMapping("")
    public CommentDTO createComment(
            @PathVariable Long videoId,
            @RequestParam String author,
            @RequestParam String text,
            @RequestParam(required = false) Long parentId) {

        Comment saved = commentService.addComment(videoId, author, text, parentId);
        return commentService.toDTO(saved);
    }
}
