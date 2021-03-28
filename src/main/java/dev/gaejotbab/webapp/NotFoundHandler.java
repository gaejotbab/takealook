package dev.gaejotbab.webapp;

import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;

import java.util.Collections;

public class NotFoundHandler implements Gaevlet {
    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpResponse response = HttpResponse.newBuilder()
                .setVersion(request.getVersion())
                .setStatusCode(404)
                .setStatusText("Not Found")
                .setHeaders(Collections.emptyMap())
                .build();

        return response;
    }
}
