package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.models.VideoDTO;
import com.tecnocampus.LS2.protube_back.models.VideoFile;
import com.tecnocampus.LS2.protube_back.repositories.IUserRepository;
import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private IVideoFileRepository videoFileRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private VideoService videoService;

    private User testUser;
    private VideoFile testVideo;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        videoService = new VideoService(objectMapper, videoFileRepository);
        videoService.userRepository = userRepository;
        videoService.passwordEncoder = passwordEncoder;

        testUser = new User();
        testUser.setUsername("testUser");

        testVideo = new VideoFile();
        testVideo.setId(1L);
        testVideo.setTitle("Test Video");
        testVideo.setDescription("Test Description");
        testVideo.setUploader(testUser);
        testVideo.setLikes(5);
    }

    @Test
    void save_shouldSaveVideoFile() {
        when(videoFileRepository.save(testVideo)).thenReturn(testVideo);

        VideoFile result = videoService.save(testVideo);

        assertEquals(testVideo, result);
        verify(videoFileRepository).save(testVideo);
    }

    @Test
    void findAll_shouldReturnAllVideos() {
        List<VideoFile> videos = List.of(testVideo);
        when(videoFileRepository.findAll()).thenReturn(videos);

        List<VideoFile> result = videoService.findAll();

        assertEquals(videos, result);
        verify(videoFileRepository).findAll();
    }

    @Test
    void getVideos_shouldReturnVideoDTOList() {
        when(videoFileRepository.findAll()).thenReturn(List.of(testVideo));

        List<VideoDTO> result = videoService.getVideos();

        assertEquals(1, result.size());
        assertEquals("Test Video", result.get(0).getTitle());
        assertEquals("testUser", result.get(0).getUploader());
    }

    @Test
    void saveUploadedVideo_shouldSaveVideo() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "content".getBytes());
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(videoFileRepository.save(any(VideoFile.class))).thenAnswer(invocation -> {
            VideoFile video = invocation.getArgument(0);
            video.setId(1L);
            return video;
        });

        Long videoId = videoService.saveUploadedVideo(file, "Title", "Description", "testUser");

        assertNotNull(videoId);
        verify(videoFileRepository).save(any(VideoFile.class));
    }

    @Test
    void saveUploadedVideo_shouldThrowException_whenUserNotFound() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "content".getBytes());
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                videoService.saveUploadedVideo(file, "Title", "Description", "testUser")
        );
    }

    @Test
    void uploadAllVideos_shouldProcessAllVideoFiles() throws Exception {
        Path mp4File = tempDir.resolve("1.mp4");
        Path jsonFile = tempDir.resolve("1.json");
        Path webpFile = tempDir.resolve("1.webp");

        Files.writeString(mp4File, "video content");
        Files.writeString(jsonFile, "{\"user\":\"testUser\",\"title\":\"Test\",\"meta\":{\"description\":\"Desc\"},\"tags\":[]}");
        Files.writeString(webpFile, "thumbnail");

        JsonNode mockNode = mock(JsonNode.class);
        JsonNode metaNode = mock(JsonNode.class);
        JsonNode tagsNode = mock(JsonNode.class);

        when(objectMapper.readTree(anyString())).thenReturn(mockNode);
        when(mockNode.get("user")).thenReturn(mockNode);
        when(mockNode.get("title")).thenReturn(mockNode);
        when(mockNode.get("meta")).thenReturn(metaNode);
        when(metaNode.get("description")).thenReturn(metaNode);
        when(mockNode.get("tags")).thenReturn(tagsNode);
        when(mockNode.asText()).thenReturn("testUser", "Test", "Desc");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(objectMapper.convertValue(any(), eq(ArrayList.class))).thenReturn(new ArrayList<>());

        videoService.uploadAllVideos(tempDir);

        verify(videoFileRepository, atLeastOnce()).save(any(VideoFile.class));
    }

    @Test
    void uploadAllVideos_shouldCreateDefaultUser_whenUserNotFound() throws Exception {
        Path mp4File = tempDir.resolve("1.mp4");
        Path jsonFile = tempDir.resolve("1.json");

        Files.writeString(mp4File, "video content");
        Files.writeString(jsonFile, "{\"user\":\"newUser\",\"title\":\"Test\",\"meta\":{\"description\":\"Desc\"},\"tags\":[]}");

        JsonNode mockNode = mock(JsonNode.class);
        JsonNode metaNode = mock(JsonNode.class);
        JsonNode tagsNode = mock(JsonNode.class);

        when(objectMapper.readTree(anyString())).thenReturn(mockNode);
        when(mockNode.get("user")).thenReturn(mockNode);
        when(mockNode.get("title")).thenReturn(mockNode);
        when(mockNode.get("meta")).thenReturn(metaNode);
        when(metaNode.get("description")).thenReturn(metaNode);
        when(mockNode.get("tags")).thenReturn(tagsNode);
        when(mockNode.asText()).thenReturn("newUser", "Test", "Desc");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(objectMapper.convertValue(any(), eq(ArrayList.class))).thenReturn(new ArrayList<>());

        videoService.uploadAllVideos(tempDir);

        verify(userRepository).save(any(User.class));
    }
}