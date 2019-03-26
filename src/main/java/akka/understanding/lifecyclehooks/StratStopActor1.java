package akka.understanding.lifecyclehooks;

import akka.actor.AbstractActor;
import akka.actor.Props;

/**
 * StratStopActor1
 */
public class StratStopActor1 extends AbstractActor {

  static Props props() {
    return Props.create(StratStopActor1.class, StratStopActor1::new);
  }

  @Override
  public void preStart() {
    System.out.println("second started");
  }

  @Override
  public void postStop() {
    System.out.println("second stopped");
  }

  // Actor.emptyBehaviour is a useful placeholder when we don't
  // want to handle any messages int the actor
  @Override
  public Receive createReceive() {
    return receiveBuilder().build();
  }
}