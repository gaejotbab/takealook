package dev.gaejotbab.gaevlet;

public enum HttpVersion {
    VERSION_1_1("HTTP/1.1"),
    VERSION_2(null),
    ;

    private final String versionString;

    HttpVersion(String versionString) {
        this.versionString = versionString;
    }

    public String getVersionString() {
        return versionString;
    }
}
