package zork.enviromnment.messages;

import java.util.Set;

public class DeviceGroupOperations {

  public static final class RequestDeviceList {
    public final long requestId;

    public RequestDeviceList(long requestId) {
      this.requestId = requestId;
    }
  }

  public static final class ReplyDeviceList {
    public final long requestId;
    public final Set<String> ids;

    public ReplyDeviceList(long requestId, Set<String> ids) {
      this.requestId = requestId;
      this.ids = ids;
    }
  }
}