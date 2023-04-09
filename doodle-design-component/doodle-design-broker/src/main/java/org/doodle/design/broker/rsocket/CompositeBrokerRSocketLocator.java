package org.doodle.design.broker.rsocket;

import io.rsocket.RSocket;
import lombok.AllArgsConstructor;
import org.doodle.broker.design.frame.Address;
import org.doodle.broker.design.frame.RoutingType;

import java.util.List;

@AllArgsConstructor
public class CompositeBrokerRSocketLocator implements BrokerRSocketLocator {
  private final List<BrokerRSocketLocator> locators;

  @Override
  public boolean supports(RoutingType routingType) {
    for (BrokerRSocketLocator locator : locators) {
      if (locator.supports(routingType)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public RSocket locate(Address address) {
    for (BrokerRSocketLocator locator : locators) {
      if (locator.supports(address.getRoutingType())) {
        return locator.locate(address);
      }
    }
    throw new IllegalStateException("找不到对应的 RSocketLocator: " + address.getRoutingType());
  }
}
