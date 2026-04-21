package bg.sofia.bgrockHub.dto.news;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record NewsRequest(
        @NotBlank String title,
        @NotBlank String content,
        String coverImgUrl,
        List<String> tags
) {}
