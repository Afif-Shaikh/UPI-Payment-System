package com.project.vpa_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VpaVerificationResponse {

    private String vpaAddress;  // e.g., "rahul@okaxis"
    private Boolean exists;
    private Boolean active;
    private String accountHolderName;  // Masked name, e.g., "R***l K***r"
    private String pspName;  // e.g., "Google Pay"

    /**
     * Mask name for privacy
     * Example: "Rahul Kumar" -> "R***l K***r"
     */
    public static String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }

        String[] parts = name.split(" ");
        StringBuilder masked = new StringBuilder();

        for (String part : parts) {
            if (part.length() <= 2) {
                masked.append(part);
            } else {
                masked.append(part.charAt(0))
                        .append("*".repeat(part.length() - 2))
                        .append(part.charAt(part.length() - 1));
            }
            masked.append(" ");
        }

        return masked.toString().trim();
    }
}