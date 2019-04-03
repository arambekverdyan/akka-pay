package zork.enviromnment;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import zork.enviromnment.messages.DeviceLifecycle.RequestTrackDevice;

public class DeviceManager extends AbstractActor {
  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  public static Props props() {
    return Props.create(DeviceManager.class, DeviceManager::new);
  }

  final Map<String, ActorRef> groupIdToActor = new HashMap<>();
  final Map<ActorRef, String> actorToGroupId = new HashMap<>();

  @Override
  public void preStart() {
    log.info("DeviceManager started");
  }

  @Override
  public void postStop() throws Exception {
    log.info("DeviceManager stopped");
  }

  private void onTrackDevice(RequestTrackDevice trackMsg) {
    String groupId = trackMsg.groupId;
    ActorRef ref = groupIdToActor.get(groupId);
    if (ref != null) {
      ref.forward(trackMsg, getContext());
    } else {
      log.info("Creating device group actor for {}", groupId);
      ActorRef group = getContext().actorOf(DeviceGroup.props(groupId));
      getContext().watch(group);
      group.forward(trackMsg, getContext());
      groupIdToActor.put(groupId, group);
      actorToGroupId.put(group, groupId);
    }
  }

  private void onTerminated(Terminated t) {
    ActorRef group = t.getActor();
    String groupId = actorToGroupId.get(group);
    log.info("Device group actor for {} has been terminated", groupId);
    actorToGroupId.remove(group);
    groupIdToActor.remove(groupId);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
    .match(RequestTrackDevice.class, this::onTrackDevice)
    .match(Terminated.class, this::onTerminated)
    .build();
  }
}