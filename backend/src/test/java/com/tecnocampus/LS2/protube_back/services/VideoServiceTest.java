package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoServiceTest {


    VideoService videoService = new VideoService();

    @Ignore
    @Test
    void shouldGoToFolderVideos() {
        assertEquals(List.of(), videoService.getVideos());
    }

}