package com.student.foundiit.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lost_item_id")
    private Item lostItem;

    @ManyToOne
    @JoinColumn(name = "found_item_id")
    private Item foundItem;

    @Column(name = "match_score")
    private int matchScore = 0;

    private boolean confirmed = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Match() {
    }

    public Match(Long id, Item lostItem, Item foundItem, int matchScore, boolean confirmed, LocalDateTime createdAt) {
        this.id = id;
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.matchScore = matchScore;
        this.confirmed = confirmed;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getLostItem() {
        return lostItem;
    }

    public void setLostItem(Item lostItem) {
        this.lostItem = lostItem;
    }

    public Item getFoundItem() {
        return foundItem;
    }

    public void setFoundItem(Item foundItem) {
        this.foundItem = foundItem;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Item lostItem;
        private Item foundItem;
        private int matchScore = 0;
        private boolean confirmed = false;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder lostItem(Item lostItem) {
            this.lostItem = lostItem;
            return this;
        }

        public Builder foundItem(Item foundItem) {
            this.foundItem = foundItem;
            return this;
        }

        public Builder matchScore(int matchScore) {
            this.matchScore = matchScore;
            return this;
        }

        public Builder confirmed(boolean confirmed) {
            this.confirmed = confirmed;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Match build() {
            return new Match(id, lostItem, foundItem, matchScore, confirmed, createdAt);
        }
    }
}
