// VERDICT | T03 Configuration & profiles | BETTER: SPRING
// WHY: @Profile and @ConditionalOnProperty swap beans declaratively; ASP.NET binds options just as well, but conditional registration is hand-written if/else.

package shop.t03_config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Which implementation exists is decided by the active profile - no if/else anywhere. */
public final class NotificationSenders {

    private NotificationSenders() {}

    public interface NotificationSender {
        String channel();
    }

    @Component
    @Profile("!prod")
    static class LogNotificationSender implements NotificationSender {
        @Override
        public String channel() {
            return "log";
        }
    }

    @Component
    @Profile("prod")
    static class SmtpNotificationSender implements NotificationSender {
        @Override
        public String channel() {
            return "smtp";
        }
    }
}
