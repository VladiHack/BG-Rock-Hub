package bg.sofia.bgrockHub.controller;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.admin.AdminStatsResponse;
import bg.sofia.bgrockHub.dto.admin.AdminUserResponse;
import bg.sofia.bgrockHub.dto.band.BandResponse;
import bg.sofia.bgrockHub.dto.review.ReviewResponse;
import bg.sofia.bgrockHub.dto.venue.VenueResponse;
import bg.sofia.bgrockHub.entity.enums.Role;
import bg.sofia.bgrockHub.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Административни операции — само за ADMIN роля")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    // ─── Stats ───────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    @Operation(summary = "Статистики за платформата")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ─── Users ───────────────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "Всички потребители с пагинация")
    public ResponseEntity<PageResponse<AdminUserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getUsers(page, size));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Промяна на роля на потребител")
    public ResponseEntity<AdminUserResponse> changeUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        return ResponseEntity.ok(adminService.changeUserRole(id, role));
    }

    @PutMapping("/users/{id}/active")
    @Operation(summary = "Активиране / деактивиране на потребител")
    public ResponseEntity<AdminUserResponse> toggleUserActive(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleUserActive(id));
    }

    // ─── Bands ───────────────────────────────────────────────────────────────

    @GetMapping("/bands")
    @Operation(summary = "Всички банди с пагинация")
    public ResponseEntity<PageResponse<BandResponse>> getBands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getBands(page, size));
    }

    @PutMapping("/bands/{id}/verify")
    @Operation(summary = "Верифициране / отмяна на верификация на банда")
    public ResponseEntity<BandResponse> verifyBand(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.verifyBand(id));
    }

    @DeleteMapping("/bands/{id}")
    @Operation(summary = "Изтриване на банда")
    public ResponseEntity<Void> deleteBand(@PathVariable Long id) {
        adminService.deleteBand(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Venues ──────────────────────────────────────────────────────────────

    @GetMapping("/venues")
    @Operation(summary = "Всички клубове с пагинация")
    public ResponseEntity<PageResponse<VenueResponse>> getVenues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getVenues(page, size));
    }

    @PutMapping("/venues/{id}/verify")
    @Operation(summary = "Верифициране / отмяна на верификация на клуб")
    public ResponseEntity<VenueResponse> verifyVenue(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.verifyVenue(id));
    }

    // ─── Reviews ─────────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    @Operation(summary = "Всички ревюта (включително неодобрени)")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean approved) {
        return ResponseEntity.ok(adminService.getReviews(page, size, approved));
    }

    @PutMapping("/reviews/{id}/approve")
    @Operation(summary = "Одобряване / отмяна на одобрение на ревю")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.approveReview(id));
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Изтриване на ревю")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
