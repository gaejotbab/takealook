package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogFilter {
    private final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

    public void process(Gaevlet gaevlet, HttpRequest request, HttpResponse response) {
        gaevlet.service(request, response);

        logger.info("{} {}:{} {}",
                request.getTarget(),
                request.getRemoteAddr(),
                request.getRemotePort(),
                response.getStatusCode());
    }
}
