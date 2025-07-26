package io.github.shazxrin.onepercentbetter.observability.configuration;

import net.ttddyy.observation.boot.autoconfigure.ProxyDataSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceMicrometerConfiguration {
    @Bean
    public ProxyDataSourceBuilderCustomizer myCustomizer() {
        return (builder, dataSource, beanName, dataSourceName) -> {
            builder.name("postgres");
        };
    }
}
