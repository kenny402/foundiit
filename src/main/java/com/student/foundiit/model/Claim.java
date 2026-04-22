package com.student.foundiit.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "claimant_id")
    private User claimant;

    @Column(name = "proof_description", columnDefinition = "TEXT")
    private String proofDescription;

    @Column(name = "proof_image_path")
    private String proofImagePath;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "review_notes")
    private String reviewNotes;

    @ManyToOne
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "claim_date")
    private LocalDate claimDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Claim() {}

    public Claim(Long id, Item item, User claimant, String proofDescription, String proofImagePath, ClaimStatus status, String reviewNotes, User reviewedBy, LocalDateTime reviewedAt, LocalDate claimDate, LocalDateTime createdAt) {
        this.id = id;
        this.item = item;
        this.claimant = claimant;
        this.proofDescription = proofDescription;
        this.proofImagePath = proofImagePath;
        this.status = status;
        this.reviewNotes = reviewNotes;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.claimDate = claimDate;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getClaimant() {
        return claimant;
    }

    public void setClaimant(User claimant) {
        this.claimant = claimant;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }

    public String getProofImagePath() {
        return proofImagePath;
    }

    public void setProofImagePath(String proofImagePath) {
        this.proofImagePath = proofImagePath;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
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
        private Item item;
        private User claimant;
        private String proofDescription;
        private String proofImagePath;
        private ClaimStatus status = ClaimStatus.PENDING;
        private String reviewNotes;
        private User reviewedBy;
        private LocalDateTime reviewedAt;
        private LocalDate claimDate;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder item(Item item) {
            this.item = item;
            return this;
        }

        public Builder claimant(User claimant) {
            this.claimant = claimant;
            return this;
        }

        public Builder proofDescription(String proofDescription) {
            this.proofDescription = proofDescription;
            return this;
        }

        public Builder proofImagePath(String proofImagePath) {
            this.proofImagePath = proofImagePath;
            return this;
        }

        public Builder status(ClaimStatus status) {
            this.status = status;
            return this;
        }

        public Builder reviewNotes(String reviewNotes) {
            this.reviewNotes = reviewNotes;
            return this;
        }

        public Builder reviewedBy(User reviewedBy) {
            this.reviewedBy = reviewedBy;
            return this;
        }

        public Builder reviewedAt(LocalDateTime reviewedAt) {
            this.reviewedAt = reviewedAt;
            return this;
        }

        public Builder claimDate(LocalDate claimDate) {
            this.claimDate = claimDate;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Claim build() {
            return new Claim(id, item, claimant, proofDescription, proofImagePath, status, reviewNotes, reviewedBy, reviewedAt, claimDate, createdAt);
        }
    }
}
