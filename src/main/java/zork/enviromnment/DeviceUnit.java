package zork.enviromnment;

import java.util.Optional;

class ReadTemperature {
  final long requestId;

  public ReadTemperature(long requestId) {
    this.requestId = requestId;
  }
}

class RespondTemperature {
  final long requestId;
  final Optional<Double> value;

  public RespondTemperature(long requestId, Optional<Double> value) {
    this.requestId = requestId;
    this.value = value;
  }
}