package com.znahodochka.backend.publications.convert;

import com.znahodochka.backend.publications.model.DiscussionType;
import org.springframework.core.convert.converter.Converter;

public class DiscussionTypeConverter implements Converter<String, DiscussionType> {
    @Override
    public DiscussionType convert(String source) {
        try {
            int value = Integer.parseInt(source);
            return DiscussionType.fromValue(value);
        } catch (NumberFormatException e) {
            return DiscussionType.valueOf(source.toUpperCase());
        }
    }
}
