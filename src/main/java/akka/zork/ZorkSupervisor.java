package akka.zork;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * ZorkSupervisor
 */
public class ZorkSupervisor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  public static Props props() {
    return Props.create(ZorkSupervisor.class, ZorkSupervisor::new);
  }

  @Override
  public void preStart() {
    log.info("Zork started");
  }

  @Override
  public void postStop() {
    log.info("Zork stopped");
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder().build();
  }

}