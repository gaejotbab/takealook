package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Filter;
import dev.gaejotbab.gaevlet.FilterChain;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;

public class DefaultFilterChain implements FilterChain {
    private final Filter filter;
    private final FilterChain nextFilterChain;

    public DefaultFilterChain(Filter filter, FilterChain nextFilterChain) {
        this.filter = filter;
        this.nextFilterChain = nextFilterChain;
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) {
        filter.doFilter(request, response, nextFilterChain);
    }
}
