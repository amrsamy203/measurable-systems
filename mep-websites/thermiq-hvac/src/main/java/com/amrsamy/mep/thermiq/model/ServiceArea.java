package com.amrsamy.mep.thermiq.model;

/** Districts our crews cover. Bookings outside these can't be selected, so coverage is enforced by type. */
public enum ServiceArea {
    NEW_CAIRO("New Cairo"),
    NASR_CITY("Nasr City"),
    HELIOPOLIS("Heliopolis"),
    MAADI("Maadi"),
    ZAMALEK("Zamalek"),
    MOKATTAM("Mokattam"),
    REHAB("El Rehab"),
    MADINATY("Madinaty"),
    SHEIKH_ZAYED("Sheikh Zayed"),
    OCTOBER("6th of October");

    private final String label;

    ServiceArea(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
