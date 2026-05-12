package com.stockflow.utils;

public class CnpjUtils {

    private CnpjUtils() {}

    public static String clean(String cnpj) {
        if (cnpj == null) return null;
        return cnpj.replaceAll("[^0-9]", "");
    }

    public static boolean isValid(String cnpj) {
        if (cnpj == null) return false;
        String c = clean(cnpj);
        if (c.length() != 14) return false;
        if (c.chars().distinct().count() == 1) return false;

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (c.charAt(i) - '0') * weights1[i];
        int d1 = sum % 11 < 2 ? 0 : 11 - (sum % 11);

        sum = 0;
        for (int i = 0; i < 13; i++) sum += (c.charAt(i) - '0') * weights2[i];
        int d2 = sum % 11 < 2 ? 0 : 11 - (sum % 11);

        return c.charAt(12) - '0' == d1 && c.charAt(13) - '0' == d2;
    }

    public static String format(String cnpj) {
        String c = clean(cnpj);
        if (c == null || c.length() != 14) return cnpj;
        return c.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
