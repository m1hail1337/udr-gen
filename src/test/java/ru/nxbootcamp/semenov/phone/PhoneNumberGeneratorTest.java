package ru.nxbootcamp.semenov.phone;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberGeneratorTest {

    @Test
    void testGeneratePhoneNumber() {
        String phoneNumber = PhoneNumberGenerator.generatePhoneNumber();
        assertEquals(PhoneNumberGenerator.getDigitsInNumber(), phoneNumber.length());
        assertTrue(phoneNumber.startsWith(PhoneNumberGenerator.getCountryCode()));
    }

    @Test
    void testGenerateContacts() {
        String owner = "1234567890";
        Set<String> contacts = PhoneNumberGenerator.generateContacts(owner);
        assertTrue(contacts.size() >= PhoneNumberGenerator.getMinContacts() &&
                contacts.size() <= PhoneNumberGenerator.getMaxContacts());
        assertTrue(contacts.stream().allMatch(c ->
                c.length() == PhoneNumberGenerator.getDigitsInNumber() && c.startsWith(PhoneNumberGenerator.getCountryCode())));
        assertFalse(contacts.contains(owner));
    }

    @Test
    void testGenerateContactsRandom() {
        String owner = "1234567890";
        Set<String> contacts1 = PhoneNumberGenerator.generateContacts(owner);
        Set<String> contacts2 = PhoneNumberGenerator.generateContacts(owner);
        if (contacts1.size() == contacts2.size()) {
            assertFalse(contacts1.containsAll(contacts2));
        }
    }
}