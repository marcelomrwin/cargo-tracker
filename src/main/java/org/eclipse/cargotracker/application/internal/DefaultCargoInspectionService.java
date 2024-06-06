package org.eclipse.cargotracker.application.internal;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.eclipse.cargotracker.application.ApplicationEvents;
import org.eclipse.cargotracker.application.CargoInspectionService;
import org.eclipse.cargotracker.domain.model.cargo.Cargo;
import org.eclipse.cargotracker.domain.model.cargo.CargoRepository;
import org.eclipse.cargotracker.domain.model.cargo.TrackingId;
import org.eclipse.cargotracker.domain.model.handling.HandlingEventRepository;
import org.eclipse.cargotracker.domain.model.handling.HandlingHistory;
import org.slf4j.Logger;

@Stateless
public class DefaultCargoInspectionService implements CargoInspectionService {

  @Inject private Logger logger;
  @Inject private ApplicationEvents applicationEvents;
  @Inject private CargoRepository cargoRepository;
  @Inject private HandlingEventRepository handlingEventRepository;

  @Override
  public void inspectCargo(TrackingId trackingId) {
    Cargo cargo = cargoRepository.find(trackingId);

    if (cargo == null) {
      logger.info("Can't inspect non-existing cargo {}", trackingId);
      return;
    }

    HandlingHistory handlingHistory =
        handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);

    cargo.deriveDeliveryProgress(handlingHistory);

    if (cargo.getDelivery().isMisdirected()) {
      applicationEvents.cargoWasMisdirected(cargo);
    }

    if (cargo.getDelivery().isUnloadedAtDestination()) {
      applicationEvents.cargoHasArrived(cargo);
    }

    cargoRepository.store(cargo);
  }
}
