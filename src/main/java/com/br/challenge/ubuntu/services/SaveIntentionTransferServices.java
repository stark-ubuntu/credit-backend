package com.br.challenge.ubuntu.services;

import com.br.challenge.ubuntu.entities.Transfer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.io.Serializable;

@ApplicationScoped
public class SaveIntentionTransferServices implements Serializable {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void execute(Transfer transfer) {
        transfer.persistAndFlush();
    }
}
