package com.reactor.reactor.services;

import com.reactor.reactor.models.Invoice;

//import reactor.core.publisher.Mono;

public interface IInvoiceService extends ICRUD<Invoice, String> {

    // Mono<byte[]> generateReport(String invoice);

}
