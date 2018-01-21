package org.bluebird.UserInterface.App;

public enum languageOption {
    CSharp;

    languageOption() {}

    public String value() {
        return name();
    }

    public static languageOption fromValue(String v) {
        return valueOf(v);
    }
}
