package com.skillbridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MentorSuggestionDto {
    private PersonSummaryDto mentor;
    private String skillLevel;
    private String connectingProjectName;
    private long endorsementCount;
}
