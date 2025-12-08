package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.models.VideoDTO;
import com.tecnocampus.LS2.protube_back.models.VideoFile;
import com.tecnocampus.LS2.protube_back.repositories.IUserRepository;
import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import com.tecnocampus.LS2.protube_back.services.UserService;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideosControllerTest {

    @Mock
    private IVideoFileRepository videoFileRepository;

    @Mock
    private VideoService videoService;

    @Mock
    private UserService userService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VideosController videosController;

    private VideoFile testVideo;
    private VideoDTO testVideoDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setLikedVideos(new ArrayList<>());

        testVideo = new VideoFile();
        testVideo.setId(1L);
        testVideo.setTitle("Test Video");
        testVideo.setDescription("Test Description");
        testVideo.setUploader(testUser);
        testVideo.setLikes(5);
        testVideo.setMp4Path("test.mp4");
        testVideo.setThumbnailPath("test.webp");

        testVideoDTO = new VideoDTO();
        testVideoDTO.setId(1L);
        testVideoDTO.setTitle("Test Video");
        testVideoDTO.setDescription("Test Description");
        testVideoDTO.setUploader("testUser");
        testVideoDTO.setLikes(5);
    }

    @Test
    void getVideos_shouldReturnListOfVideos() {
        List<VideoDTO> videos = List.of(testVideoDTO);
        when(videoService.getVideos()).thenReturn(videos);

        ResponseEntity<List<VideoDTO>> response = videosController.getVideos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(videos, response.getBody());
        verify(videoService).getVideos();
    }

    @Test
    void getVideoById_shouldReturnVideo_whenVideoExists() {
        when(videoFileRepository.findById(1L)).thenReturn(Optional.of(testVideo));

        ResponseEntity<VideoDTO> response = videosController.getVideoById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Video", response.getBody().getTitle());
    }

    @Test
    void getVideoById_shouldReturnNotFound_whenVideoDoesNotExist() {
        when(videoFileRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<VideoDTO> response = videosController.getVideoById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void uploadVideo_shouldReturnBadRequest_whenFileIsEmpty() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "video/mp4", new byte[0]);

        ResponseEntity<?> response = videosController.uploadVideo(emptyFile, "Title", "Description", "user");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No video file provided", response.getBody());
    }

    @Test
    void uploadVideo_shouldReturnVideoId_whenSuccessful() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "test content".getBytes());
        when(videoService.saveUploadedVideo(any(), eq("Title"), eq("Description"), eq("user"))).thenReturn(1L);

        ResponseEntity<?> response = videosController.uploadVideo(file, "Title", "Description", "user");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody());
    }

    @Test
    void uploadVideo_shouldReturnError_whenExceptionOccurs() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "test content".getBytes());
        when(videoService.saveUploadedVideo(any(), any(), any(), any())).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = videosController.uploadVideo(file, "Title", "Description", "user");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Error uploading video"));
    }

    @Test
    void like_shouldIncreaseLikes_whenUserHasNotLiked() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.findById(1L)).thenReturn(Optional.of(testVideo));

        ResponseEntity<?> response = videosController.like(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(testUser);
        verify(videoFileRepository).save(testVideo);
    }

    @Test
    void like_shouldNotIncreaseLikes_whenUserAlreadyLiked() {
        testUser.getLikedVideos().add(testVideo);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.findById(1L)).thenReturn(Optional.of(testVideo));

        ResponseEntity<?> response = videosController.like(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void like_shouldThrowException_whenUserNotFound() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> videosController.like(1L, authentication));
    }

    @Test
    void like_shouldThrowException_whenVideoNotFound() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> videosController.like(1L, authentication));
    }

    @Test
    void unlike_shouldDecreaseLikes_whenUserHasLiked() {
        testUser.getLikedVideos().add(testVideo);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.findById(1L)).thenReturn(Optional.of(testVideo));

        ResponseEntity<?> response = videosController.unlike(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).save(testUser);
        verify(videoFileRepository).save(testVideo);
    }

    @Test
    void unlike_shouldNotDecreaseLikes_whenUserHasNotLiked() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.findById(1L)).thenReturn(Optional.of(testVideo));

        ResponseEntity<?> response = videosController.unlike(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void unlike_shouldThrowException_whenUserNotFound() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> videosController.unlike(1L, authentication));
    }

    @Test
    void unlike_shouldThrowException_whenVideoNotFound() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> videosController.unlike(1L, authentication));
    }
}