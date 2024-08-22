package dslabs.clientserver;

import dslabs.atmostonce.AMOApplication;
import dslabs.atmostonce.AMOResult;
import dslabs.framework.Address;
import dslabs.framework.Application;
import dslabs.framework.Node;
import dslabs.kvstore.KVStore;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import dslabs.kvstore.KVStore.KVStoreResult;

/**
 * Simple server that receives requests and returns responses.
 *
 * <p>See the documentation of {@link Node} for important implementation notes.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class SimpleServer extends Node {
  // Your code here...
  private final AMOApplication<KVStore> app;

  /* -----------------------------------------------------------------------------------------------
   *  Construction and Initialization
   * ---------------------------------------------------------------------------------------------*/
  public SimpleServer(Address address, Application app) {
    super(address);

    // Your code here...
    this.app = new AMOApplication<>((KVStore) app);
  }

  @Override
  public void init() {
    // No initialization necessary
  }

  /* -----------------------------------------------------------------------------------------------
   *  Message Handlers
   * ---------------------------------------------------------------------------------------------*/
  private void handleRequest(Request m, Address sender) {
    // Your code here...
    AMOResult r = app.execute(m.command());
    send(new Reply(r), sender);
  }
}
