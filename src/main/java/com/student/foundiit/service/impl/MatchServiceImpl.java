package com.student.foundiit.service.impl;

import com.student.foundiit.model.Item;
import com.student.foundiit.model.ItemStatus;
import com.student.foundiit.model.Match;
import com.student.foundiit.repository.ItemRepository;
import com.student.foundiit.repository.MatchRepository;
import com.student.foundiit.service.MatchService;
import com.student.foundiit.service.NotificationService;
import com.student.foundiit.service.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public MatchServiceImpl(MatchRepository matchRepository, ItemRepository itemRepository,
            NotificationService notificationService, EmailService emailService) {
        this.matchRepository = matchRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @Override
    public List<Match> findMatches(Item newItem) {
        ItemStatus targetStatus = (newItem.getStatus() == ItemStatus.LOST) ? ItemStatus.FOUND : ItemStatus.LOST;
        List<Item> candidates = itemRepository.findByStatusAndCategory(targetStatus, newItem.getCategory());

        List<Match> matches = new ArrayList<>();

        for (Item candidate : candidates) {
            int score = calculateScore(newItem, candidate);
            if (score >= 40) {
                Match match = Match.builder()
                        .lostItem(newItem.getStatus() == ItemStatus.LOST ? newItem : candidate)
                        .foundItem(newItem.getStatus() == ItemStatus.FOUND ? newItem : candidate)
                        .matchScore(score)
                        .build();

                Match savedMatch = matchRepository.save(match);
                matches.add(savedMatch);

                // Notify both owners
                notificationService.notifyMatch(savedMatch);
                emailService.sendMatchFoundEmail(savedMatch);
            }
        }

        return matches;
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public List<Match> getMatchesForItem(Item item) {
        if (item.getStatus() == ItemStatus.LOST) {
            return matchRepository.findTop5ByLostItemOrderByMatchScoreDesc(item);
        } else {
            return matchRepository.findTop5ByFoundItemOrderByMatchScoreDesc(item);
        }
    }

    @Override
    public long countTotalMatches() {
        return matchRepository.count();
    }

    private int calculateScore(Item item1, Item item2) {
        int score = 0;

        // Category match (already filtered in findMatches, but adding for completeness)
        if (item1.getCategory().equals(item2.getCategory())) {
            score += 30;
        }

        // Keyword overlap in title and description
        score += calculateKeywordOverlap(item1, item2);

        // Location match
        if (item1.getLocation() != null && item2.getLocation() != null &&
                item1.getLocation().equalsIgnoreCase(item2.getLocation())) {
            score += 20;
        }

        // Date within 30 days
        if (item1.getDateCreated() != null && item2.getDateCreated() != null) {
            long daysApart = Math.abs(ChronoUnit.DAYS.between(item1.getDateCreated(), item2.getDateCreated()));
            if (daysApart <= 30) {
                score += 10;
            }
        }

        return Math.min(score, 100);
    }

    private int calculateKeywordOverlap(Item item1, Item item2) {
        String text1 = (item1.getTitle() + " " + item1.getDescription()).toLowerCase();
        String text2 = (item2.getTitle() + " " + item2.getDescription()).toLowerCase();

        Set<String> words1 = Arrays.stream(text1.split("\\W+"))
                .filter(w -> w.length() > 3).collect(Collectors.toSet());
        Set<String> words2 = Arrays.stream(text2.split("\\W+"))
                .filter(w -> w.length() > 3).collect(Collectors.toSet());

        long overlap = words1.stream().filter(words2::contains).count();
        return (int) Math.min(overlap * 10, 40);
    }
}
