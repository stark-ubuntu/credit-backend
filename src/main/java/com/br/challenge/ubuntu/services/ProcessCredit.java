package com.br.challenge.ubuntu.services;

import com.br.challenge.ubuntu.entities.Transfer;
import com.starkbank.Invoice;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.math.BigDecimal;
import java.util.HashMap;

@ApplicationScoped
public class ProcessCredit {

    @ConsumeEvent(value = "process-credit")
    @Outgoing("challenge")
    @Transactional
    public HashMap<String, BigDecimal> consume(Invoice.Log log) {
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

        HashMap<String, BigDecimal> credit = new HashMap<>();
        credit.put(log.invoice.taxId, transferAmount);

        Transfer transfer = new Transfer();
        transfer.setAmount(transferAmount);
        transfer.setTaxId(log.invoice.taxId);
        transfer.setStatus("Pending");

        transfer.persist();

        return credit;
    }

}
