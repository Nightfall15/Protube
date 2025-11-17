package com.tecnocampus.LS2.protube_back.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Entity(name = "VideoFile")
public class VideoFile {


    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private String title;
    @Getter
    @Column(length = 5000) String description;
    @Getter
    private ArrayList<String> comments;
    @Getter
    private String mp4Path;
    @Getter
    private String thumbnailPath;
    @Getter
    private String jsonPath;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploader;

    private ArrayList<String> tags;

    public void addComment(String comment) {
        comments.add(comment);
    }

}
