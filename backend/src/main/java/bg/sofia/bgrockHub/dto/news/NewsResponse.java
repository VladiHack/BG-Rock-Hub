package bg.sofia.bgrockHub.dto.news;

import java.time.LocalDateTime;
import java.util.List;

public record NewsResponse(
        Long id,
        String title,
        String content,
        String coverImgUrl,
        List<String> tags,
        Integer likesCount,
        boolean isPublished,
        Long authorId,
        String authorUsername,
        String authorAvatarUrl,
        LocalDateTime createdAt
) {}
