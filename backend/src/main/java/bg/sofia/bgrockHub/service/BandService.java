package bg.sofia.bgrockHub.service;

import bg.sofia.bgrockHub.dto.PageResponse;
import bg.sofia.bgrockHub.dto.band.BandRequest;
import bg.sofia.bgrockHub.dto.band.BandResponse;
import bg.sofia.bgrockHub.entity.Band;
import bg.sofia.bgrockHub.entity.User;
import bg.sofia.bgrockHub.entity.enums.Genre;
import bg.sofia.bgrockHub.entity.enums.ReviewTargetType;
import bg.sofia.bgrockHub.exception.ResourceNotFoundException;
import bg.sofia.bgrockHub.exception.UnauthorizedException;
import bg.sofia.bgrockHub.repository.BandRepository;
import bg.sofia.bgrockHub.repository.FollowRepository;
import bg.sofia.bgrockHub.repository.ReviewRepository;
import bg.sofia.bgrockHub.repository.UserRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class BandService {

    private final BandRepository bandRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    @CacheEvict(value = {"bands", "topBands"}, allEntries = true)
    public BandResponse create(BandRequest request) {
        User owner = getCurrentUser();
        Band band = Band.builder()
                .name(request.name())
                .genre(request.genre())
                .description(request.description())
                .city(request.city())
                .foundedYear(request.foundedYear())
                .avatarUrl(request.avatarUrl())
                .spotifyUrl(request.spotifyUrl())
                .youtubeUrl(request.youtubeUrl())
                .facebookUrl(request.facebookUrl())
                .instagramUrl(request.instagramUrl())
                .members(request.members())
                .owner(owner)
                .build();
        return toResponse(bandRepository.save(band));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "bands", key = "#id")
    public BandResponse getById(Long id) {
        return toResponse(findBandById(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<BandResponse> getAll(int page, int size, Genre genre, String city, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("avgRating").descending());
        Page<Band> bands;
        if (search != null && !search.isBlank()) {
            bands = bandRepository.search(search, pageable);
        } else if (genre != null && city != null) {
            bands = bandRepository.findByGenreAndCity(genre, city, pageable);
        } else if (genre != null) {
            bands = bandRepository.findByGenre(genre, pageable);
        } else if (city != null) {
            bands = bandRepository.findByCity(city, pageable);
        } else {
            bands = bandRepository.findAll(pageable);
        }
        return toPageResponse(bands);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topBands")
    public List<BandResponse> getTopRated(int limit) {
        return bandRepository.findTopRated(PageRequest.of(0, limit))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<BandResponse> getUnknownBands(int limit) {
        return bandRepository.findUnknownBands(PageRequest.of(0, limit))
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    @CacheEvict(value = {"bands", "topBands"}, allEntries = true)
    public BandResponse update(Long id, BandRequest request) {
        Band band = findBandById(id);
        checkOwnership(band);
        band.setName(request.name());
        band.setGenre(request.genre());
        band.setDescription(request.description());
        band.setCity(request.city());
        band.setFoundedYear(request.foundedYear());
        band.setAvatarUrl(request.avatarUrl());
        band.setSpotifyUrl(request.spotifyUrl());
        band.setYoutubeUrl(request.youtubeUrl());
        band.setFacebookUrl(request.facebookUrl());
        band.setInstagramUrl(request.instagramUrl());
        band.setMembers(request.members());
        return toResponse(bandRepository.save(band));
    }

    @Transactional
    @CacheEvict(value = {"bands", "topBands"}, allEntries = true)
    public void delete(Long id) {
        Band band = findBandById(id);
        checkOwnershipOrAdmin(band);
        bandRepository.delete(band);
    }

    private Band findBandById(Long id) {
        return bandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Банда", id));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Потребителят не е намерен"));
    }

    private void checkOwnership(Band band) {
        User current = getCurrentUser();
        if (!band.getOwner().getId().equals(current.getId())) {
            throw new UnauthorizedException("Само собственикът може да редактира тази банда");
        }
    }

    private void checkOwnershipOrAdmin(Band band) {
        User current = getCurrentUser();
        boolean isAdmin = current.getRole().name().equals("ADMIN");
        if (!band.getOwner().getId().equals(current.getId()) && !isAdmin) {
            throw new UnauthorizedException("Нямате права да изтриете тази банда");
        }
    }

    private BandResponse toResponse(Band band) {
        long followersCount = followRepository.countByBandId(band.getId());
        return new BandResponse(
                band.getId(), band.getName(), band.getGenre(), band.getDescription(),
                band.getCity(), band.getFoundedYear(), band.getAvatarUrl(),
                band.getSpotifyUrl(), band.getYoutubeUrl(), band.getFacebookUrl(), band.getInstagramUrl(),
                band.getMembers(), band.isVerified(), band.getAvgRating(), band.getTotalRatings(),
                band.getOwner().getId(), band.getOwner().getUsername(),
                band.getPhotos(), followersCount, band.getCreatedAt()
        );
    }

    private PageResponse<BandResponse> toPageResponse(Page<Band> page) {
        return new PageResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
