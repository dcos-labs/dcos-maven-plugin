package io.dcos;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class PingResource {

  @GET
  public String hi() {
    return "Hi!";
  }
}
