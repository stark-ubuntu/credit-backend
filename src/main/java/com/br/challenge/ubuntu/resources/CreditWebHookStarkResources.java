package com.br.challenge.ubuntu.resources;

import com.br.challenge.ubuntu.helpers.LoggingResource;
import com.starkbank.Event;
import com.starkbank.Invoice;
import io.vertx.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/credit")
public class CreditWebHookStarkResources extends LoggingResource {

    private static final Logger LOG = Logger.getLogger(CreditWebHookStarkResources.class);

    @Inject
    EventBus eventBus;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response credit(@Context HttpHeaders headers, String request) throws Exception {
        info("Receiving invoice event");
        String signature = headers.getHeaderString("Digital-Signature");

        Event event = Event.parse(request, signature);
        info("Authorized request");

        Invoice.Log log = ((Event.InvoiceEvent) event).log;

        eventBus.send("process-credit", log);
        info("Event triggered in threads");

        return Response.ok().build();
    }
}
