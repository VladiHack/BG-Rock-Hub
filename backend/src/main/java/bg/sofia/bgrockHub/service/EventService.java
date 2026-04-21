package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.event.EventRequest;
import bg.sofia.bgrockHub.dto.event.EventResponse;
import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.Event;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.Venue;
import bg.sofia.bgrockHub.entity.enums.EventStatus;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.BandRepository;
import bg.sofia.bgrockHub.repository.EventRepository;
import bg.sofia.bgrockHub.repository.UserRepository;
import bg.sofia.bgrockHub.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final BandRepository bandRepository;
    private final BandService bandService;
    private final VenueService venueService;

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse create(EventRequest request) {
        User organizer = getCurrentUser();
        Venue venue = request.venueId() != null ? venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("Клуб", request.venueId())) : null;

        Set<Band> bands = new HashSet<>();
        if (request.bandIds() != null) {
            for (Long bandId : request.bandIds()) {
                bands.add(bandRepository.findById(bandId)
                        .orElseThrow(() -> new ResourceNotFoundException("Банда", bandId)));
            }
        }

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .eventDate(request.eventDate())
                .city(request.city())
                .ticketPrice(request.ticketPrice())
                .coverImgUrl(request.coverImgUrl())
                .genre(request.genre())
                .venue(venue)
                .organizer(organizer)
                .bands(bands)
                .build();

        return toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "#id")
    public EventResponse getById(Long id) {
        return toResponse(findEventById(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getAll(int page, int size, EventStatus status, String city, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("eventDate").ascending());
        Page<Event> events;
        if (search != null && !search.isBlank()) {
            events = eventRepository.search(search, pageable);
        } else if (status != null && city != null) {
            events = eventRepository.findByStatusAndCity(status, city, pageable);
        } else if (status != null) {
            events = eventRepository.findByStatus(status, pageable);
        } else if (city != null) {
            events = eventRepository.findByCity(city, pageable);
        } else {
            events = eventRepository.findAll(pageable);
        }
        return toPageResponse(events);
    }

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getUpcoming(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return toPageResponse(eventRepository.findUpcoming(LocalDateTime.now(), pageable));
    }

    @Transactional
    public EventResponse toggleAttendance(Long id) {
        Event event = findEventById(id);
        User user = getCurrentUser();
        if (event.getAttendees().contains(user)) {
            event.getAttendees().remove(user);
            event.setInterestedCount(Math.max(0, event.getInterestedCount() - 1));
        } else {
            event.getAttendees().add(user);
            event.setInterestedCount(event.getInterestedCount() + 1);
        }
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse update(Long id, EventRequest request) {
        Event event = findEventById(id);
        User current = getCurrentUser();
        if (!event.getOrganizer().getId().equals(current.getId()) && !current.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("Нямате права да редактирате това събитие");
        }
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());
        event.setCity(request.city());
        event.setTicketPrice(request.ticketPrice());
        event.setCoverImgUrl(request.coverImgUrl());
        event.setGenre(request.genre());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public void delete(Long id) {
        Event event = findEventById(id);
        User current = getCurrentUser();
        if (!event.getOrganizer().getId().equals(current.getId()) && !current.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("Нямате права да изтриете това събитие");
        }
        eventRepository.delete(event);
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Събитие", id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен"));
    }

    private EventResponse toResponse(Event e) {
        var bandsResp = e.getBands().stream().map(b -> bandService.getById(b.getId())).collect(java.util.stream.Collectors.toSet());
        var venueResp = e.getVenue() != null ? venueService.getById(e.getVenue().getId()) : null;
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getEventDate(),
                e.getCity(), e.getTicketPrice(), e.getCoverImgUrl(), e.getStatus(), e.getGenre(),
                e.getInterestedCount(), e.getAvgRating(), e.getTotalRatings(),
                venueResp, e.getOrganizer().getId(), e.getOrganizer().getUsername(),
                bandsResp, e.getPhotos(), e.getCreatedAt());
    }

    private PageResponse<EventResponse> toPageResponse(Page<Event> page) {
        return new PageResponse<>(page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
