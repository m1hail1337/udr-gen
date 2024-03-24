package ru.nxbootcamp.semenov.cdr;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.nxbootcamp.semenov.cdr.call.Call;
import ru.nxbootcamp.semenov.cdr.db.calls.CallsRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CdrGeneratorTest {

    private static final String TEST_REPORTS_PATH = "src/test/resources/reports/cdr/";
    private static CallsRepository repository;

    @SuppressWarnings("unchecked")
    private final ArgumentCaptor<List<Call>> callsCaptor = ArgumentCaptor.forClass(List.class);

    @BeforeAll
    static void setUp() {
        CdrGenerator.setReportsPath(TEST_REPORTS_PATH);
        repository = mock(CallsRepository.class);
        CdrGenerator.setRepository(repository);
    }

    @BeforeEach
    void clearAndReset() {
        try (Stream<Path> files = Files.walk(Path.of(TEST_REPORTS_PATH))){
            files.map(Path::toFile).filter(File::isFile).forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        reset(repository);
    }

    @Test
    void testGenerateYearCdrWithEmptyContacts() {
        Set<String> contacts = new HashSet<>();
        assertThrows(IllegalArgumentException.class, () -> CdrGenerator.generateYearCdr(contacts));
    }

    @Test
    void testGenerateYearCdrWithSingleContact() {
        String contact = "1234567890";
        CdrGenerator.generateYearCdr(Set.of(contact));
        verify(repository, times(12)).save(callsCaptor.capture());
        List<List<Call>> allCalls = callsCaptor.getAllValues();
        for (List<Call> calls : allCalls) {
            for (Call call : calls) {
                assertEquals(contact, call.msisdn());
            }
        }
    }

    @Test
    void generateYearCdr() {
        Set<String> contacts = Set.of(
                "70502458936",
                "71445619059",
                "77877048116",
                "73356602355",
                "73448047816",
                "74205433560",
                "79055722398",
                "76812962223",
                "76103332771",
                "72231730160"
        );
        CdrGenerator.generateYearCdr(contacts);
        verify(repository, times(12)).save(callsCaptor.capture());
        List<List<Call>> allCalls = callsCaptor.getAllValues();
        for (List<Call> calls : allCalls) {
            for (Call call : calls) {
                assertTrue(contacts.contains(call.msisdn()));
            }
        }
        for (Month month : Month.values()) {
            assertTrue(Files.exists(Path.of(CdrGenerator.REPORTS_PATH + month.name() + CdrGenerator.FILE_EXTENSION)));
        }
    }
}