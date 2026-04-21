package bg.sofia.bgrockHub.dto.admin;

public record AdminStatsResponse(
        long totalUsers,
        long totalBands,
        long totalVenues,
        long totalEvents,
        long totalReviews,
        long pendingReviews
) {}
