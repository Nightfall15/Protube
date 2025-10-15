package com.tecnocampus.LS2.protube_back.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class VideoFile {


    @Setter
    @Getter
    @Id private Long id;
    @Setter
    @Getter
    private String title;
    @Getter
    @Setter
    @Column(length = 5000) String description;
    @Getter
    @Setter
    private String userUploader;
    @Getter
    @Setter
    private ArrayList<String> comments;
    @Getter
    @Setter
    private String mp4Path;
    @Getter
    @Setter
    private String thumbnailPath;
    @Getter
    @Setter
    private String jsonPath;

    @Setter
    private ArrayList<String> tags;

    public void addComment(String comment) {
        comments.add(comment);
    }

}
