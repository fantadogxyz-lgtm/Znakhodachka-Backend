package com.znahodochka.backend.publications.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum PublicationType {
    POST(0),  //обычный базированный пост, хы
    CHALLENGE(1), //это для поста который задает члнж
    CHALLENGE_RESPONSE(2); //это для тех что там участвует
    private final int value;

    PublicationType(final int value) {
        this.value = value;
    }

    public static PublicationType fromValue(final int value) {
        for (final PublicationType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }


    public static List<PublicationType> fromListValue(final List<Integer> values) {
        return values.stream().map(PublicationType::fromValue).toList();
    }

}
