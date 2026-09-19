package com.amrsamy.relateai.adapters.ai;

import com.amrsamy.relateai.application.port.AiClientPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText('${relateai.openai.api-key:}')")
public class OpenAiClient implements AiClientPort {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);

    private final String apiKey;
    private final String model;
    private final MockAiClient fallback;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiClient(
            @Value("${relateai.openai.api-key:}") String apiKey,
            @Value("${relateai.openai.model:gpt-4o-mini}") String model,
            MockAiClient fallback,
            ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.model = model;
        this.fallback = fallback;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    @Override
    public GeneratedChallenge generateDailyChallenge(String dateIso) {
        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "temperature", 0.8,
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", "You write short, warm daily relationship challenges for a community app. One paragraph, max 60 words. No hashtags."),
                            Map.of(
                                    "role", "user",
                                    "content", "Generate today's challenge for date " + dateIso + ".")
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                String content = root.path("choices").path(0).path("message").path("content").asText("").trim();
                if (!content.isBlank()) {
                    return new GeneratedChallenge(content, "OPENAI");
                }
            }
            log.warn("OpenAI returned status {} — falling back to mock", response.statusCode());
        } catch (Exception ex) {
            log.warn("OpenAI call failed — falling back to mock: {}", ex.getMessage());
        }
        GeneratedChallenge mock = fallback.generateDailyChallenge(dateIso);
        return new GeneratedChallenge(mock.prompt(), "MOCK_FALLBACK");
    }
}
