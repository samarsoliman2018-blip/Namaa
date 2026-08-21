package Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GroqTest {
    public static void main(String[] args) throws Exception {
        String groqKey = "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";
        String url = "https://api.groq.com/openai/v1/chat/completions";

        String requestBody = """
        {
          "model": "groq/compound-mini",
          "messages": [
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": "Hello Groq, can you introduce yourself briefly?"}
          ],
          "temperature": 0.7,
          "max_tokens": 200
        }
        """;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());

        // Quick parse to extract the assistant's reply
        String marker = "\"content\":\"";
        int start = response.body().indexOf(marker);
        if (start != -1) {
            start += marker.length();
            int end = response.body().indexOf("\"", start);
            if (end != -1) {
                String output = response.body().substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
                System.out.println("Groq says: " + output);
            }
        }
    }
}
