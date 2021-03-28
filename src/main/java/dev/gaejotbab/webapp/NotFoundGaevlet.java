package dev.gaejotbab.webapp;

import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;

import java.util.Collections;

public class NotFoundGaevlet implements Gaevlet {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.setVersion(request.getVersion());
        response.setStatusCode(404);
        response.setStatusText("Not Found");
        response.setHeaders(Collections.emptyMap());
    }
}
