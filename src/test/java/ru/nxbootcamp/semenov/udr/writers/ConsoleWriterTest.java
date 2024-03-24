package ru.nxbootcamp.semenov.udr.writers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.nxbootcamp.semenov.cdr.call.Call;
import ru.nxbootcamp.semenov.cdr.call.CallType;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleWriterTest {

    private static final String TEST_PATH = "src/test/resources/console/";

    @AfterAll
    static void clearAllActual() {
        try (Stream<Path> files = Files.walk(Path.of(TEST_PATH + "actual/"))) {
            files.map(Path::toFile).filter(File::isFile).forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPrintFullStatistic() throws IOException {
        File actualFile = Files.createFile(Path.of(TEST_PATH + "actual/full.txt")).toFile();
        System.setOut(new PrintStream(actualFile));
        Set<String> contacts = Set.of(
                "75766350689",
                "71356854784",
                "73307544143",
                "77574588827",
                "76983618431",
                "71617938763",
                "78951515453",
                "71979692534",
                "76042003447",
                "73408114726"
        );
        Map<String, Duration> incomingCalls = createIncomingMsisdnCalls();
        Map<String, Duration> outgoingCalls = createOutgoingMsisdnCalls();
        ConsoleWriter.printFullStatistic(contacts, incomingCalls, outgoingCalls);
        List<String> actualLines = Files.readAllLines(actualFile.toPath());
        List<String> expectedLines = Files.readAllLines(Path.of(TEST_PATH + "expected/full.txt"));
        assertIterableEquals(expectedLines.subList(0, 3), actualLines.subList(0, 3));
        assertTrue(actualLines.containsAll(expectedLines));
    }

    @Test
    void testPrintMsisdnStatistic() throws IOException {
        File actualFile = Files.createFile(Path.of(TEST_PATH + "actual/msisdn.txt")).toFile();
        System.setOut(new PrintStream(actualFile));
        String msisdn = "71745888335";
        Map<Month, Duration> incomingCalls = createIncomingMonthCalls();
        Map<Month, Duration> outgoingCalls = createOutgoingMonthCalls();
        ConsoleWriter.printMsisdnStatistic(msisdn, incomingCalls, outgoingCalls);
        List<String> actualLines = Files.readAllLines(actualFile.toPath());
        List<String> expectedLines = Files.readAllLines(Path.of(TEST_PATH + "expected/msisdn.txt"));
        assertIterableEquals(expectedLines, actualLines);
    }

    @Test
    void testPrintMonthlyMsisdnStatistic() throws IOException {
        File actualFile = Files.createFile(Path.of(TEST_PATH + "actual/month.txt")).toFile();
        System.setOut(new PrintStream(actualFile));
        String msisdn = "79096690853";
        Month month = Month.SEPTEMBER;
        Map<CallType, Duration> durations = Map.of(
                CallType.INCOMING, Duration.ofHours(1).plusMinutes(10).plusSeconds(30),
                CallType.OUTGOING, Duration.ofHours(1).plusMinutes(36).plusSeconds(9)
        );
        List<Call> calls = createTestCalls(msisdn, month);
        ConsoleWriter.printMonthlyMsisdnStatistic(month, msisdn, calls, durations);
        List<String> actualLines = Files.readAllLines(actualFile.toPath());
        List<String> expectedLines = Files.readAllLines(Path.of(TEST_PATH + "expected/month.txt"));
        assertIterableEquals(expectedLines, actualLines);
    }

    private Map<String, Duration> createIncomingMsisdnCalls() {
        return Map.of(
                "75766350689", Duration.ofHours(15).plusMinutes(40).plusSeconds(56),
                "73307544143", Duration.ofHours(22).plusMinutes(16).plusSeconds(44),
                "71356854784", Duration.ofHours(12).plusMinutes(2).plusSeconds(15),
                "77574588827", Duration.ofHours(17).plusMinutes(12).plusSeconds(53),
                "71617938763", Duration.ofHours(13).plusMinutes(22).plusSeconds(4),
                "76983618431", Duration.ofHours(21).plusMinutes(46).plusSeconds(31),
                "78951515453", Duration.ofHours(16).plusMinutes(50).plusSeconds(59),
                "71979692534", Duration.ofHours(16).plusMinutes(7).plusSeconds(48),
                "76042003447", Duration.ofHours(17).plusMinutes(59).plusSeconds(31),
                "73408114726", Duration.ofHours(11).plusMinutes(41).plusSeconds(10)
        );
    }

    private Map<String, Duration> createOutgoingMsisdnCalls() {
        return Map.of(
                "75766350689", Duration.ofHours(21).plusMinutes(20).plusSeconds(51),
                "73307544143", Duration.ofHours(19).plusMinutes(50).plusSeconds(2),
                "71356854784", Duration.ofHours(17).plusMinutes(7).plusSeconds(45),
                "77574588827", Duration.ofHours(11).plusMinutes(51).plusSeconds(35),
                "71617938763", Duration.ofHours(19).plusMinutes(7).plusSeconds(9),
                "76983618431", Duration.ofHours(15).plusMinutes(28).plusSeconds(29),
                "78951515453", Duration.ofHours(18).plusMinutes(44).plusSeconds(44),
                "71979692534", Duration.ofHours(18).plusMinutes(46).plusSeconds(32),
                "76042003447", Duration.ofHours(19).plusMinutes(19).plusSeconds(23),
                "73408114726", Duration.ofHours(20).plusMinutes(45).plusSeconds(57)
        );
    }

    private Map<Month, Duration> createIncomingMonthCalls() {
        return new HashMap<>() {{
            put(Month.JANUARY, Duration.ofSeconds(1857));
            put(Month.FEBRUARY, Duration.ofSeconds(3827));
            put(Month.MARCH, Duration.ofSeconds(3566));
            put(Month.APRIL, Duration.ofSeconds(4567));
            put(Month.MAY, Duration.ofSeconds(2401));
            put(Month.JUNE, Duration.ofSeconds(2318));
            put(Month.JULY, Duration.ofSeconds(9717));
            put(Month.AUGUST, Duration.ofSeconds(8306));
            put(Month.SEPTEMBER, Duration.ofSeconds(10294));
            put(Month.OCTOBER, Duration.ofSeconds(4707));
            put(Month.NOVEMBER, Duration.ofSeconds(10004));
            put(Month.DECEMBER, Duration.ofSeconds(3449));
        }};
    }

    private Map<Month, Duration> createOutgoingMonthCalls() {
        return new HashMap<>() {{
            put(Month.JANUARY, Duration.ofSeconds(8643));
            put(Month.FEBRUARY, Duration.ofSeconds(790));
            put(Month.MARCH, Duration.ofSeconds(1215));
            put(Month.APRIL, Duration.ofSeconds(2869));
            put(Month.MAY, Duration.ofSeconds(5147));
            put(Month.JUNE, Duration.ofSeconds(421));
            put(Month.JULY, Duration.ofSeconds(8356));
            put(Month.AUGUST, Duration.ofSeconds(9352));
            put(Month.SEPTEMBER, Duration.ofSeconds(8792));
            put(Month.OCTOBER, Duration.ofSeconds(8009));
            put(Month.NOVEMBER, Duration.ofSeconds(2035));
            put(Month.DECEMBER, Duration.ofSeconds(2021));
        }};
    }

    private List<Call> createTestCalls(String msisdn, Month month) {
        return List.of(
                Call.builder()
                        .type(CallType.OUTGOING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 10, 1, 37, 43))
                        .finish(LocalDateTime.of(2024, month, 10, 2, 7, 23))
                        .build(),
                Call.builder()
                        .type(CallType.OUTGOING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 11, 0, 50, 41))
                        .finish(LocalDateTime.of(2024, month, 11, 0, 52, 32))
                        .build(),
                Call.builder()
                        .type(CallType.OUTGOING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 17, 14, 32, 3))
                        .finish(LocalDateTime.of(2024, month, 17, 15, 6, 2))
                        .build(),
                Call.builder()
                        .type(CallType.OUTGOING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 19, 14, 39, 32))
                        .finish(LocalDateTime.of(2024, month, 19, 15, 10, 11))
                        .build(),
                Call.builder()
                        .type(CallType.INCOMING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 19, 17, 16, 23))
                        .finish(LocalDateTime.of(2024, month, 19, 18, 8, 1))
                        .build(),
                Call.builder()
                        .type(CallType.INCOMING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 24, 16, 12, 57))
                        .finish(LocalDateTime.of(2024, month, 24, 16, 19, 42))
                        .build(),
                Call.builder()
                        .type(CallType.INCOMING)
                        .msisdn(msisdn)
                        .start(LocalDateTime.of(2024, month, 27, 11, 57, 21))
                        .finish(LocalDateTime.of(2024, month, 27, 12, 9, 28))
                        .build()
        );
    }
}