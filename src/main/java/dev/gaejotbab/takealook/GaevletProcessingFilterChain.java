package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.FilterChain;
import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;

public class GaevletProcessingFilterChain implements FilterChain {
    private final Gaevlet gaevlet;

    public GaevletProcessingFilterChain(Gaevlet gaevlet) {
        this.gaevlet = gaevlet;
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) {
        gaevlet.service(request, response);
    }
}
