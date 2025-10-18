package com.tecnocampus.LS2.protube_back.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VideoDTO {
    private String title;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;

}
