package org.eclipse.cargotracker.interfaces.booking.sse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
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
import org.slf4j.Logger;

/** Sever-sent events service for tracking all cargo in real time. */
@Singleton
@Path("/cargo")
public class RealtimeCargoTrackingService {
  @Inject private Logger logger;

  @Inject private CargoRepository cargoRepository;

  @Context private Sse sse;
  private SseBroadcaster broadcaster;

  @PostConstruct
  public void init() {
    broadcaster = sse.newBroadcaster();
    logger.info( "SSE broadcaster created.");
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
