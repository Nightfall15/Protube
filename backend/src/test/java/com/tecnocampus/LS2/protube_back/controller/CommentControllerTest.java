package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.models.Comment;
import com.tecnocampus.LS2.protube_back.models.CommentDTO;
import com.tecnocampus.LS2.protube_back.services.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    CommentService commentService;

    @InjectMocks
    CommentController commentController;

    @Test
    void testGetComments() {
        CommentDTO dto = new CommentDTO();
        dto.setId(1L);
        dto.setAuthor("Batman");

        when(commentService.getCommentsForVideo(10L))
                .thenReturn(List.of(dto));

        List<CommentDTO> result = commentController.getComments(10L);

        assertEquals(1, result.size());
        assertEquals("Sherma", result.get(0).getAuthor());
        verify(commentService, times(1))
                .getCommentsForVideo(10L);
    }

    @Test
    void testCreateComment() {
        Comment saved = new Comment();
        saved.setId(5L);
        saved.setAuthor("Garmond");
        saved.setText("SENSIBO!");
        saved.setCreatedAt(LocalDateTime.now());

        when(commentService.addComment(7L, "Garmond", "SENSIBO!", null))
                .thenReturn(saved);

        CommentDTO dto = new CommentDTO();
        dto.setId(5L);
        dto.setAuthor("Garmond");
        dto.setText("SENSIBO!");

        when(commentService.toDTO(saved)).thenReturn(dto);

        CommentDTO result = commentController.createComment(
                7L,
                "Garmond",
                "SENSIBO!",
                null
        );

        assertEquals(5L, result.getId());
        assertEquals("Garmond", result.getAuthor());
        assertEquals("SENSIBO!", result.getText());

        verify(commentService).addComment(7L, "Garmond", "SENSIBO!", null);
        verify(commentService).toDTO(saved);
    }
}