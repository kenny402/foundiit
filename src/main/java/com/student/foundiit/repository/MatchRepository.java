package com.student.foundiit.repository;

import com.student.foundiit.model.Item;
import com.student.foundiit.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByLostItemOrFoundItemOrderByMatchScoreDesc(Item lost, Item found);
    
    List<Match> findTop5ByLostItemOrderByMatchScoreDesc(Item item);
    
    List<Match> findTop5ByFoundItemOrderByMatchScoreDesc(Item item);
}
