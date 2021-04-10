package dev.gaejotbab.gaevlet;

import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String target;
    private final HttpVersion version;

    private final Map<String, String> headers;

    private final byte[] body;

    private final String remoteAddr;
    private final int remotePort;

    public HttpRequest(
            HttpMethod method,
            String target,
            HttpVersion version,
            Map<String, String> headers,
            byte[] body,
            String remoteAddr,
            int remotePort) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.remoteAddr = remoteAddr;
        this.remotePort = remotePort;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private HttpMethod method;
        private String target;
        private HttpVersion version;
        private Map<String, String> headers;
        private byte[] body = null;
        private String remoteAddr;
        private int remotePort;

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

        public Builder setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
            return this;
        }

        public Builder setRemotePort(int remotePort) {
            this.remotePort = remotePort;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(method, target, version, headers, body, remoteAddr, remotePort);
        }
    }
}
