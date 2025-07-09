package com.mimecast.architech.springk8sample.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqsMessage {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("message")
    private String message;
} 