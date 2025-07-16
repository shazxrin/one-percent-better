package io.github.shazxrin.onepercentbetter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@Configuration
public class ApplicationConfiguration {
}
