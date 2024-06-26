package com.optum.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime locDateTime) {
        return locDateTime == null ? null : locDateTime.format(FORMATTER);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String sqlTimestamp) {
        return sqlTimestamp == null ? null : LocalDateTime.parse(sqlTimestamp, FORMATTER);
    }
}
