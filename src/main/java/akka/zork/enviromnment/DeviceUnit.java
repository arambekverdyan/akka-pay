package akka.zork.enviromnment;

import java.util.Optional;

class ReadTemperature {
}

class RespondTemperature {
  final Optional<Double> value;

  public RespondTemperature(Optional<Double> value) {
    this.value = value;
  }
}