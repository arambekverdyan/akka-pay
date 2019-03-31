package zork.enviromnment;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DeviceGroup extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  final String groupId;

  public DeviceGroup(String groupId) {
    this.groupId = groupId;
  }

  public static Props props(String groupId) {
    return Props.create(DeviceGroup.class, () -> new DeviceGroup(groupId));
  }

  final Map<String, ActorRef> deviceIdToActor = new HashMap<>();

  @Override
  public void preStart() {
    log.info("DeviceGroup {} started", groupId);
  }

  @Override
  public void postStop() {
    log.info("DeviceGroup {} stopped", groupId);
  }

  private void onTrackDevice(DeviceManager.RequestTrackDevice trackMsg) {
    if (this.groupId.equals(trackMsg.groupId)) {
      ActorRef device = deviceIdToActor.get(trackMsg.deviceId);
      if (device == null) {
        log.info("Creating device actor for {}", trackMsg.deviceId);
        device = getContext().actorOf(Device.props(groupId, trackMsg.deviceId),
"device-" + trackMsg.deviceId);
        deviceIdToActor.put(trackMsg.deviceId, device);
        device.forward(trackMsg, getContext());
      } else {
        device.forward(trackMsg, getContext());
      }
    } else {
      log.warning(
        "Ingnoring TrackDevice request for {}. This actor is responsible for {}.",
        groupId,
        this.groupId);
    }
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
      .match(DeviceManager.RequestTrackDevice.class, this::onTrackDevice)
      .build();
  }
}