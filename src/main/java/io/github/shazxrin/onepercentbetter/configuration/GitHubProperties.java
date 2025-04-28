package io.github.shazxrin.onepercentbetter.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("github")
public class GitHubProperties {
   private String token;
   private String username;
}
