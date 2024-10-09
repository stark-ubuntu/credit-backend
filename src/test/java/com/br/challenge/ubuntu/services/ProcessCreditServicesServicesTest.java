package com.br.challenge.ubuntu.services;

import com.br.challenge.ubuntu.entities.Transfer;
import com.starkbank.Invoice;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@QuarkusTest
class ProcessCreditServicesServicesTest {

    @Inject
    SaveIntentionTransferServices saveIntentionTransferServices;


    @Test
    void shouldSendMessageToKafkaAndSaveTransferIntention() throws Exception {
        PanacheMock.mock(Transfer.class);
        Transfer transferMock = Mockito.mock(Transfer.class);

        ProcessCreditServices processCreditServices = new ProcessCreditServices();
        processCreditServices.emitter =  Mockito.mock(Emitter.class);
        processCreditServices.saveIntentionTransferServices = saveIntentionTransferServices;
        HashMap<String, Object> params = new HashMap<>();
        params.put("amount", 100);
        params.put("taxId", "856.661.130-62");
        params.put("fine", 0);
        params.put("interest", 100);
        Invoice invoice = new Invoice(params);

        invoice.interestAmount = 100;
        invoice.fee = 0;
        invoice.fineAmount = 0;

        Invoice.Log log = new Invoice.Log(
                "2020-03-26T18:00:05.165485+00:00",
                "paid",
                new String[0],
                invoice,
                "5096976731340800"
        );

        Transfer transfer = new Transfer();
        transfer.setId(UUID.fromString("0917aa8a-076c-4e1f-ac81-503e1c4bfb2e"));
        transfer.setStatus("Pending");
        transfer.setAmount(100);
        transfer.setTaxId("856.661.130-62");

        when(Transfer.findByTaxId("856.661.130-62")).thenReturn(transfer);
        doNothing().when(transferMock).persistAndFlush();


        processCreditServices.execute(log);

        Transfer result = Transfer.findByTaxId("856.661.130-62");

        assertEquals(result.getTaxId(), invoice.taxId);

    }

}