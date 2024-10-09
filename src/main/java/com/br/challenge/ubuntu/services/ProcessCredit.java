package com.br.challenge.ubuntu.services;

import com.br.challenge.ubuntu.entities.Transfer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.starkbank.Invoice;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jetbrains.annotations.Blocking;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ProcessCredit {

    @Inject
    @Channel("challenge") // O nome do canal para enviar as mensagens
    Emitter<String> emitter;

    @ConsumeEvent(value = "process-credit")
    @Blocking
    public void consume(Invoice.Log log) {
        BigDecimal amount = BigDecimal.valueOf(log.invoice.amount.intValue());
        BigDecimal interestAmount = BigDecimal.valueOf(log.invoice.interestAmount.intValue());
        BigDecimal fine = BigDecimal.valueOf(log.invoice.fine.doubleValue());
        BigDecimal fineAmount = BigDecimal.valueOf(log.invoice.fineAmount.intValue());
        BigDecimal interest = BigDecimal.valueOf(log.invoice.interest.doubleValue());

        BigDecimal transferAmount = amount
                .subtract(fine)
                .subtract(BigDecimal.valueOf(log.invoice.fee))
                .subtract(fineAmount)
                .add(interest)
                .add(interestAmount);

//        Transfer transfer = new Transfer();
//        transfer.setAmount(transferAmount);
//        transfer.setTaxId(log.invoice.taxId);
//        transfer.setStatus("Pending");
//        transfer.persist();

        Gson gson = new Gson();
        HashMap<String, JsonObject> credit = new HashMap<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("amount", transferAmount);
        jsonObject.addProperty("outTaxId", log.invoice.taxId);
        credit.put(log.invoice.taxId, jsonObject);
        String json = gson.toJson(credit);
        emitter.send(json);
    }


}
