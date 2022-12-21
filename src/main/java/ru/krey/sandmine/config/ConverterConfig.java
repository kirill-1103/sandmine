package ru.krey.sandmine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;
import ru.krey.sandmine.converters.StringValueToDateTimeValueConverter;
import ru.krey.sandmine.converters.StringValueToDateValueConverter;

import java.util.Set;

@Configuration
public class ConverterConfig {
    @Bean
    Neo4jConversions neo4jConversions(){
        Set<GenericConverter> additionalConverters= Set.of(new StringValueToDateValueConverter(), new StringValueToDateTimeValueConverter());
        return new Neo4jConversions(additionalConverters);
    }
}
