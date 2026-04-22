package com.student.foundiit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClaimDto {

    private Long itemId;

    @NotBlank(message = "Proof description is required")
    @Size(min = 20, max = 3000, message = "Proof description must be between 20 and 3000 characters")
    private String proofDescription;

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }
}
