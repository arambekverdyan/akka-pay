package zork.enviromnment.messages;

import java.util.Optional;

public class DeviceFunctionality {

  public static final class RecordTemperature {
    public final long requestId;
    public final double value;

    public RecordTemperature(long requestId, double value) {
      this.requestId = requestId;
      this.value = value;
    }
  }

  public static final class TemperatureRecorded {
    public final long requestId;

    public TemperatureRecorded(long requestId) {
      this.requestId = requestId;
    }
  }

  public static final class ReadTemperature {
    public final long requestId;

    public ReadTemperature(long requestId) {
      this.requestId = requestId;
    }
  }

  public static final class RespondTemperature {
    public final long requestId;
    public final Optional<Double> value;

    public RespondTemperature(long requestId, Optional<Double> value) {
      this.requestId = requestId;
      this.value = value;
    }
  }
}