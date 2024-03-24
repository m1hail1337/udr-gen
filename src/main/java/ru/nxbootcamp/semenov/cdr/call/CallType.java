package ru.nxbootcamp.semenov.cdr.call;

public enum CallType {
    INCOMING("01"),
    OUTGOING("02");

    final String code;

    CallType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CallType getByCode(String code) {
        for (CallType type : CallType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }

        throw new RuntimeException("Unexpected type");
    }
}
