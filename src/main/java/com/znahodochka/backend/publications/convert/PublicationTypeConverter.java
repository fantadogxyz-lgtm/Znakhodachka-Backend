package com.znahodochka.backend.publications.convert;

import com.znahodochka.backend.publications.model.PublicationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PublicationTypeConverter implements Converter<String, PublicationType> {
    @Override
    public PublicationType convert(String source) {
        try {
            int value = Integer.parseInt(source);
            return PublicationType.fromValue(value);
        } catch (NumberFormatException e) {
            return PublicationType.valueOf(source.toUpperCase());
        }
    }
}
