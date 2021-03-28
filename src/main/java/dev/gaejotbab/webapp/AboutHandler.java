package dev.gaejotbab.webapp;

import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AboutHandler {
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> responseHeaders = Map.of("Content-Type", "text/html; charset=UTF-8");

        String responseBodyString = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>떼껄룩 소개</title>
                </head>
                <body>
                    <h1>떼껄룩 소개</h1>
                    <p>톰캣 유사품이에요.</p>
                </body>
                </html>
                """;

        byte[] body = responseBodyString.getBytes(StandardCharsets.UTF_8);

        HttpResponse response = HttpResponse.newBuilder()
                .setVersion(request.getVersion())
                .setStatusCode(200)
                .setStatusText("OK")
                .setHeaders(responseHeaders)
                .setBody(body)
                .build();

        return response;
    }
}
