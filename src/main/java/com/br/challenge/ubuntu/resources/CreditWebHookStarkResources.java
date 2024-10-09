package com.br.challenge.ubuntu.resources;

import com.google.gson.*;
import com.starkbank.Event;
import com.starkbank.Invoice;
import io.vertx.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/credit")
public class CreditWebHookStarkResources {

    @Inject
    EventBus eventBus;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response credit(String request){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Event.class, new Event.Deserializer());
        Gson gson = gsonBuilder
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        JsonElement jsonElement = JsonParser.parseString(request);

        Event event = gson.fromJson(((JsonObject) jsonElement).get("event"), Event.class);

        Invoice.Log log = ((Event.InvoiceEvent) event).log;

        eventBus.send("process-credit", log);

        return Response.ok().build();
    }
}
