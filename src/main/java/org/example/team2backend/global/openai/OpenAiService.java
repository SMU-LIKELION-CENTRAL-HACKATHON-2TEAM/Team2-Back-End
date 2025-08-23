package org.example.team2backend.global.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public String getChatCompletion(String prompt) throws IOException {

        String url = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 JSON 생성기다. 절대 설명 없이 JSON만 반환해야 한다."),
                        Map.of("role", "user", "content", prompt)),
                "max_tokens", 800
        );

        RequestBody requestBody = RequestBody.create(
                mapper.writeValueAsString(body),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API Error: " + response);
            }
            return response.body().string();
        }
    }

}
