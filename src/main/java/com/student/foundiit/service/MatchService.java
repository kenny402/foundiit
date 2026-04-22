package com.student.foundiit.service;

import com.student.foundiit.model.Item;
import com.student.foundiit.model.Match;

import java.util.List;

public interface MatchService {
    List<Match> findMatches(Item newItem);
    List<Match> getMatchesForItem(Item item);
    long countTotalMatches();
}
