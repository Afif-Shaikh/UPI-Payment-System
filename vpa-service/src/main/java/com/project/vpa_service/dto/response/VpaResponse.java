package com.project.vpa_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VpaResponse {

    private String id;  // e.g., VPA100001
    private String userId;  // e.g., U100001
    private String vpaHandle;  // e.g., "rahul"
    private String vpaAddress;  // e.g., "rahul@okaxis"
    private String pspId;
    private String pspName;
    private String pspHandle;
    private String linkedAccountId;  // e.g., A100001SBISAV
    private Boolean isPrimary;
    private Boolean isVerified;
    private Boolean active;
    private LocalDateTime createdAt;
}