package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Filter;
import dev.gaejotbab.gaevlet.FilterChain;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {
        chain.doFilter(request, response);

        logger.info("{} {}:{} {}",
                request.getTarget(),
                request.getRemoteAddr(),
                request.getRemotePort(),
                response.getStatusCode());
    }
}
