package com.stockflow.fiscal.provider;

import com.stockflow.fiscal.dto.NfceDTO;

public interface FiscalProvider {

    String getName();

    boolean supports(String url);

    NfceDTO fetchInvoice(String url);
}
