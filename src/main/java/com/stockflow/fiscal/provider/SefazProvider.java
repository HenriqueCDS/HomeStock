package com.stockflow.fiscal.provider;

import com.stockflow.exception.FiscalException;
import com.stockflow.fiscal.dto.NfceDTO;
import com.stockflow.fiscal.parser.NfceHtmlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class SefazProvider implements FiscalProvider {

    private final WebClient webClient;
    private final NfceHtmlParser parser;

    @Override
    public String getName() {
        return "SEFAZ";
    }

    @Override
    public boolean supports(String url) {
        return url != null && (
            url.contains("sefaz.") ||
            url.contains("nfce.") ||
            url.contains("portalsped.fazenda")
        );
    }

    @Override
    public NfceDTO fetchInvoice(String url) {
        log.info("Fetching NFC-e from SEFAZ: {}", url);
        try {
            String html = webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> {
                        throw new FiscalException("SEFAZ returned error: " + response.statusCode(),
                            HttpStatus.BAD_GATEWAY);
                    })
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(30))
                .block();

            if (html == null || html.isBlank()) {
                throw new FiscalException("Empty response from SEFAZ");
            }

            return parser.parse(html);
        } catch (FiscalException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching from SEFAZ: {}", e.getMessage());
            throw new FiscalException("Failed to fetch invoice from SEFAZ: " + e.getMessage());
        }
    }
}
