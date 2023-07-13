package com.rramsauer.iotlocatiotrackerapp.ui.view.util;

public class ListPreferenceElement {
    String value;
    String entry;

    /* Constructor */
    public ListPreferenceElement(String value, String entry) {
        this.value = value;
        this.entry = entry;
    }

    /* Getter */
    public String getValue() {
        return value;
    }

    /* Setter */
    public void setValue(String value) {
        this.value = value;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }
}
