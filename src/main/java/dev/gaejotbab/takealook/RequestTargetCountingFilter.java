package dev.gaejotbab.takealook;

import dev.gaejotbab.gaevlet.Gaevlet;
import dev.gaejotbab.gaevlet.HttpRequest;
import dev.gaejotbab.gaevlet.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestTargetCountingFilter {
    private final Logger logger = LoggerFactory.getLogger(RequestTargetCountingFilter.class);

    private ConcurrentMap<String, Integer> requestTargetCounter = new ConcurrentHashMap<>();

    private AtomicInteger requestCounter = new AtomicInteger(0);

    private AccessLogFilter accessLogFilter = new AccessLogFilter();

    public void process(Gaevlet gaevlet, HttpRequest request, HttpResponse response) {
        requestTargetCounter.compute(
                request.getTarget(),
                (target, previousValue) -> previousValue == null ? 1 : previousValue + 1);

        if (requestCounter.incrementAndGet() % 10 == 0) {
            logger.info("Request target count: {}", requestTargetCounter);
        }

        // gaevlet.service(request, response);
        accessLogFilter.process(gaevlet, request, response);
    }
}
