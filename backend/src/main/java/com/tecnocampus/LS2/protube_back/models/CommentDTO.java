package com.tecnocampus.LS2.protube_back.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String author;
    private String text;
    private String createdAt;
}
