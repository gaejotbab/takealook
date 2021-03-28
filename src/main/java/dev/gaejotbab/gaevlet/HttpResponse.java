package dev.gaejotbab.gaevlet;

import java.util.Map;

public class HttpResponse {
    private final HttpVersion version;
    private final int statusCode;
    private final String statusText;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(HttpVersion version, int statusCode, String statusText, Map<String, String> headers, byte[] body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = headers;
        this.body = body;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private HttpVersion version;
        private int statusCode;
        private String statusText;
        private Map<String, String> headers;
        private byte[] body = null;

        public Builder setVersion(HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setStatusText(String statusText) {
            this.statusText = statusText;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(version, statusCode, statusText, headers, body);
        }
    }
}
