package zork;

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * ZorkMain
 */
public class ZorkMain {

  public static void main(String... args) {
    ActorSystem system = ActorSystem.create("zork-system");

    try {
      // Create top level supervisor
      ActorRef supervisor = system.actorOf(ZorkSupervisor.props(), "zork-supervisor");

      System.out.println("Press enter to exit the system");
      System.in.read();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      system.terminate();
    }
  }
}