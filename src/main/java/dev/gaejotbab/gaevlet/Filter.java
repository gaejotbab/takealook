package dev.gaejotbab.gaevlet;

public interface Filter {
    void doFilter(HttpRequest request, HttpResponse response, FilterChain chain);
}
