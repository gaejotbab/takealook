package dev.gaejotbab.gaevlet;

public interface FilterChain {
    void doFilter(HttpRequest request, HttpResponse response);
}
