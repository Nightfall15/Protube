package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.models.RangeResource;
import com.tecnocampus.LS2.protube_back.models.VideoDTO;
import com.tecnocampus.LS2.protube_back.models.VideoFile;
import com.tecnocampus.LS2.protube_back.repositories.IVideoFileRepository;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class VideosController {

    VideoService videoService;
    private final IVideoFileRepository videoFileRepository;

    public VideosController(IVideoFileRepository videoFileRepository, VideoService videoService) {
        this.videoService = videoService;
        this.videoFileRepository = videoFileRepository;
    }


    @GetMapping("")
    public ResponseEntity<List<VideoDTO>> getVideos() {
        return ResponseEntity.ok().body(videoService.getVideos());

    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoDTO> getVideoById(@PathVariable Long id) {

        return videoFileRepository.findById(id)
                .map(video -> {
                    VideoDTO dto = new VideoDTO();
                    dto.setId(video.getId());
                    dto.setTitle(video.getTitle());
                    dto.setDescription(video.getDescription());
                    dto.setUploader(video.getUploader().getUsername());

                    dto.setThumbnailUrl("http://localhost:8080/api/videos/thumbnail/" + video.getId());
                    dto.setVideoUrl("http://localhost:8080/api/videos/stream/" + video.getId());

                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/thumbnail/{id}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Long id) throws IOException {
        VideoFile video = videoFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        Path thumbnailPath = Paths.get(video.getThumbnailPath());

        if (!Files.exists(thumbnailPath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thumbnail not found");
        }

        Resource resource = new UrlResource(thumbnailPath.toUri());
        long fileSize = Files.size(thumbnailPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/webp"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400") // Cache for 1 day
                .contentLength(fileSize)
                .body(resource);
    }


    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable Long id,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) throws IOException {

        VideoFile video = videoFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Path videoPath = Paths.get(video.getMp4Path());
        Resource resource = new UrlResource(videoPath.toUri());

        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        long fileSize = Files.size(videoPath);

        // Handle range requests (for seeking in video)
        if (range != null && range.startsWith("bytes=")) {
            String[] ranges = range.substring(6).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 && !ranges[1].isEmpty()
                    ? Long.parseLong(ranges[1])
                    : fileSize - 1;

            if (start > end || start >= fileSize || end >= fileSize) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header("Content-Range", "bytes */" + fileSize)
                        .build();
            }

            long contentLength = end - start + 1;

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Range", "bytes " + start + "-" + end + "/" + fileSize)
                    .contentLength(contentLength)
                    .body(new RangeResource(resource, start, contentLength));
        }

        // Full file response
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header("Accept-Ranges", "bytes")
                .contentLength(fileSize)
                .body(resource);
    }


}
