package ru.nxbootcamp.semenov.udr.writers;

import ru.nxbootcamp.semenov.cdr.call.Call;
import ru.nxbootcamp.semenov.cdr.call.CallType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConsoleWriter {

    private static final String FULL_STATISTIC_LINE = "-------------------------------------\n";
    private static final String MSISDN_STATISTIC_LINE = "-----------------------------------\n";
    private static final String MONTHLY_MSISDN_STATISTIC_LINE = "----------------------------------------\n";
    private static final String COLUMN_SEPARATOR = " | ";

    public static void printFullStatistic(Set<String> msisdnSet,
                                          Map<String, Duration> incomingCalls,
                                          Map<String, Duration> outgoingCalls) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            printFullStatisticHeader(writer);
            for (String msisdn : msisdnSet) {
                Duration incomingCallsDuration = incomingCalls.getOrDefault(msisdn, Duration.ZERO);
                Duration outgoingCallsDuration = outgoingCalls.getOrDefault(msisdn, Duration.ZERO);
                printMsisdnInfo(writer, msisdn, incomingCallsDuration, outgoingCallsDuration);
            }
            writer.append(FULL_STATISTIC_LINE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printMsisdnInfo(BufferedWriter writer,
                                        String msisdn,
                                        Duration incomingCallsDuration,
                                        Duration outgoingCallsDuration) throws IOException {
        writer.append(COLUMN_SEPARATOR.stripLeading()).append(msisdn).append(COLUMN_SEPARATOR);
        writer.append(formatDuration(incomingCallsDuration)).append(COLUMN_SEPARATOR);
        writer.append(formatDuration(outgoingCallsDuration)).append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
    }

    public static void printMsisdnStatistic(String msisdn,
                                            Map<Month, Duration> incomingCalls,
                                            Map<Month, Duration> outgoingCalls) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            printMsisdnStatisticHeader(writer, msisdn);
            for (Month month : Month.values()) {
                Duration incomingCallsDuration = incomingCalls.getOrDefault(month, Duration.ZERO);
                Duration outgoingCallsDuration = outgoingCalls.getOrDefault(month, Duration.ZERO);
                printMonthInfo(writer, month, incomingCallsDuration, outgoingCallsDuration);
            }
            writer.append(MSISDN_STATISTIC_LINE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printMonthInfo(BufferedWriter writer,
                                       Month month,
                                       Duration incomingCallsDuration,
                                       Duration outgoingCallsDuration) throws IOException {
        writer.append(COLUMN_SEPARATOR.stripLeading()).append(centerString(9, month.name()));
        writer.append(COLUMN_SEPARATOR).append(formatDuration(incomingCallsDuration)).append(COLUMN_SEPARATOR);
        writer.append(formatDuration(outgoingCallsDuration)).append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
    }

    public static void printMonthlyMsisdnStatistic(Month month,
                                                   String msisdn,
                                                   List<Call> calls,
                                                   Map<CallType, Duration> durations) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            printMonthlyMsisdnStatisticHeader(writer, month, msisdn);
            for (Call call : calls) {
                printCallInfo(writer, call);
            }
            printMonthlyMsisdnStatisticFooter(writer, durations);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printCallInfo(BufferedWriter writer, Call call) throws IOException {
        writer.append(COLUMN_SEPARATOR.stripLeading()).append(call.type().name()).append(COLUMN_SEPARATOR);
        writer.append(String.format("%2d ", call.start().getDayOfMonth())).append(COLUMN_SEPARATOR);
        writer.append(formatDateTime(call.start())).append(COLUMN_SEPARATOR);
        writer.append(formatDateTime(call.finish())).append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
    }

    private static void printMonthlyMsisdnStatisticFooter(BufferedWriter writer,
                                                          Map<CallType, Duration> durations) throws IOException {
        Duration totalIncomingDuration = durations.get(CallType.INCOMING);
        Duration totalOutgoingDuration = durations.get(CallType.OUTGOING);
        writer.append(MONTHLY_MSISDN_STATISTIC_LINE);
        writer.append(COLUMN_SEPARATOR.stripLeading()).append("Total incoming call time:   ");
        writer.append(formatDuration(totalIncomingDuration)).append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
        writer.append(COLUMN_SEPARATOR.stripLeading()).append("Total outgoing call time:   ");
        writer.append(formatDuration(totalOutgoingDuration)).append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
        writer.append(MONTHLY_MSISDN_STATISTIC_LINE);
    }

    private static void printFullStatisticHeader(BufferedWriter writer) throws IOException {
        writer.append(FULL_STATISTIC_LINE);
        writer.append("|    Phone    | Incoming | Outgoing |\n");
        writer.append(FULL_STATISTIC_LINE);
    }

    private static void printMsisdnStatisticHeader(BufferedWriter writer, String msisdn) throws IOException {
        String headerText = String.format("Statistic for %s", msisdn);
        writer.append(MSISDN_STATISTIC_LINE).append(COLUMN_SEPARATOR.stripLeading());
        writer.append(centerString(MSISDN_STATISTIC_LINE.length() - 5, headerText));
        writer.append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
        writer.append(MSISDN_STATISTIC_LINE);
        writer.append("|   Month   | Incoming | Outgoing |\n");
        writer.append(MSISDN_STATISTIC_LINE);
    }

    private static void printMonthlyMsisdnStatisticHeader(BufferedWriter writer,
                                                          Month month,
                                                          String msisdn) throws IOException {
        String headerText = String.format("%s statistic for %s", month.name(), msisdn);
        writer.append(MONTHLY_MSISDN_STATISTIC_LINE);
        writer.append(COLUMN_SEPARATOR.stripLeading()).append(centerString(36, headerText));
        writer.append(COLUMN_SEPARATOR.stripTrailing());
        writer.newLine();
        writer.append(MONTHLY_MSISDN_STATISTIC_LINE);
        writer.append("|   Type   | Day |  Start   |   End    |\n");
        writer.append(MONTHLY_MSISDN_STATISTIC_LINE);
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(formatter);
    }

    private static String centerString(int fieldLength, String str) {
        int spaceSize = fieldLength - str.length();
        int prefixSize = spaceSize / 2;
        int suffixSize = (spaceSize + 1) / 2;
        return fieldLength > str.length()
                ? " ".repeat(prefixSize) + str + " ".repeat(suffixSize)
                : str;
    }
}
