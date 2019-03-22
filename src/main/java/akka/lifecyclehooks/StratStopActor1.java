package akka.lifecyclehooks;

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
  public Receive createReceive() {
    return null;
  }
}