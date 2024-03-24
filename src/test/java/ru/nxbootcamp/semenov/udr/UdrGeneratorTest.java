package ru.nxbootcamp.semenov.udr;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.nxbootcamp.semenov.cdr.CdrGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class UdrGeneratorTest {

    private static final List<String> REPORT_PATHS = new ArrayList<>();

    @BeforeAll
    static void ignoreConsoleWriter() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterAll
    static void clearAll() {
        for (String path : REPORT_PATHS) {
            Path filePath = Path.of(path);
            try (Stream<Path> files = Files.walk(filePath)){
                files.map(Path::toFile).filter(File::isFile).forEach(File::delete);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (String path : REPORT_PATHS) {
            String gitkeep = Path.of(path).getParent().toString() + "/actual/.gitkeep";
            try {
                Files.createFile(Path.of(gitkeep));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void testGenerateTotalReport() throws IOException {
        CdrGenerator.setReportsPath("src/test/resources/reports/udr/test-total/cdr/");
        String expectedReportsPath = "src/test/resources/reports/udr/test-total/udr/expected/";
        String actualReportsPath = "src/test/resources/reports/udr/test-total/udr/actual/";
        UdrGenerator.setReportsPath(actualReportsPath);
        REPORT_PATHS.add(actualReportsPath);
        UdrGenerator.generateReport();
        for (Path expectedFilePath : getFilesPathsFromDirectory(Path.of(expectedReportsPath))) {
            Path actualFilePath = Path.of(actualReportsPath + expectedFilePath.getFileName());
            List<String> expectedLines = Files.readAllLines(expectedFilePath);
            List<String> actualLines = Files.readAllLines(actualFilePath);
            assertIterableEquals(expectedLines, actualLines);
        }
    }

    @Test
    void testGenerateMsisdnReport() throws IOException {
        String msisdn = "76475050278";
        CdrGenerator.setReportsPath("src/test/resources/reports/udr/test-msisdn/cdr/");
        String expectedReportsPath = "src/test/resources/reports/udr/test-msisdn/udr/expected/";
        String actualReportsPath = "src/test/resources/reports/udr/test-msisdn/udr/actual/";
        UdrGenerator.setReportsPath(actualReportsPath);
        REPORT_PATHS.add(actualReportsPath);
        UdrGenerator.generateReport(msisdn);
        for (Path expectedFilePath : getFilesPathsFromDirectory(Path.of(expectedReportsPath))) {
            Path actualFilePath = Path.of(actualReportsPath + expectedFilePath.getFileName());
            List<String> expectedLines = Files.readAllLines(expectedFilePath);
            List<String> actualLines = Files.readAllLines(actualFilePath);
            assertIterableEquals(expectedLines, actualLines);
        }
    }

    @Test
    void testGenerateMsisdnMonthReport() throws IOException {
        String msisdn = "75048467773";
        Month month = Month.MARCH;
        CdrGenerator.setReportsPath("src/test/resources/reports/udr/test-msisdn-month/cdr/");
        String expectedReportsPath = "src/test/resources/reports/udr/test-msisdn-month/udr/expected/";
        String actualReportsPath = "src/test/resources/reports/udr/test-msisdn-month/udr/actual/";
        UdrGenerator.setReportsPath(actualReportsPath);
        REPORT_PATHS.add(actualReportsPath);
        UdrGenerator.generateReport(msisdn, month);
        for (Path expectedFilePath : getFilesPathsFromDirectory(Path.of(expectedReportsPath))) {
            Path actualFilePath = Path.of(actualReportsPath + expectedFilePath.getFileName());
            List<String> expectedLines = Files.readAllLines(expectedFilePath);
            List<String> actualLines = Files.readAllLines(actualFilePath);
            assertIterableEquals(expectedLines, actualLines);
        }
    }

    private static List<Path> getFilesPathsFromDirectory(Path dir) throws IOException {
        return Files.walk(dir).map(Path::toFile).filter(File::isFile).map(File::toPath).toList();
    }
}