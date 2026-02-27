package com.student.foundiit.service;

import com.student.foundiit.model.Claim;
import com.student.foundiit.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim submitClaim(Claim claim) {
        return claimRepository.save(claim);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public void updateClaimStatus(Long id, Claim.Status status) {
        claimRepository.findById(id).ifPresent(claim -> {
            claim.setStatus(status);
            claimRepository.save(claim);
        });
    }
}
