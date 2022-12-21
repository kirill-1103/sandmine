package ru.krey.sandmine.converters;

import org.neo4j.driver.internal.value.DateTimeValue;
import org.neo4j.driver.internal.value.LocalDateTimeValue;
import org.neo4j.driver.internal.value.StringValue;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class StringValueToDateTimeValueConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<GenericConverter.ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new ConvertiblePair(StringValue.class, DateTimeValue.class));
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if(StringValue.class.isAssignableFrom(sourceType.getType())){
            String stringSource = ((StringValue)source).asString();
            LocalDateTime localDateTime = LocalDateTime.parse(stringSource.substring(0,stringSource.indexOf("+")));
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
            return new DateTimeValue(zonedDateTime);
        }
        return null;
    }
}
