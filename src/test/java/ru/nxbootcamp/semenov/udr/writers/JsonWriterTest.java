package ru.nxbootcamp.semenov.udr.writers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nxbootcamp.semenov.cdr.call.CallType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    private static final String TEST_PATH = "src/test/resources/json/";

    @BeforeEach
    void clear() {
        try (Stream<Path> files = Files.walk(Path.of(TEST_PATH))){
            files.map(Path::toFile).filter(File::isFile).forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateJsonReport() throws IOException {
        String msisdn = "1234567890";
        Month month = Month.FEBRUARY;
        Map<CallType, Duration> durations = Map.of(
                CallType.INCOMING, Duration.ofMinutes(30),
                CallType.OUTGOING, Duration.ofSeconds(191)
        );
        JsonWriter.createJsonReport(msisdn, month, durations, TEST_PATH);
        Path expectedPath = Path.of("src/test/resources/json/1234567890_2.json");
        assertTrue(Files.exists(expectedPath));
        List<String> lines = Files.readAllLines(expectedPath);
        assertEquals("{", lines.get(0));
        assertEquals("  \"msisdn\": \"1234567890\",", lines.get(1));
        assertEquals("  \"incomingCall\": {", lines.get(2));
        assertEquals("    \"totalTime\": \"00:30:00\"", lines.get(3));
        assertEquals("  },", lines.get(4));
        assertEquals("  \"outgoingCall\": {", lines.get(5));
        assertEquals("    \"totalTime\": \"00:03:11\"", lines.get(6));
        assertEquals("  }", lines.get(7));
        assertEquals("}", lines.get(8));

        Files.delete(expectedPath);
    }
}