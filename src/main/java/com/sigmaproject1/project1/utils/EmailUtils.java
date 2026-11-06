package com.sigmaproject1.project1.utils;

import java.util.Locale;

public class EmailUtils {

    public static String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT).strip();
    }
}
