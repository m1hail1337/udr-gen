package ru.nxbootcamp.semenov;

import ru.nxbootcamp.semenov.cdr.CdrGenerator;
import ru.nxbootcamp.semenov.cdr.db.calls.CallsRepository;
import ru.nxbootcamp.semenov.cdr.db.contacts.ContactsRepository;
import ru.nxbootcamp.semenov.phone.PhoneNumberGenerator;
import ru.nxbootcamp.semenov.udr.UdrGenerator;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        ContactsRepository contactsRepository = new ContactsRepository();
        CallsRepository callsRepository = new CallsRepository();
        contactsRepository.setLogsEnabled(true);
        String subscriber = PhoneNumberGenerator.generatePhoneNumber();
        Set<String> contacts = PhoneNumberGenerator.generateContacts(subscriber);
        contactsRepository.insertContacts(contacts);
        CdrGenerator.setRepository(callsRepository);
        CdrGenerator.generateYearCdr(contacts);
        UdrGenerator.generateReport();
    }
}
