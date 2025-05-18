package io.github.shazxrin.onepercentbetter.checkin.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.check-in")
public class CheckInProperties {
    public String bootstrapDate;

    public String getBootstrapDate() {
        return bootstrapDate;
    }

    public void setBootstrapDate(String bootstrapDate) {
        this.bootstrapDate = bootstrapDate;
    }
}
