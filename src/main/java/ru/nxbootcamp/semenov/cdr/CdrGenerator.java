package ru.nxbootcamp.semenov.cdr;

import ru.nxbootcamp.semenov.cdr.call.CallType;
import ru.nxbootcamp.semenov.cdr.call.Call;
import ru.nxbootcamp.semenov.cdr.db.calls.CallsRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CdrGenerator {

    public static String REPORTS_PATH = "reports/cdr/";
    public static final String FILE_EXTENSION = ".txt";

    private static CallsRepository repository;

    private static final Random random = new Random();
    private static final int YEAR = 2024;
    private static final int MAX_DAYS_WITHOUT_CALLS = 1;
    private static final int MIN_CALL_DURATION = 10;
    private static final int MAX_CALL_DURATION = 3600;

    public static void generateYearCdr(Set<String> contacts) {
        for (Month month : Month.values()) {
            List<Call> calls = generateCdr(contacts, month);
            createMonthReport(calls, month);
            repository.save(calls);
        }
    }

    private static List<Call> generateCdr(Set<String> contacts, Month month) {
        List<Call> calls = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.of(YEAR, month, 1, 0, 0);

        while (currentDateTime.getMonth() == month) {
            Call call = generateCall(currentDateTime, contacts);
            calls.add(call);
            currentDateTime = call.finish();
        }

        if (calls.get(calls.size() - 1).start().getMonth() != month) {
            calls.remove(calls.size() - 1);
        }

        return calls;
    }

    private static Call generateCall(LocalDateTime currentDateTime, Set<String> contacts) {
        CallType callType = getRandomCallType();
        String msisdn = getRandomMsisdn(contacts);
        int callDuration = random.nextInt(MIN_CALL_DURATION, MAX_CALL_DURATION + 1);
        LocalDateTime startOfCall = generateCallDateTime(currentDateTime);
        LocalDateTime finishOfCall = startOfCall.plusSeconds(callDuration);
        return Call.builder()
                .type(callType)
                .msisdn(msisdn)
                .start(startOfCall)
                .finish(finishOfCall)
                .build();
    }

    private static CallType getRandomCallType() {
        return CallType.values()[random.nextInt(CallType.values().length)];
    }

    private static String getRandomMsisdn(Set<String> contacts) {
        return (String) contacts.toArray()[random.nextInt(contacts.size())];
    }

    private static LocalDateTime generateCallDateTime(LocalDateTime prevCallDateTime) {
        return prevCallDateTime
                .plusDays(random.nextInt(MAX_DAYS_WITHOUT_CALLS))
                .plusHours(random.nextInt(24))
                .plusMinutes(random.nextInt(60))
                .plusSeconds(random.nextInt(60));
    }

    private static void createMonthReport(List<Call> calls, Month month) {
        String fileName = REPORTS_PATH + month.name() + FILE_EXTENSION;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            for (Call call : calls) {
                writer.append(call.toString()).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setRepository(CallsRepository repository) {
        CdrGenerator.repository = repository;
    }

    public static void setReportsPath(String reportsPath) {
        REPORTS_PATH = reportsPath;
    }
}
