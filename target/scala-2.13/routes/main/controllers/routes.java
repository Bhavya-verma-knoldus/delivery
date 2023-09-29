// @GENERATOR:play-routes-compiler
// @SOURCE:conf/routes

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseDeliveries Deliveries = new controllers.ReverseDeliveries(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseDeliveries Deliveries = new controllers.javascript.ReverseDeliveries(RoutesPrefix.byNamePrefix());
  }

}
