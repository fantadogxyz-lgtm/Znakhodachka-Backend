package com.znahodochka.backend.users.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum Role {
    BASIC(0);

    private final Integer value;

    Role(final Integer value) {
        this.value = value;
    }

    public static Role fromValue(final int value) {
        for (final Role e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    public int toInt() {
        return this.value;
    }
}
