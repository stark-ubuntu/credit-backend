package com.br.challenge.ubuntu.services;

import com.br.challenge.ubuntu.entities.Transfer;
import com.br.challenge.ubuntu.helpers.LoggingResource;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.starkbank.Invoice;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.math.BigDecimal;
import java.util.HashMap;

@ApplicationScoped
public class ProcessCreditServices extends LoggingResource {

    @Inject
    @Channel("challenge")
    Emitter<String> emitter;

    @Inject
    SaveIntentionTransferServices saveIntentionTransferServices;

    @ConsumeEvent(value = "process-credit")
    @Blocking
    public void execute(Invoice.Log log) {
        info("Event received and dealt with en bloc");
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

        Gson gson = new Gson();
        HashMap<String, JsonObject> credit = new HashMap<>();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("amount", transferAmount);
        jsonObject.addProperty("outTaxId", log.invoice.taxId);
        credit.put(log.invoice.taxId, jsonObject);
        String json = gson.toJson(credit);

        emitter.send(json);
        info("Send a message to the kafka topic indicating your intention to transfer.");

        Transfer transfer = new Transfer();
        transfer.setAmount(transferAmount.intValue());
        transfer.setTaxId(log.invoice.taxId);
        transfer.setStatus("Pending");

        saveIntentionTransferServices.execute(transfer);
        info("Transfer successfully save.");
    }


}
