package com.lumina.android.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrencyUtils {
    private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");
    private static final DecimalFormat rateFormatter = new DecimalFormat("#,##0.0000");

    public static String formatAmount(double amount) {
        return formatter.format(amount);
    }

    public static String formatRate(double rate) {
        return rateFormatter.format(rate);
    }

    public static boolean isValidInput(String input) {
        if (input == null || input.isEmpty()) return false;
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
