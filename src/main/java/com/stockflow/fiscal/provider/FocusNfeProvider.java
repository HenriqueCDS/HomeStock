package com.stockflow.fiscal.provider;

import com.stockflow.exception.FiscalException;
import com.stockflow.fiscal.dto.NfceDTO;
import com.stockflow.fiscal.parser.NfceHtmlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class FocusNfeProvider implements FiscalProvider {

    private final WebClient webClient;
    private final NfceHtmlParser parser;

    @Override
    public String getName() {
        return "FOCUS_NFE";
    }

    @Override
    public boolean supports(String url) {
        return url != null && url.contains("focusnfe.com.br");
    }

    @Override
    public NfceDTO fetchInvoice(String url) {
        log.info("Fetching NFC-e from FocusNFe: {}", url);
        try {
            String html = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            if (html == null || html.isBlank()) {
                throw new FiscalException("Empty response from FocusNFe");
            }
            return parser.parse(html);
        } catch (FiscalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching from FocusNFe: {}", e.getMessage());
            throw new FiscalException("Failed to fetch invoice from FocusNFe: " + e.getMessage());
        }
    }
}
