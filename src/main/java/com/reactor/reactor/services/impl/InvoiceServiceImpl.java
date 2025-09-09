package com.reactor.reactor.services.impl;

import org.springframework.stereotype.Service;

import com.reactor.reactor.models.Invoice;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.repositories.IInvoiceRepo;
import com.reactor.reactor.services.IInvoiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService {

    private final IInvoiceRepo invoiceRepo;

    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    // métodos para implementar el generateReport

}
