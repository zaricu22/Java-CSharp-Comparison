// VERDICT | T13 Rate limiting | BETTER: ASP.NET
// WHY: AddRateLimiter + RequireRateLimiting are built in and partitioned per client; Spring has no rate limiter, so it is a hand-written filter (or Bucket4j).

package shop.t13_ratelimiting;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** A fixed-window limiter per X-Client-Id, written by hand because Spring has none built in. */
@Component
public class SearchRateLimitFilter extends OncePerRequestFilter {

    private record Window(long startNanos, AtomicInteger used) {}

    private final int permits;
    private final long windowNanos;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    /** @Value (not ShopProperties) so the filter also works in @WebMvcTest slices, which load Filters but not properties beans. */
    public SearchRateLimitFilter(@Value("${shop.rate-limit.permits}") int permits,
                                 @Value("${shop.rate-limit.window}") Duration window) {
        this.permits = permits;
        this.windowNanos = window.toNanos();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/search");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String client = Objects.requireNonNullElse(request.getHeader("X-Client-Id"), "anonymous");
        long now = System.nanoTime();
        Window window = windows.compute(client, (key, current) ->
                current == null || now - current.startNanos() >= windowNanos ? new Window(now, new AtomicInteger()) : current);
        if (window.used().incrementAndGet() > permits) {
            response.setStatus(429);
            response.setContentType("application/problem+json");
            response.getWriter().write("{\"title\":\"Too Many Requests\",\"status\":429}");
            return;
        }
        chain.doFilter(request, response);
    }
}
