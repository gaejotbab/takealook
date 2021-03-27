package dev.gaejotbab.gaevlet.http;

import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String target;
    private final HttpVersion version;

    private final Map<String, String> headers;

    private final byte[] body;

    public HttpRequest(HttpMethod method, String target, HttpVersion version, Map<String, String> headers, byte[] body) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static class Builder {
        private HttpMethod method;
        private String target;
        private HttpVersion version;
        private Map<String, String> headers;
        private byte[] body = null;

        public Builder setMethod(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder setTarget(String target) {
            this.target = target;
            return this;
        }

        public Builder setVersion(HttpVersion version) {
            this.version = version;
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

        public HttpRequest build() {
            return new HttpRequest(method, target, version, headers, body);
        }
    }
}
