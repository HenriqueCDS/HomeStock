package com.stockflow.usecase;

import com.stockflow.domain.dto.invoice.InvoiceResponseDTO;
import com.stockflow.domain.entity.Invoice;
import com.stockflow.domain.entity.InvoiceItem;
import com.stockflow.domain.entity.Product;
import com.stockflow.domain.enums.InvoiceStatus;
import com.stockflow.exception.DuplicateResourceException;
import com.stockflow.fiscal.dto.NfceDTO;
import com.stockflow.fiscal.dto.NfceItemDTO;
import com.stockflow.fiscal.service.FiscalService;
import com.stockflow.mapper.InvoiceMapper;
import com.stockflow.repository.InvoiceRepository;
import com.stockflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "features.fiscal.enabled", havingValue = "true")
public class ProcessNfceUseCase {

    private final FiscalService fiscalService;
    private final InvoiceRepository invoiceRepository;
    private final ProductService productService;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceResponseDTO execute(UUID tenantId, String qrCodeContent) {
        NfceDTO nfceData = fiscalService.processQrCode(qrCodeContent);

        if (nfceData.getInvoiceKey() != null &&
            invoiceRepository.existsByInvoiceKeyAndTenantId(nfceData.getInvoiceKey(), tenantId)) {
            throw new DuplicateResourceException("Invoice", "key", nfceData.getInvoiceKey());
        }

        Invoice invoice = Invoice.builder()
            .tenantId(tenantId)
            .invoiceKey(nfceData.getInvoiceKey())
            .supplierName(nfceData.getSupplierName())
            .supplierCnpj(nfceData.getSupplierCnpj())
            .purchaseDate(nfceData.getPurchaseDate())
            .totalValue(nfceData.getTotalValue())
            .qrCodeUrl(qrCodeContent.startsWith("http") ? qrCodeContent : null)
            .status(InvoiceStatus.FETCHED)
            .build();

        List<InvoiceItem> items = buildItems(tenantId, invoice, nfceData.getItems());
        invoice.setItems(items);

        invoice = invoiceRepository.save(invoice);
        log.info("NFC-e processed: tenantId={}, invoiceKey={}, items={}", tenantId, nfceData.getInvoiceKey(), items.size());

        return invoiceMapper.toResponse(invoice);
    }

    private List<InvoiceItem> buildItems(UUID tenantId, Invoice invoice, List<NfceItemDTO> nfceItems) {
        List<InvoiceItem> items = new ArrayList<>();
        if (nfceItems == null) return items;

        for (NfceItemDTO nfceItem : nfceItems) {
            Product product = productService.findOrCreateByEan(
                tenantId, nfceItem.getEan(), nfceItem.getName(), nfceItem.getUnit());

            InvoiceItem item = InvoiceItem.builder()
                .invoice(invoice)
                .product(product)
                .productName(nfceItem.getName())
                .productEan(nfceItem.getEan())
                .quantity(nfceItem.getQuantity())
                .unitValue(nfceItem.getUnitValue())
                .totalValue(nfceItem.getTotalValue())
                .unit(nfceItem.getUnit())
                .build();
            items.add(item);
        }
        return items;
    }
}
