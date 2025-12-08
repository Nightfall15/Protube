package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.models.VideoDTO;
import com.tecnocampus.LS2.protube_back.models.VideoFile;
import com.tecnocampus.LS2.protube_back.repositories.IUserRepository;
import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired
    private IVideoFileRepository videoFileRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public VideoService(ObjectMapper objectMapper, IVideoFileRepository videoFileRepository) {
        this.objectMapper = objectMapper;
        this.videoFileRepository = videoFileRepository;

    }

    public VideoFile save(VideoFile videoFile) {
        return videoFileRepository.save(videoFile);
    }

    public List<VideoFile> findAll() {
        return videoFileRepository.findAll();
    }


   public List<VideoDTO> getVideos() {
        return videoFileRepository.findAll().stream()
                .map(video -> {
                    VideoDTO dto = new VideoDTO();
                    dto.setId(video.getId());
                    dto.setTitle(video.getTitle());
                    dto.setDescription(video.getDescription());
                    dto.setThumbnailUrl("http://localhost:8080/api/videos/thumbnail/"+video.getId());
                    dto.setVideoUrl("http://localhost:8080/api/videos/stream/"+video.getId());
                    dto.setUploader(video.getUploader().getUsername());
                    dto.setLikes(video.getLikes());
                    return dto;
                })
                .collect(Collectors.toList());
   }

    public void uploadAllVideos(Path storeDir) throws Exception {
        Map<String, VideoFiles> videoGroups = new HashMap<>();

        Files.list(storeDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

                    videoGroups.putIfAbsent(baseName, new VideoFiles());
                    VideoFiles files = videoGroups.get(baseName);

                    if (fileName.endsWith(".mp4")) {
                        files.mp4Path = path.toString();
                    } else if (fileName.endsWith(".webp")) {
                        files.thumbnailPath = path.toString();
                    } else if (fileName.endsWith(".json")) {
                        files.jsonPath = path.toString();
                    }
                    files.setFileName(baseName);
                });

        for (Map.Entry<String, VideoFiles> entry : videoGroups.entrySet()) {
            VideoFiles files = entry.getValue();

            if (files.mp4Path != null && files.getJsonPath() != null) {
                String metadata = Files.readString(Paths.get(files.getJsonPath()));
                JsonNode jsonNode = objectMapper.readTree(metadata);

                String username = jsonNode.get("user").asText();
                User user = userRepository.findByUsername(username)
                        .orElseGet(() -> createDefaultUser(username));

                VideoFile video = new VideoFile();
                video.setId(Long.parseLong(files.fileName));
                video.setTitle(jsonNode.get("title").asText());
                video.setMp4Path(files.mp4Path);
                video.setJsonPath(files.jsonPath);
                video.setThumbnailPath(files.thumbnailPath);
                video.setDescription(jsonNode.get("meta").get("description").asText());
                video.setTags(objectMapper.convertValue(jsonNode.get("tags"), ArrayList.class));
                video.setUploader(user);

                videoFileRepository.save(video);
            }
        }
    }

    public Long saveUploadedVideo(
            MultipartFile file,
            String title,
            String description,
            String uploaderUsername

    ) throws Exception {
        //Ensure directory exists
        Path videoDir = Paths.get("videos");
        if (!Files.exists(videoDir)) Files.createDirectories(videoDir);

        //Generate file name
        String cleanFilename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path videoPath = videoDir.resolve(cleanFilename);

        //Save mp4 file to disk
        Files.copy(file.getInputStream(), videoPath);

        //Get username
        User uploader = userRepository.findByUsername(uploaderUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + uploaderUsername));

        //Create entity
        VideoFile video = new VideoFile();
        video.setTitle(title);
        video.setDescription(description);
        video.setMp4Path(videoPath.toString());
        video.setThumbnailPath(null); //no thumbnail for now
        video.setJsonPath(null);      //not required
        video.setTags(new ArrayList<>()); //empty list for now
        video.setUploader(uploader);

        //Save to database
        videoFileRepository.save(video);

        return video.getId();
    }

    private void generateThumbnail(String videoPath, String thumbnailPath) throws IOException, InterruptedException {
        // Get video duration first
        ProcessBuilder durationBuilder = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                videoPath
        );
        Process durationProcess = durationBuilder.start();
        String durationStr = new String(durationProcess.getInputStream().readAllBytes()).trim();
        durationProcess.waitFor();

        double duration = Double.parseDouble(durationStr);
        double randomTime = Math.random() * duration;

        // Generate thumbnail at random time
        ProcessBuilder builder = new ProcessBuilder(
                "ffmpeg",
                "-ss", String.valueOf(randomTime),
                "-i", videoPath,
                "-vframes", "1",
                "-vf", "scale=320:-1",
                "-q:v", "2",
                thumbnailPath
        );
        builder.redirectErrorStream(true);
        Process process = builder.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("FFmpeg failed with exit code: " + exitCode);
        }
    }

    private User createDefaultUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("1234"));
        user.setEmail(username + "@store_user.com");
        user.setName("Default");
        user.setSurname("User");
        user.setNumber("0000000000");
        return userRepository.save(user);
    }

    @Setter
    @Getter
    private class VideoFiles {

        private String fileName;
        private String mp4Path;
        private String thumbnailPath;
        private String jsonPath;

    }
}

