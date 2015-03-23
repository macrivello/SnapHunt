package com.michaelcrivello.apps.snaphunt.util;

import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by michael on 3/18/15.
 */
public class InputValidator {
    // Email Pattern
    static Pattern emailPattern = Patterns.EMAIL_ADDRESS;
    // Username, alphanumeric, 3-20 characters
    static String usernameRegex = "^[A-Za-z0-9_-]{3,20}$";
    // Password, I suck at regex, 8-30 characters
    static String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,30}$";

    public enum INPUT_TYPE{
        USERNAME,
        EMAIL,
        PASSWORD
    }

    // Return whether input is valid based on defined rules
    public static boolean isValid(String input, INPUT_TYPE type) {
        boolean valid = false;

        if (type == INPUT_TYPE.USERNAME) {
            valid = Pattern.compile(usernameRegex).matcher(input).matches();
        } else if (type == INPUT_TYPE.EMAIL) {
            valid = emailPattern.matcher(input).matches();
        } else if (type == INPUT_TYPE.PASSWORD) {
            valid = Pattern.compile(passwordRegex).matcher(input).matches();
        } else {
            // Invalid INPUT_TYPE
            valid = false;
        }

        return valid;
    }
}
