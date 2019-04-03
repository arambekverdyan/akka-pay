package zork.enviromnment;

import java.util.Optional;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import zork.enviromnment.messages.DeviceFunctionality;
import zork.enviromnment.messages.DeviceLifecycle.DeviceRegistered;
import zork.enviromnment.messages.DeviceLifecycle.RequestTrackDevice;

public class Device extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  final String groupId;
  final String deviceId;

  public Device(String groupId, String deviceId) {
    this.groupId = groupId;
    this.deviceId = deviceId;
  }

  public static Props props(String groupId, String deviceId) {
    return Props.create(Device.class, () -> new Device(groupId, deviceId));
  }

  Optional<Double> lastTemperatureReading = Optional.empty();

  @Override
  public void preStart() {
    log.info("Device actoe {}-{} started", groupId, deviceId);
  }

  @Override
  public void postStop() {
    log.info("Device actor {}-{} stopped", groupId, deviceId);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
    .match(RequestTrackDevice.class, r -> {
      if (this.groupId.equals(r.groupId) && this.deviceId.equals(r.deviceId)) {
        getSender().tell(new DeviceRegistered(), getSelf());
      } else {
        log.warning(
          "Ignoring TrackDevice request for {}-{}. This actor is responsible for {}-{}.",
          r.groupId,
          r.deviceId,
          this.groupId,
          this.deviceId
        );
      }
    })
    .match(DeviceFunctionality.RecordTemperature.class, r -> {
      log.info("Recorded temperature reading {} with {}", r.value, r.requestId);
      lastTemperatureReading = Optional.of(r.value);
      getSender().tell(new DeviceFunctionality.TemperatureRecorded(r.requestId), getSelf());
    }).match(DeviceFunctionality.ReadTemperature.class, r -> {
      getSender().tell(new DeviceFunctionality.RespondTemperature(r.requestId, lastTemperatureReading), getSelf());
    }).build();
  }
}