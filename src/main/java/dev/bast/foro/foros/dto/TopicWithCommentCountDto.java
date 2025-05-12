package dev.bast.foro.foros.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicWithCommentCountDto {

    private Integer id;
    private String title;
    private String content;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private int commentCount;

    // Constructor to create from a TopicDto and add the comment count
    public TopicWithCommentCountDto(TopicDto topicDto, int commentCount) {
        this.id = topicDto.getId();
        this.title = topicDto.getTitle();
        this.content = topicDto.getContent();
        this.userId = topicDto.getUserId();
        this.username = topicDto.getUsername();
        this.createdAt = topicDto.getCreatedAt();
        this.updatedAt = topicDto.getUpdatedAt();
        this.active = topicDto.isActive();
        this.commentCount = commentCount;
    }
}