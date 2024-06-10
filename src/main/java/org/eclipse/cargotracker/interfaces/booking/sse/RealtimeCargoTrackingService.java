package org.eclipse.cargotracker.interfaces.booking.sse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.cargotracker.domain.model.cargo.Cargo;
import org.eclipse.cargotracker.domain.model.cargo.CargoRepository;
import org.eclipse.cargotracker.infrastructure.events.cdi.CargoUpdated;
import org.eclipse.cargotracker.infrastructure.rest.MetricCounterInterceptor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;

import java.util.List;

/**
 * Sever-sent events service for tracking all cargo in real time.
 */
@Singleton
@Path("/cargo")
public class RealtimeCargoTrackingService {
    @Inject
    private Logger logger;

    @Inject
    private CargoRepository cargoRepository;

    @Context
    private Sse sse;
    private SseBroadcaster broadcaster;

    @PostConstruct
    public void init() {
        broadcaster = sse.newBroadcaster();
        logger.info("SSE broadcaster created.");
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @MetricCounterInterceptor
    @Operation(description = "List all cargo tracking")
    public JsonArray getAllCargo() {

        List<Cargo> cargos = cargoRepository.findAll();

        JsonArrayBuilder builder = Json.createArrayBuilder();

        for (Cargo cargo : cargos) {
            builder.add(cargoToJson(cargo));
        }

        return builder.build();
    }

    private JsonObjectBuilder cargoToJson(Cargo cargo) {
        return Json.createObjectBuilder().add("trackingId", cargo.getTrackingId().getIdString())
                .add("routingStatus", cargo.getDelivery().getRoutingStatus().toString())
                .add("misdirected", cargo.getDelivery().isMisdirected())
                .add("transportStatus", cargo.getDelivery().getTransportStatus().toString())
                .add("atDestination", cargo.getDelivery().isUnloadedAtDestination())
                .add("origin", cargo.getOrigin().getUnLocode().getIdString()).add("lastKnownLocation",
                        cargo.getDelivery().getLastKnownLocation().getUnLocode().getIdString().equals("XXXXX")
                                ? "Unknown"
                                : cargo.getDelivery().getLastKnownLocation().getUnLocode().getIdString());
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void tracking(@Context SseEventSink eventSink) {
        cargoRepository.findAll().stream().map(this::cargoToSseEvent).forEach(eventSink::send);

        broadcaster.register(eventSink);
        logger.info("SSE event sink registered.");
    }

    @PreDestroy
    public void close() {
        broadcaster.close();
        logger.info("SSE broadcaster closed.");
    }

    public void onCargoUpdated(@ObservesAsync @CargoUpdated Cargo cargo) {
        logger.info("SSE event broadcast for cargo: {}", cargo);
        broadcaster.broadcast(cargoToSseEvent(cargo));
    }

    private OutboundSseEvent cargoToSseEvent(Cargo cargo) {
        return sse.newEventBuilder()
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(new RealtimeCargoTrackingViewAdapter(cargo))
                .build();
    }
}
