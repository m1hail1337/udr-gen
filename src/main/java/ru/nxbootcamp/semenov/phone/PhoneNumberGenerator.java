package ru.nxbootcamp.semenov.phone;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PhoneNumberGenerator {

    private static final Random random = new Random();
    private static final String COUNTRY_CODE = "7";
    private static final int DIGITS_IN_NUMBER = 11;
    private static final int MIN_CONTACTS = 10;
    private static final int MAX_CONTACTS = 10;

    public static String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder(COUNTRY_CODE);
        for (int i = 0; i < DIGITS_IN_NUMBER - 1; i++) {
            sb.append(random.nextInt(0, 10));
        }
        return sb.toString();
    }

    public static Set<String> generateContacts(String owner) {
        int contactBookCapacity = random.nextInt(MIN_CONTACTS, MAX_CONTACTS + 1);
        Set<String> contacts = new HashSet<>();
        while (contacts.size() < contactBookCapacity) {
            String phoneNumber = generatePhoneNumber();
            if (!phoneNumber.equals(owner)) {
                contacts.add(phoneNumber);
            }
        }
        return contacts;
    }

    public static int getDigitsInNumber() {
        return DIGITS_IN_NUMBER;
    }

    public static int getMaxContacts() {
        return MAX_CONTACTS;
    }

    public static int getMinContacts() {
        return MIN_CONTACTS;
    }

    public static String getCountryCode() {
        return COUNTRY_CODE;
    }
}
