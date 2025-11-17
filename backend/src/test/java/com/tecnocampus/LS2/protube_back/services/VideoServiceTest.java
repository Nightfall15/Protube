package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private IVideoFileRepository videoFileRepository;

    @InjectMocks
    private VideoService videoService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldGoToFolderVideos() {
        when(videoFileRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), videoService.getVideos());
    }
}