package com.skillbridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSummaryDto {
    private String id;
    private String name;
    private String title;
    private String location;
}
