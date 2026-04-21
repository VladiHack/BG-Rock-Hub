package bg.sofia.bgrockHub.controller;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.event.EventRequest;
import bg.sofia.bgrockHub.dto.event.EventResponse;
import bg.sofia.bgrockHub.entity.enums.EventStatus;
import bg.sofia.bgrockHub.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Управление на концерти и фестивали")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(eventService.getAll(page, size, status, city, search));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<PageResponse<EventResponse>> getUpcoming(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(eventService.getUpcoming(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BAND', 'VENUE', 'ADMIN')")
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('BAND', 'VENUE', 'ADMIN')")
    public ResponseEntity<EventResponse> update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @PostMapping("/{id}/attend")
    @Operation(summary = "Ще присъствам / не присъствам")
    public ResponseEntity<EventResponse> toggleAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.toggleAttendance(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('BAND', 'VENUE', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
