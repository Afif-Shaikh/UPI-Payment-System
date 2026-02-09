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
public class PspResponse {

    private String id;
    private String pspName;
    private String pspHandle;
    private String bankName;
    private String bankIfscPrefix;
    private String logoUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}