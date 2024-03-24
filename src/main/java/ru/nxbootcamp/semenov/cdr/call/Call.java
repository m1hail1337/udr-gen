package ru.nxbootcamp.semenov.cdr.call;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.StringJoiner;

public record Call(
        CallType type,
        String msisdn,
        LocalDateTime start,
        LocalDateTime finish
) {

    private static final String RECORD_SEPARATOR = ",";

    public static CallBuilder builder() {
        return new CallBuilder();
    }

    public static Call parseCdrRecord(String cdrLine) {
        String[] args = cdrLine.split(RECORD_SEPARATOR);
        CallType type = CallType.getByCode(args[0]);
        String interlocutor = args[1];
        LocalDateTime start = LocalDateTime.ofEpochSecond(Long.parseLong(args[2]), 0, ZoneOffset.UTC);
        LocalDateTime finish = LocalDateTime.ofEpochSecond(Long.parseLong(args[3]), 0, ZoneOffset.UTC);
        return new Call(type, interlocutor, start, finish);
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(RECORD_SEPARATOR);
        sj.add(type.getCode()).add(msisdn).add(localDateTimeToUnixString(start)).add(localDateTimeToUnixString(finish));
        return sj.toString();
    }

    private static String localDateTimeToUnixString(LocalDateTime dateTime) {
        return String.valueOf(dateTime.toEpochSecond(ZoneOffset.UTC));
    }

    public static class CallBuilder {
        private CallType type;
        private String msisdn;
        private LocalDateTime start;
        private LocalDateTime finish;

        private CallBuilder() { }

        public CallBuilder type(CallType type) {
            this.type = type;
            return this;
        }

        public CallBuilder msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }

        public CallBuilder start(LocalDateTime start) {
            this.start = start;
            return this;
        }

        public CallBuilder finish(LocalDateTime finish) {
            this.finish = finish;
            return this;
        }

        public Call build() {
            return new Call(type, msisdn, start, finish);
        }

    }
}
