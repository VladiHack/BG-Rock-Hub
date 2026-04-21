package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.venue.VenueRequest;
import bg.sofia.bgrockHub.dto.venue.VenueResponse;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.Venue;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
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

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    @Transactional
    @CacheEvict(value = "venues", allEntries = true)
    public VenueResponse create(VenueRequest request) {
        User owner = getCurrentUser();
        Venue venue = Venue.builder()
                .name(request.name())
                .address(request.address())
                .city(request.city())
                .description(request.description())
                .capacity(request.capacity())
                .phone(request.phone())
                .website(request.website())
                .coverImgUrl(request.coverImgUrl())
                .owner(owner)
                .build();
        return toResponse(venueRepository.save(venue));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "venues", key = "#id")
    public VenueResponse getById(Long id) {
        return toResponse(findVenueById(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<VenueResponse> getAll(int page, int size, String city, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("avgRating").descending());
        Page<Venue> venues;
        if (search != null && !search.isBlank()) {
            venues = venueRepository.search(search, pageable);
        } else if (city != null) {
            venues = venueRepository.findByCity(city, pageable);
        } else {
            venues = venueRepository.findAll(pageable);
        }
        return toPageResponse(venues);
    }

    @Transactional
    @CacheEvict(value = "venues", allEntries = true)
    public VenueResponse update(Long id, VenueRequest request) {
        Venue venue = findVenueById(id);
        checkOwnership(venue);
        venue.setName(request.name());
        venue.setAddress(request.address());
        venue.setCity(request.city());
        venue.setDescription(request.description());
        venue.setCapacity(request.capacity());
        venue.setPhone(request.phone());
        venue.setWebsite(request.website());
        venue.setCoverImgUrl(request.coverImgUrl());
        return toResponse(venueRepository.save(venue));
    }

    @Transactional
    @CacheEvict(value = "venues", allEntries = true)
    public void delete(Long id) {
        Venue venue = findVenueById(id);
        checkOwnershipOrAdmin(venue);
        venueRepository.delete(venue);
    }

    private Venue findVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Клуб", id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен"));
    }

    private void checkOwnership(Venue venue) {
        User current = getCurrentUser();
        if (!venue.getOwner().getId().equals(current.getId())) {
            throw new UnauthorizedException("Само собственикът може да редактира");
        }
    }

    private void checkOwnershipOrAdmin(Venue venue) {
        User current = getCurrentUser();
        if (!venue.getOwner().getId().equals(current.getId()) && !current.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("Нямате права");
        }
    }

    private VenueResponse toResponse(Venue v) {
        return new VenueResponse(v.getId(), v.getName(), v.getAddress(), v.getCity(),
                v.getDescription(), v.getCapacity(), v.getPhone(), v.getWebsite(),
                v.getCoverImgUrl(), v.isVerified(), v.getAvgRating(), v.getTotalRatings(),
                v.getOwner().getId(), v.getOwner().getUsername(), v.getPhotos(), v.getCreatedAt());
    }

    private PageResponse<VenueResponse> toPageResponse(Page<Venue> page) {
        return new PageResponse<>(page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
