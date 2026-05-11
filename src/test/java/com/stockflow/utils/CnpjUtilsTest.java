package com.stockflow.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CnpjUtilsTest {

    @Test
    void isValid_shouldReturnTrueForValidCnpj() {
        assertThat(CnpjUtils.isValid("11222333000181")).isTrue();
        assertThat(CnpjUtils.isValid("11.222.333/0001-81")).isTrue();
    }

    @Test
    void isValid_shouldReturnFalseForInvalidCnpj() {
        assertThat(CnpjUtils.isValid("00000000000000")).isFalse();
        assertThat(CnpjUtils.isValid("11111111111111")).isFalse();
        assertThat(CnpjUtils.isValid("12345678000100")).isFalse();
        assertThat(CnpjUtils.isValid(null)).isFalse();
    }

    @Test
    void clean_shouldRemoveFormatting() {
        assertThat(CnpjUtils.clean("11.222.333/0001-81")).isEqualTo("11222333000181");
    }

    @Test
    void format_shouldApplyMask() {
        assertThat(CnpjUtils.format("11222333000181")).isEqualTo("11.222.333/0001-81");
    }
}
