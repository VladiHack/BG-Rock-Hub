package bg.sofia.bgrockHub.controller;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.band.BandRequest;
import bg.sofia.bgrockHub.dto.band.BandResponse;
import bg.sofia.bgrockHub.entity.enums.Genre;
import bg.sofia.bgrockHub.service.BandService;
import bg.sofia.bgrockHub.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bands")
@RequiredArgsConstructor
@Tag(name = "Bands", description = "Управление на банди")
public class BandController {

    private final BandService bandService;
    private final FollowService followService;

    @GetMapping
    @Operation(summary = "Списък с банди")
    public ResponseEntity<PageResponse<BandResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(bandService.getAll(page, size, genre, city, search));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Детайли за банда")
    public ResponseEntity<BandResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bandService.getById(id));
    }

    @GetMapping("/top")
    @Operation(summary = "Топ оценени банди")
    public ResponseEntity<List<BandResponse>> getTopRated(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(bandService.getTopRated(limit));
    }

    @GetMapping("/unknown")
    @Operation(summary = "Непознати банди — spotlight")
    public ResponseEntity<List<BandResponse>> getUnknown(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(bandService.getUnknownBands(limit));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BAND', 'ADMIN')")
    @Operation(summary = "Създай профил на банда")
    public ResponseEntity<BandResponse> create(@Valid @RequestBody BandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bandService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BAND', 'ADMIN')")
    @Operation(summary = "Редактирай банда")
    public ResponseEntity<BandResponse> update(@PathVariable Long id, @Valid @RequestBody BandRequest request) {
        return ResponseEntity.ok(bandService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BAND', 'ADMIN')")
    @Operation(summary = "Изтрий банда")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bandService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/follow")
    @Operation(summary = "Следвай / спри да следваш банда")
    public ResponseEntity<Map<String, Object>> toggleFollow(@PathVariable Long id) {
        return ResponseEntity.ok(followService.toggleFollow(id));
    }

    @GetMapping("/{id}/following")
    @Operation(summary = "Следвам ли тази банда?")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("following", followService.isFollowing(id)));
    }
}
