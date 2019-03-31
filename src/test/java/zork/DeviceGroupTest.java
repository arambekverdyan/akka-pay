package zork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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
