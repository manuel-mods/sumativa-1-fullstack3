package dev.bast.foro.foros.repository;

import dev.bast.foro.foros.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findByUserIdAndActiveIsTrue(Long userId);

    List<Topic> findByActiveIsTrue();

    List<Topic> findByUserIdAndActiveTrue(Long userId);

    List<Topic> findByActiveIsTrueOrderByCreatedAtDesc();
}