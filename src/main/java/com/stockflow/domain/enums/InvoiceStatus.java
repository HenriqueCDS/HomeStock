package com.stockflow.domain.enums;

public enum InvoiceStatus {
    PENDING,    // QR code recebido, aguardando consulta
    FETCHED,    // dados consultados, aguardando confirmação
    CONFIRMED,  // confirmada e estoque atualizado
    REJECTED,   // rejeitada pelo usuário
    ERROR       // erro na consulta
}
