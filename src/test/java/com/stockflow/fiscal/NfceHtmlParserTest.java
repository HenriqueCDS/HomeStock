package com.stockflow.fiscal;

import com.stockflow.fiscal.dto.NfceDTO;
import com.stockflow.fiscal.parser.NfceHtmlParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NfceHtmlParserTest {

    private final NfceHtmlParser parser = new NfceHtmlParser();

    @Test
    void parse_shouldExtractInvoiceKeyFrom44DigitNumber() {
        String html = "<html><body><p>Chave: 35240112345678000195650010000012341234567890</p></body></html>";
        NfceDTO result = parser.parse(html);
        assertThat(result.getInvoiceKey()).isEqualTo("35240112345678000195650010000012341234567890");
    }

    @Test
    void parse_shouldReturnEmptyItemsForBlankHtml() {
        String html = "<html><body></body></html>";
        NfceDTO result = parser.parse(html);
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void parse_shouldHandleNullGracefully() {
        NfceDTO result = parser.parse("<html></html>");
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isNotNull();
    }
}
