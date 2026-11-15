package com.matchiq.matchiq.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class AiAnalysisService {

    private final RestClient restClient;

    public AiAnalysisService(
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.url}") String apiUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .build();
    }

    public String generateAnalysis(String message) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4.1-mini",
                "input", message
        );

        try {
            Map<?, ?> response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);

        } catch (RestClientException e) {
            throw new AiAnalysisException(
                    "AI analysis is temporarily unavailable."
            );

        } catch (IllegalStateException e) {
            throw new AiAnalysisException(
                    "AI analysis returned an invalid response."
            );
        }
    }

    private String extractText(Map<?, ?> response) {

        if (response == null) {
            throw new IllegalStateException(
                    "OpenAI returned an empty response"
            );
        }

        Object outputObject = response.get("output");

        if (!(outputObject instanceof List<?> output)) {
            throw new IllegalStateException(
                    "OpenAI response does not contain output"
            );
        }

        StringBuilder text = new StringBuilder();

        for (Object itemObject : output) {
            if (!(itemObject instanceof Map<?, ?> item)) {
                continue;
            }

            if (!(item.get("content") instanceof List<?> content)) {
                continue;
            }

            for (Object contentObject : content) {
                if (!(contentObject instanceof Map<?, ?> part)) {
                    continue;
                }

                if ("output_text".equals(part.get("type"))) {
                    Object value = part.get("text");

                    if (value instanceof String valueText) {
                        text.append(valueText);
                    }
                }
            }
        }

        if (text.isEmpty()) {
            throw new IllegalStateException(
                    "OpenAI returned no generated text"
            );
        }

        return text.toString();
    }
}