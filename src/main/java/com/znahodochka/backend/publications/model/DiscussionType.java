package com.znahodochka.backend.publications.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum DiscussionType {
    MAIN(0),
    COMMENT(1);

    private final int value;

    DiscussionType(int value) {
        this.value = value;
    }

    public static DiscussionType fromValue(final int value) {
        for (final DiscussionType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }


    public static List<DiscussionType> fromListValue(final List<Integer> values) {
        return values.stream().map(DiscussionType::fromValue).toList();
    }
}
