package ru.etu.sport.attribute;

public enum DeleteValueOption {
    FULL ("full"),
    INT ("int"),
    STRING ("string"),
    IMAGE ("image");

    private String value;

    DeleteValueOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
