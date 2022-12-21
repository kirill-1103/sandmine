package ru.krey.sandmine.converters;

import org.neo4j.driver.internal.value.DateValue;
import org.neo4j.driver.internal.value.StringValue;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class StringValueToDateValueConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<GenericConverter.ConvertiblePair> convertiblePairs = new HashSet<>();
        convertiblePairs.add(new GenericConverter.ConvertiblePair(StringValue.class, DateValue.class));
        return convertiblePairs;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if(StringValue.class.isAssignableFrom(sourceType.getType())){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatter = formatter.withLocale(Locale.getDefault());
            LocalDate date = LocalDate.parse(((StringValue)source).asString(), formatter);
            return new DateValue(date);
        }
        return null;
    }
}
