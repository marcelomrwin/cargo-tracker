package org.eclipse.cargotracker.interfaces.booking.facade.dto;

import org.eclipse.cargotracker.application.util.DateConverter;

import java.io.Serializable;
import java.time.LocalDateTime;

/** DTO for a leg in an itinerary. */
public class Leg implements Serializable {

  private static final long serialVersionUID = 1L;
  private final String voyageNumber;
  private final Location from;
  private final Location to;
  private final String loadTime;
  private final String unloadTime;

  public Leg(
      String voyageNumber,
      Location from,
      Location to,
      LocalDateTime loadTime,
      LocalDateTime unloadTime) {
    this.voyageNumber = voyageNumber;
    this.from = from;
    this.to = to;
    this.loadTime = DateConverter.toString(loadTime);
    this.unloadTime = DateConverter.toString(unloadTime);
  }

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public String getFrom() {
    return from.toString();
  }

  public String getFromUnLocode() {
    return from.getUnLocode();
  }

  public String getFromName() {
    return from.getName();
  }

  public String getTo() {
    return to.toString();
  }

  public String getToName() {
    return to.getName();
  }

  public String getToUnLocode() {
    return to.getUnLocode();
  }

  public String getLoadTime() {
    return loadTime;
  }

  public String getUnloadTime() {
    return unloadTime;
  }

  @Override
  public String toString() {
    return "Leg{"
        + "voyageNumber="
        + voyageNumber
        + ", from="
        + from.getUnLocode()
        + ", to="
        + to.getUnLocode()
        + ", loadTime="
        + loadTime
        + ", unloadTime="
        + unloadTime
        + '}';
  }
}
