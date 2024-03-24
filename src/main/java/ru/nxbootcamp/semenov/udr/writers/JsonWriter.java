package ru.nxbootcamp.semenov.udr.writers;

import ru.nxbootcamp.semenov.cdr.call.CallType;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Month;
import java.util.Map;

public class JsonWriter {

    private static final String FILE_EXTENSION = ".json";

    public static void createJsonReport(String msisdn, Month month, Map<CallType, Duration> durations, String path) {
        String file = path + msisdn + "_" + month.getValue() + FILE_EXTENSION;
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("{\n").append("  \"msisdn\": ").append("\"").append(msisdn).append("\"").append(",\n");
            writer.append("  \"incomingCall\": {\n").append("    \"totalTime\": ");
            writer.append("\"").append(formatDuration(durations.get(CallType.INCOMING))).append("\"").append("\n  },\n");
            writer.append("  \"outgoingCall\": {\n").append("    \"totalTime\": ");
            writer.append("\"").append(formatDuration(durations.get(CallType.OUTGOING))).append("\"").append("\n  }\n");
            writer.append("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
