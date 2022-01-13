package com.adamk33n3r.runelite.watchdog.notifications.tts;

import org.apache.commons.text.WordUtils;

public enum Voice {
    LUCAS(0, Language.ENGLISH, Gender.MALE),
    JAMES(1, Language.ENGLISH, Gender.MALE),
    LINDA(2, Language.ENGLISH, Gender.FEMALE),
    RICHARD(3, Language.ENGLISH, Gender.MALE),
    GEORGE(4, Language.ENGLISH, Gender.MALE),
    //SUSAN(5, Language.ENGLISH, Gender.FEMALE), Same as Hazel
    HEERA(6, Language.ENGLISH, Gender.FEMALE),
    RAVI(7, Language.ENGLISH, Gender.MALE),
    MARK(8, Language.ENGLISH, Gender.MALE),
    LAURA(9, Language.SPANISH, Gender.FEMALE),
    PABLO(10, Language.SPANISH, Gender.MALE),
    HEIDI(11, Language.FINNISH, Gender.FEMALE),
    JULIE(12, Language.FRENCH, Gender.FEMALE),
    PAUL(13, Language.FRENCH, Gender.MALE),
    AYUMI(14, Language.JAPANESE, Gender.FEMALE),
    ICHIRO(15, Language.JAPANESE, Gender.MALE),
    SAYAKA(16, Language.JAPANESE, Gender.FEMALE),
    JON(17, Language.NORWEGIAN, Gender.MALE),
    FRANK(18, Language.DUTCH, Gender.MALE),
    BENGT(19, Language.SWEDISH, Gender.MALE),
    HAZEL(20, Language.ENGLISH, Gender.FEMALE),
    EMMA(21, Language.ENGLISH, Gender.FEMALE),
    ZIRA(22, Language.ENGLISH, Gender.FEMALE),
    HELENA(23, Language.SPANISH, Gender.FEMALE),
    HORTENSE(24, Language.FRENCH, Gender.FEMALE),
    HARUKA(25, Language.JAPANESE, Gender.FEMALE);

    public final int id;
    public final Language language;
    public final Gender gender;
    Voice(int id, Language language, Gender gender) {
        this.id = id;
        this.language = language;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(String.format("%s %s %s", this.name(), this.language.name(), this.gender.name()));
    }
}
