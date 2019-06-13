package zork.enviromnment.messages;

public class Group {
  public static class RequestTrackGroup {
    public final String groupId;

    public RequestTrackGroup(String groupId) {
      this.groupId = groupId;
    }
  }

  public static class GroupRegistered {}
}