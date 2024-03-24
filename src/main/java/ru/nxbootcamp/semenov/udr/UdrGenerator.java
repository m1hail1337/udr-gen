package ru.nxbootcamp.semenov.udr;

import ru.nxbootcamp.semenov.cdr.CdrGenerator;
import ru.nxbootcamp.semenov.cdr.call.Call;
import ru.nxbootcamp.semenov.cdr.call.CallType;
import ru.nxbootcamp.semenov.udr.writers.ConsoleWriter;
import ru.nxbootcamp.semenov.udr.writers.JsonWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UdrGenerator {

    public static String REPORTS_PATH = "reports/udr/";

    public static void generateReport() {
        Map<String, Duration> totalIncomingDurations = new HashMap<>();
        Map<String, Duration> totalOutgoingDurations = new HashMap<>();
        Set<String> msisdnSet = new HashSet<>();
        for (Month month : Month.values()) {
            Map<String, List<Call>> msisdnToCalls = mapMsisdnToCallsForMonth(month);
            for (String msisdn : msisdnToCalls.keySet()) {
                Map<CallType, Duration> durations = countCallDurationsForMsisdn(
                        msisdnToCalls.getOrDefault(msisdn, new ArrayList<>())
                );
                JsonWriter.createJsonReport(msisdn, month, durations, REPORTS_PATH);

                appendMsisdnDurations(msisdn, totalIncomingDurations, totalOutgoingDurations, durations);
            }
            msisdnSet.addAll(msisdnToCalls.keySet());
        }

        ConsoleWriter.printFullStatistic(msisdnSet, totalIncomingDurations, totalOutgoingDurations);
    }

    private static void appendMsisdnDurations(String msisdn,
                                              Map<String, Duration> totalIncomingDurations,
                                              Map<String, Duration> totalOutgoingDurations,
                                              Map<CallType, Duration> durations) {
        Duration currentIncommingDuration = totalIncomingDurations.getOrDefault(msisdn, Duration.ZERO);
        Duration currentOutgoingDuration = totalOutgoingDurations.getOrDefault(msisdn, Duration.ZERO);
        totalIncomingDurations.put(msisdn, currentIncommingDuration.plus(durations.get(CallType.INCOMING)));
        totalOutgoingDurations.put(msisdn, currentOutgoingDuration.plus(durations.get(CallType.OUTGOING)));
    }

    public static void generateReport(String msisdn) {
        Map<Month, Duration> incomingCalls = new HashMap<>();
        Map<Month, Duration> outgoingCalls = new HashMap<>();
        for (Month month : Month.values()) {
            Map<String, List<Call>> msisdnToCalls = mapMsisdnToCallsForMonth(month);
            for (String currentMsisdn : msisdnToCalls.keySet()) {
                Map<CallType, Duration> durations = countCallDurationsForMsisdn(
                        msisdnToCalls.getOrDefault(msisdn, new ArrayList<>())
                );
                JsonWriter.createJsonReport(currentMsisdn, month, durations, REPORTS_PATH);

                if (currentMsisdn.equals(msisdn)) {
                    incomingCalls.put(month, durations.get(CallType.INCOMING));
                    outgoingCalls.put(month, durations.get(CallType.OUTGOING));
                }
            }
        }

        ConsoleWriter.printMsisdnStatistic(msisdn, incomingCalls, outgoingCalls);
    }

    public static void generateReport(String msisdn, Month month) {
        Map<String, List<Call>> msisdnToCalls = mapMsisdnToCallsForMonth(month);
        List<Call> targetCalls = msisdnToCalls.getOrDefault(msisdn, new ArrayList<>());
        Map<CallType, Duration> durations = countCallDurationsForMsisdn(targetCalls);
        JsonWriter.createJsonReport(msisdn, month, durations, REPORTS_PATH);

        ConsoleWriter.printMonthlyMsisdnStatistic(month, msisdn, targetCalls, durations);
    }

    private static List<Call> getCallsFromMonthCdrFile(Month month) {
        String path = CdrGenerator.REPORTS_PATH + month.name() + CdrGenerator.FILE_EXTENSION;
        try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
            return br.lines().map(Call::parseCdrRecord).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<CallType, Duration> countCallDurationsForMsisdn(List<Call> calls) {
        Duration totalIncoming = Duration.ZERO;
        Duration totalOutgoing = Duration.ZERO;

        for (Call call : calls) {
            if (call.type() == CallType.INCOMING) {
                totalIncoming = totalIncoming.plus(Duration.between(call.start(), call.finish()));
            } else {
                totalOutgoing = totalOutgoing.plus(Duration.between(call.start(), call.finish()));
            }
        }

        return Map.of(
                CallType.INCOMING, totalIncoming,
                CallType.OUTGOING, totalOutgoing
        );
    }

    private static Map<String, List<Call>> mapMsisdnToCallsForMonth(Month month) {
        List<Call> calls = getCallsFromMonthCdrFile(month);
        return calls.stream().collect(Collectors.groupingBy(Call::msisdn, Collectors.toList()));
    }

    public static void setReportsPath(String reportsPath) {
        REPORTS_PATH = reportsPath;
    }
}
