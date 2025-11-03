package com.tecnocampus.LS2.protube_back.repositories;

import com.tecnocampus.LS2.protube_back.models.VideoFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVideoFileRepository extends JpaRepository<VideoFile, Long> {

}
