// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import shop.t03_config.NotificationSenders.NotificationSender;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ConfigController {

    public record ConfigView(String currency, BigDecimal freeShippingOver, String notificationChannel, boolean recommendations) {}

    private final ShopProperties properties;
    private final NotificationSender notifications;
    private final ObjectProvider<RecommendationService> recommendations;

    public ConfigController(ShopProperties properties, NotificationSender notifications,
                            ObjectProvider<RecommendationService> recommendations) {
        this.properties = properties;
        this.notifications = notifications;
        this.recommendations = recommendations;
    }

    @GetMapping("/api/config")
    public ConfigView config() {
        return new ConfigView(properties.currency(), properties.freeShippingOver(),
                notifications.channel(), recommendations.getIfAvailable() != null);
    }

    @GetMapping("/api/recommendations/{sku}")
    public List<String> recommend(@PathVariable String sku) {
        var service = recommendations.getIfAvailable();
        if (service == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recommendations are disabled");
        }
        return service.recommendFor(sku);
    }
}
