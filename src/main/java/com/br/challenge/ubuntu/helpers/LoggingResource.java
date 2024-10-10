package com.br.challenge.ubuntu.helpers;

import org.jboss.logging.Logger;

public abstract class LoggingResource {

    protected final Logger LOG = Logger.getLogger(this.getClass());

    protected void info(String message) {
        LOG.info(message);
    }

    protected void error(String message, Throwable throwable) {
        LOG.error(message, throwable);
    }

}
