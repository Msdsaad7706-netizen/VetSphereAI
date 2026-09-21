package org.saad.vetsphereai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHealthAdvisorResponse {

    private String triageLevel; // LOW, MODERATE, URGENT, EMERGENCY
    private String summary;
    private List<String> possibleConditions;
    private List<String> recommendedActions;
    private String dosageAssistantNotes;
    private String preventiveCareTips;
    private String disclaimer;
    private LocalDateTime timestamp;
}
