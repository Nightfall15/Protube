package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.models.Comment;
import com.tecnocampus.LS2.protube_back.models.CommentDTO;
import com.tecnocampus.LS2.protube_back.models.VideoFile;
import com.tecnocampus.LS2.protube_back.repositories.ICommentRepository;
import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    ICommentRepository commentRepository;

    @Mock
    IVideoFileRepository videoRepo;

    @InjectMocks
    CommentService commentService;

    VideoFile video;

    @BeforeEach
    void setup() {
        video = new VideoFile();
        video.setId(99L);
    }

    @Test
    void testGetCommentsForVideo_returnsDTOs() {
        Comment c = new Comment();
        c.setId(1L);
        c.setAuthor("Hornet");
        c.setText("sha");
        c.setCreatedAt(LocalDateTime.now());

        when(commentRepository
                .findByVideoIdAndParentIsNullOrderByCreatedAtAsc(99L))
                .thenReturn(List.of(c));

        List<CommentDTO> result = commentService.getCommentsForVideo(99L);

        assertEquals(1, result.size());
        assertEquals("Hornet", result.get(0).getAuthor());
        assertEquals("sha", result.get(0).getText());
        verify(commentRepository, times(1))
                .findByVideoIdAndParentIsNullOrderByCreatedAtAsc(99L);
    }

    @Test
    void testAddComment_withoutParent() {
        when(videoRepo.findById(99L)).thenReturn(Optional.of(video));

        Comment saved = new Comment();
        saved.setId(123L);

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        Comment result = commentService.addComment(
                99L,
                "Hornet",
                "guarana",
                null
        );

        assertNotNull(result);
        assertEquals(123L, result.getId());
        verify(commentRepository, never()).findById(any());
    }

    @Test
    void testAddComment_withParent() {
        when(videoRepo.findById(99L)).thenReturn(Optional.of(video));

        Comment parent = new Comment();
        parent.setId(50L);

        when(commentRepository.findById(50L)).thenReturn(Optional.of(parent));

        Comment saved = new Comment();
        saved.setId(200L);

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        Comment result = commentService.addComment(
                99L,
                "Shakra",
                "Phoshanka",
                50L
        );

        assertEquals(200L, result.getId());
        verify(commentRepository, times(1)).findById(50L);
    }

    @Test
    void testAddComment_videoNotFound() {
        when(videoRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                commentService.addComment(999L, "Lace", "jiji", null)
        );
    }

    @Test
    void testAddComment_parentNotFound() {
        when(videoRepo.findById(99L)).thenReturn(Optional.of(video));
        when(commentRepository.findById(67L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                commentService.addComment(99L, "SIX", "SEVEN", 67L)
        );
    }
}