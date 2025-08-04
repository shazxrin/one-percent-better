package io.github.shazxrin.onepercentbetter.checkin.core.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.check-in.project")
public class CheckInProjectProperties {
    public static class Bootstrap {
        private boolean enabled;
        private String date;
        private List<String> projects;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<String> getProjects() {
            return projects;
        }

        public void setProjects(List<String> projects) {
            this.projects = projects;
        }
    }

    private String scheduleCron;
    private Bootstrap bootstrap;

    public String getScheduleCron() {
        return scheduleCron;
    }

    public void setScheduleCron(String scheduleCron) {
        this.scheduleCron = scheduleCron;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }
}
