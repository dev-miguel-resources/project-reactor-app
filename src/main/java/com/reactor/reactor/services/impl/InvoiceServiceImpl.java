package com.reactor.reactor.services.impl;

import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.reactor.reactor.models.Invoice;
import com.reactor.reactor.models.InvoiceDetail;
import com.reactor.reactor.repositories.IClientRepo;
import com.reactor.reactor.repositories.IDishRepo;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.repositories.IInvoiceRepo;
import com.reactor.reactor.services.IInvoiceService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends CRUDImpl<Invoice, String> implements IInvoiceService {

    private final IInvoiceRepo invoiceRepo;
    private final IClientRepo clientRepo;
    private final IDishRepo dishRepo;

    @Override
    protected IGenericRepo<Invoice, String> getRepo() {
        return invoiceRepo;
    }

    private Mono<Invoice> populateClient(Invoice invoice) {
        return clientRepo.findById(invoice.getClient().getId())
                // Una vez encontrado el cliente, lo asignamos a la factura
                .map(client -> {
                    invoice.setClient(client);
                    return invoice; // Devolver la factura ya con el id
                })
                // Retrasar la subscripción de 2 seg. (simular una latencia o control de
                // tiempos)
                .delaySubscription(Duration.ofSeconds(2));
    }

    private Mono<Invoice> populateItems(Invoice invoice) {
        // Por cada item de la factura, se busca el plato (Dish) en la bdd
        List<Mono<InvoiceDetail>> list = invoice.getItems().stream()
                .map(item -> dishRepo.findById(item.getDish().getId()) // Busco el plato por ID
                        .map(dish -> {
                            item.setDish(dish); // Vamos asignado el plato encontrado.
                            return item;
                        }))
                .toList(); // Convierte el stream en una lista de Mon<InvoiceDetail>

        // Esperar a que todo el flujo de la lista con los datos ya esté listo, recién
        // conforma y devuelve
        // ese invoice con el detalle.
        return Mono.when(list).then(Mono.just(invoice))
                // Retrasa la ejecución en 3 seg.
                .delaySubscription(Duration.ofSeconds(3));
    }

    private byte[] generatePDF(Invoice invoice) {
        try (InputStream stream = getClass().getResourceAsStream("/facturas.jrxml")) {
            // Definimos los parámetros que se enviarán al reporte. Por ej: nombre del
            // cliente
            Map<String, Object> params = new HashMap<>();
            params.put("txt_client", invoice.getClient().getFirstName() + " " + invoice.getClient().getLastName());

            // Compilar el archivo jrxml (plantilla del reporte)
            JasperReport report = JasperCompileManager.compileReport(stream);

            // Llenar el reporte con los datos de los params + la lista de items
            JasperPrint print = JasperFillManager.fillReport(report, params,
                    new JRBeanCollectionDataSource(invoice.getItems()));

            // Se deja exportable el reporte generado a un arreglo de bytes en formato PDF
            return JasperExportManager.exportReportToPdf(print);
        } catch (Exception e) {
            // En caso de error, retorne un arreglo vacío de bytes
            return new byte[0];
        }
    }

    @Override
    public Mono<byte[]> generateReport(String invoice) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateReport'");
    }

    // Generar un PDF a partir del Invoice generado.
    /*
     * @Override
     * public Mono<byte[]> generateReport(String idInvoice) {
     * long startTime = System.currentTimeMillis(); // Marca inicial de ejecución
     * 
     * return invoiceRepo.findById(idInvoice) // Buscamos el invoice por ID
     * .subscribeOn(Schedulers.single()) // Ejecuta en un hilo único (single thread)
     * .publishOn(Schedulers.newSingle("th-data")) // Genera un nuevo hilo llamado
     * "th-data"
     * .flatMap(invoice -> Mono.zip( // operador reactivo para ejecutar 2
     * procesamientos en paralelo
     * populateClient(invoice), // Me devuelve el client poblado
     * populateItems(invoice), // Poblamos los items
     * (populateClient, populatedItems) -> detailsComplete // Combinación de
     * resultados
     * ))
     * .publishOn(Schedulers.boundedElastic()) // Cambia a un pool elástico
     * .map(this::generatePDF) // Genera el PDF con el invoice final
     * }
     */

}
