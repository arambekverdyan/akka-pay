package zork;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import zork.enviromnment.DeviceGroup;
import zork.enviromnment.DeviceManager;
import zork.enviromnment.messages.Group.GroupRegistered;
import zork.enviromnment.messages.Group.RequestTrackGroup;

public class DeviceManagerTest {
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
  public void testActiveGroupsAfterOneShutdown() {
    TestKit probe = new TestKit(system);
    ActorRef manager = system.actorOf(DeviceManager.props());

    manager.tell(new RequestTrackGroup("group1"), probe.getRef());
    GroupRegistered group1 = probe.expectMsgClass(GroupRegistered.class);

    


  }
}