package dev.gaejotbab.webapp;

import dev.gaejotbab.gaevlet.HttpGaevlet;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HomeGaevlet extends HttpGaevlet {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        Map<String, String> responseHeaders = Map.of("Content-Type", "text/html; charset=UTF-8");

        String responseBodyString = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>떼껄룩 테스트 페이지</title>
                </head>
                <body>
                    <h1>떼껄룩 테스트 페이지</h1>
                    <p>별 거 없습니다.</p>
                    <p><a href="/about">소개</a></p>
                </body>
                </html>
                """;

        byte[] body = responseBodyString.getBytes(StandardCharsets.UTF_8);

        response.setVersion(request.getVersion());
        response.setStatusCode(200);
        response.setStatusText("OK");
        response.setHeaders(responseHeaders);
        response.setBody(body);
    }
}
