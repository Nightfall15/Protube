package com.tecnocampus.LS2.protube_back.repositories;

import com.tecnocampus.LS2.protube_back.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ICommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideoIdOrderByCreatedAtAsc(Long videoId);
}
