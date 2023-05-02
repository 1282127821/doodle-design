package org.doodle.design.broker.frame;

import lombok.experimental.UtilityClass;
import org.doodle.broker.design.frame.*;

import java.util.Map;

@UtilityClass
public final class BrokerFrameUtils {

  public static BrokerFrame setup(Map<String, String> tags) {
    RouteSetup.Builder setup = RouteSetup.newBuilder().setTags(Tags.newBuilder().putAllTag(tags));
    return BrokerFrame.newBuilder().setSetup(setup).build();
  }

  public static BrokerFrame unicast(Map<String, String> tags) {
    return address(tags, RoutingType.UNICAST);
  }

  public static BrokerFrame multicast(Map<String, String> tags) {
    return address(tags, RoutingType.MULTICAST);
  }

  public static BrokerFrame address(Map<String, String> tags, RoutingType type) {
    Address.Builder address =
        Address.newBuilder().setTags(Tags.newBuilder().putAllTag(tags)).setRoutingType(type);
    return BrokerFrame.newBuilder().setAddress(address).build();
  }
}
