package com.stockflow.fiscal.provider;

import com.stockflow.exception.FiscalException;
import com.stockflow.fiscal.dto.NfceDTO;
import com.stockflow.fiscal.parser.NfceHtmlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "features.fiscal.enabled", havingValue = "true")
public class NuvemFiscalProvider implements FiscalProvider {

    private final WebClient webClient;
    private final NfceHtmlParser parser;

    @Override
    public String getName() {
        return "NUVEM_FISCAL";
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.contains("nuvemfiscal.com.br");
    }

    @Override
    public NfceDTO fetchInvoice(String url) {
        log.info("Fetching NFC-e from NuvemFiscal: {}", url);
        try {
            String html = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            if (html == null || html.isBlank()) {
                throw new FiscalException("Empty response from NuvemFiscal");
            }
            return parser.parse(html);
        } catch (FiscalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching from NuvemFiscal: {}", e.getMessage());
            throw new FiscalException("Failed to fetch invoice from NuvemFiscal: " + e.getMessage());
        }
    }
}
