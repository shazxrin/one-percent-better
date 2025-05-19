package io.github.shazxrin.onepercentbetter.checkin.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.check-in")
public class CheckInProperties {
    private String bootstrapDate;

    private List<String> bootstrapProjects;

    public String getBootstrapDate() {
        return bootstrapDate;
    }

    public void setBootstrapDate(String bootstrapDate) {
        this.bootstrapDate = bootstrapDate;
    }

    public List<String> getBootstrapProjects() {
        return bootstrapProjects;
    }

    public void setBootstrapProjects(List<String> bootstrapProjects) {
        this.bootstrapProjects = bootstrapProjects;
    }
}
