package org.eclipse.cargotracker.application.internal;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.eclipse.cargotracker.application.BookingService;
import org.eclipse.cargotracker.domain.model.cargo.*;
import org.eclipse.cargotracker.domain.model.location.Location;
import org.eclipse.cargotracker.domain.model.location.LocationRepository;
import org.eclipse.cargotracker.domain.model.location.UnLocode;
import org.eclipse.cargotracker.domain.service.RoutingService;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Stateless
public class DefaultBookingService implements BookingService {

    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private LocationRepository locationRepository;
    @Inject
    private RoutingService routingService;
    @Inject
    private Logger logger;

    @Override
    public TrackingId bookNewCargo(
            UnLocode originUnLocode, UnLocode destinationUnLocode, LocalDate arrivalDeadline) {
        TrackingId trackingId = cargoRepository.nextTrackingId();
        Location origin = locationRepository.find(originUnLocode);
        Location destination = locationRepository.find(destinationUnLocode);
        RouteSpecification routeSpecification =
                new RouteSpecification(origin, destination, arrivalDeadline);

        Cargo cargo = new Cargo(trackingId, routeSpecification);

        cargoRepository.store(cargo);
        logger.info(
                "Booked new cargo with tracking ID {}", cargo.getTrackingId().getIdString());

        return cargo.getTrackingId();
    }

    @Override
    public List<Itinerary> requestPossibleRoutesForCargo(TrackingId trackingId) {
        Cargo cargo = cargoRepository.find(trackingId);

        if (cargo == null) {
            return Collections.emptyList();
        }

        return routingService.fetchRoutesForSpecification(cargo.getRouteSpecification());
    }

    @Override
    public void assignCargoToRoute(Itinerary itinerary, TrackingId trackingId) {
        Cargo cargo = cargoRepository.find(trackingId);

        cargo.assignToRoute(itinerary);
        cargoRepository.store(cargo);

        logger.info("Assigned cargo {} to new route", trackingId);
    }

    @Override
    public void changeDestination(TrackingId trackingId, UnLocode unLocode) {
        Cargo cargo = cargoRepository.find(trackingId);
        Location newDestination = locationRepository.find(unLocode);

        RouteSpecification routeSpecification =
                new RouteSpecification(
                        cargo.getOrigin(), newDestination, cargo.getRouteSpecification().getArrivalDeadline());
        cargo.specifyNewRoute(routeSpecification);

        cargoRepository.store(cargo);

        logger.info(
                "Changed destination for cargo {0} to {1}",
                new Object[]{trackingId, routeSpecification.getDestination()});
    }

    @Override
    public void changeDeadline(TrackingId trackingId, LocalDate newDeadline) {
        Cargo cargo = cargoRepository.find(trackingId);

        RouteSpecification routeSpecification =
                new RouteSpecification(
                        cargo.getOrigin(), cargo.getRouteSpecification().getDestination(), newDeadline);
        cargo.specifyNewRoute(routeSpecification);

        cargoRepository.store(cargo);

        logger.info(
                "Changed deadline for cargo {0} to {1}",
                new Object[]{trackingId, newDeadline});
    }
}
