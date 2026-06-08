package com.stockflow.fiscal.parser;

import com.stockflow.fiscal.dto.NfceDTO;
import com.stockflow.fiscal.dto.NfceItemDTO;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@ConditionalOnProperty(name = "features.fiscal.enabled", havingValue = "true")
public class NfceHtmlParser {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern KEY_PATTERN = Pattern.compile("\\d{44}");

    public NfceDTO parse(String html) {
        Document doc = Jsoup.parse(html);

        String supplierName = extractText(doc, "#u20, .txtTit, #conteudo h1");
        String supplierCnpj = extractCnpj(doc);
        String invoiceKey = extractInvoiceKey(doc);
        LocalDate purchaseDate = extractDate(doc);
        BigDecimal totalValue = extractTotalValue(doc);
        List<NfceItemDTO> items = extractItems(doc);

        return NfceDTO.builder()
            .invoiceKey(invoiceKey)
            .supplierName(supplierName)
            .supplierCnpj(supplierCnpj)
            .purchaseDate(purchaseDate)
            .totalValue(totalValue)
            .items(items)
            .build();
    }

    private List<NfceItemDTO> extractItems(Document doc) {
        List<NfceItemDTO> items = new ArrayList<>();

        Elements rows = doc.select("#tabResult tr, .item, table#Prod tr");
        for (Element row : rows) {
            try {
                String name = row.select(".txtTit, td.descricao, .Nome").text().trim();
                if (name.isEmpty()) continue;

                String ean = row.select(".RCod, .ean, .Cod").text().replaceAll("[^0-9]", "");
                String qtyText = row.select(".Qtd, .qtd, td:nth-child(3)").text()
                    .replaceAll("[^0-9.,]", "").replace(",", ".");
                String unitValText = row.select(".vb, .vlrUn, td:nth-child(4)").text()
                    .replaceAll("[^0-9.,]", "").replace(",", ".");
                String totalValText = row.select(".valor, .vlrItem, td:nth-child(5)").text()
                    .replaceAll("[^0-9.,]", "").replace(",", ".");
                String unit = row.select(".Unid, .un, td:nth-child(2)").text().trim();

                BigDecimal qty = parseDecimal(qtyText);
                BigDecimal unitVal = parseDecimal(unitValText);
                BigDecimal totalVal = parseDecimal(totalValText);

                if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) continue;

                items.add(NfceItemDTO.builder()
                    .name(name)
                    .ean(ean.length() >= 8 ? ean : null)
                    .quantity(qty)
                    .unitValue(unitVal != null ? unitVal : BigDecimal.ZERO)
                    .totalValue(totalVal != null ? totalVal : BigDecimal.ZERO)
                    .unit(unit.isEmpty() ? "UN" : unit)
                    .build());
            } catch (Exception e) {
                log.debug("Error parsing item row: {}", e.getMessage());
            }
        }
        return items;
    }

    private String extractText(Document doc, String selector) {
        Element el = doc.selectFirst(selector);
        return el != null ? el.text().trim() : "";
    }

    private String extractCnpj(Document doc) {
        String text = doc.text();
        Pattern p = Pattern.compile("(\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2})");
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1).replaceAll("[^0-9]", "");
        return null;
    }

    private String extractInvoiceKey(Document doc) {
        String text = doc.text().replaceAll("\\s", "");
        Matcher m = KEY_PATTERN.matcher(text);
        if (m.find()) return m.group();
        return null;
    }

    private LocalDate extractDate(Document doc) {
        try {
            Pattern p = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})");
            Matcher m = p.matcher(doc.text());
            if (m.find()) return LocalDate.parse(m.group(1), DATE_FORMAT);
        } catch (Exception e) {
            log.debug("Could not parse date: {}", e.getMessage());
        }
        return LocalDate.now();
    }

    private BigDecimal extractTotalValue(Document doc) {
        Element el = doc.selectFirst("#vl-total-nota, .totalNota, #valorTotal");
        if (el != null) {
            String text = el.text().replaceAll("[^0-9.,]", "").replace(",", ".");
            return parseDecimal(text);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            // Handle Brazilian format: 1.234,56 -> 1234.56
            if (value.contains(",") && value.contains(".")) {
                value = value.replace(".", "").replace(",", ".");
            } else if (value.contains(",")) {
                value = value.replace(",", ".");
            }
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
