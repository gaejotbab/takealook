package dev.gaejotbab.gaevlet;

public abstract class HttpGaevlet implements Gaevlet {
    protected void doDelete(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doDelete is not implmented.");
    }

    protected void doGet(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doGet is not implmented.");
    }

    protected void doHead(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doHead is not implmented.");
    }

    protected void doOptions(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doOptions is not implmented.");
    }

    protected void doPost(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doPost is not implmented.");
    }

    protected void doPut(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doPut is not implmented.");
    }

    protected void doTrace(HttpRequest req, HttpResponse resp) {
        throw new UnsupportedOperationException("doTrace is not implmented.");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        switch (request.getMethod()) {
            case DELETE -> doDelete(request, response);
            case GET -> doGet(request, response);
            case HEAD -> doHead(request, response);
            case OPTIONS -> doOptions(request, response);
            case POST -> doPost(request, response);
            case PUT -> doPut(request, response);
            case TRACE -> doTrace(request, response);
            default -> throw new UnsupportedOperationException("Method is not supported.");
        }
    }
}
