package zork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.testkit.javadsl.TestKit;
import zork.enviromnment.Device;
import zork.enviromnment.DeviceGroup;
import zork.enviromnment.DeviceManager;

public class DeviceGroupTest {
  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void teardown() {
    TestKit.shutdownActorSystem(system);
  }

  @Test
  public void testListActiveDevices() {
    TestKit probe = new TestKit(system);
    ActorRef group = system.actorOf(DeviceGroup.props("group"));

    group.tell(new DeviceManager.RequestTrackDevice("group", "device1"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);

    group.tell(new DeviceManager.RequestTrackDevice("group", "device2"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);

    group.tell(new DeviceGroup.RequestDeviceList(0L), probe.getRef());
    DeviceGroup.ReplyDeviceList reply = probe.expectMsgClass(DeviceGroup.ReplyDeviceList.class);
    assertEquals(0L, reply.requestId);
    assertEquals(Stream.of("device1", "device2").collect(Collectors.toSet()), reply.ids);
  }

  @Test
  public void testListActiveDevicesAfterOneShutDown() {
    TestKit probe = new TestKit(system);
    ActorRef group = system.actorOf(DeviceGroup.props("group"));

    group.tell(new DeviceManager.RequestTrackDevice("group", "device1"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
    ActorRef toShutDown = probe.getLastSender();

    group.tell(new DeviceManager.RequestTrackDevice("group", "device2"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);

    group.tell(new DeviceGroup.RequestDeviceList(0L), probe.getRef());
    DeviceGroup.ReplyDeviceList reply = probe.expectMsgClass(DeviceGroup.ReplyDeviceList.class);
    assertEquals(0L, reply.requestId);
    assertEquals(Stream.of("device1", "device2").collect(Collectors.toSet()), reply.ids);

    probe.watch(toShutDown);
    toShutDown.tell(PoisonPill.getInstance(), ActorRef.noSender());
    probe.expectTerminated(toShutDown);

    // using awaitAssert to retry because it might take longer for the groupActor
    // to see the Terminated, that order is undefined
    probe.awaitAssert(() -> {
      group.tell(new DeviceGroup.RequestDeviceList(1L), probe.getRef());
      DeviceGroup.ReplyDeviceList r = probe.expectMsgClass(DeviceGroup.ReplyDeviceList.class);
      assertEquals(1L, r.requestId);
      assertEquals(Stream.of("device2").collect(Collectors.toSet()), r.ids);
      return null;
    });

  }

  @Test
  public void testRegisterDeviceActor() {
    TestKit probe = new TestKit(system);
    ActorRef group = system.actorOf(DeviceGroup.props("group"));

    group.tell(new DeviceManager.RequestTrackDevice("group", "device1"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
    ActorRef device1 = probe.getLastSender();

    group.tell(new DeviceManager.RequestTrackDevice("group", "device2"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
    ActorRef device2 = probe.getLastSender();
    assertNotEquals(device1, device2);

    // Check that the device actors are working
    device1.tell(new Device.RecordTemperature(0L, 1.0), probe.getRef());
    assertEquals(0L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);
    device2.tell(new Device.RecordTemperature(1L, 2.0), probe.getRef());
    assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);
  }

  @Test
  public void testIgnoreRequestsForWrongId() {
    TestKit probe = new TestKit(system);
    ActorRef group = system.actorOf(DeviceGroup.props("group"));

    group.tell(new DeviceManager.RequestTrackDevice("groupId", "device1"), probe.getRef());
    probe.expectNoMessage();
  }

  @Test
  public void testReturnSameActorForSameDeviceId() {
    TestKit probe = new TestKit(system);
    ActorRef group = system.actorOf(DeviceGroup.props("group"));

    group.tell(new DeviceManager.RequestTrackDevice("group", "device1"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
    ActorRef device1 = probe.getLastSender();

    group.tell(new DeviceManager.RequestTrackDevice("group", "device1"), probe.getRef());
    probe.expectMsgClass(DeviceManager.DeviceRegistered.class);
    ActorRef device2 = probe.getLastSender();

    assertEquals(device1, device2);
  }
}
