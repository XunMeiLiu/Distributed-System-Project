package dslabs.clientserver;

import static dslabs.clientserver.ClientTimer.CLIENT_RETRY_MILLIS;

import com.google.common.base.Objects;
import dslabs.atmostonce.AMOCommand;
import dslabs.atmostonce.AMOResult;
import dslabs.framework.Address;
import dslabs.framework.Client;
import dslabs.framework.Command;
import dslabs.framework.Node;
import dslabs.framework.Result;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Simple client that sends requests to a single server and returns responses.
 *
 * <p>See the documentation of {@link Client} and {@link Node} for important implementation notes.
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
class SimpleClient extends Node implements Client {
  private final Address serverAddress;

  // Your code here...
  private Address address;
  private AMOCommand request;
  private AMOResult reply;
  private int sequenceNum;

  /* -----------------------------------------------------------------------------------------------
   *  Construction and Initialization
   * ---------------------------------------------------------------------------------------------*/
  public SimpleClient(Address address, Address serverAddress) {
    super(address);
    this.address = address;
    this.serverAddress = serverAddress;
    this.sequenceNum = 0;
  }

  @Override
  public synchronized void init() {
    // No initialization necessary
  }

  /* -----------------------------------------------------------------------------------------------
   *  Client Methods
   * ---------------------------------------------------------------------------------------------*/
  @Override
  public synchronized void sendCommand(Command command) {
    // Your code here...
//    if (!(command instanceof AMOCommand)) {
//      throw new IllegalArgumentException();
//    }
    sequenceNum++;
    this.request = new AMOCommand(command, this.sequenceNum, this.address);;
    this.reply = null;

//    send(new Request(this.request.command(), sequenceNum), serverAddress);
    Request req = new Request(this.request);
    send(req, serverAddress);
    set(new ClientTimer(this.request), CLIENT_RETRY_MILLIS);
  }

  @Override
  public synchronized boolean hasResult() {
    // Your code here...
    return reply != null;
  }

  @Override
  public synchronized Result getResult() throws InterruptedException {
    // Your code here...
    while (reply == null) {
      wait();
    }
    return reply.result();
  }

  /* -----------------------------------------------------------------------------------------------
   *  Message Handlers
   * ---------------------------------------------------------------------------------------------*/
  private synchronized void handleReply(Reply m, Address sender) {
    // Your code here...
    if (request.sequenceNum() == m.result().sequenceNum()) {
      this.reply = m.result();
      notify();
    }
  }

  /* -----------------------------------------------------------------------------------------------
   *  Timer Handlers
   * ---------------------------------------------------------------------------------------------*/
  private synchronized void onClientTimer(ClientTimer t) {
    // Your code here...
    if (Objects.equal(this.request.sequenceNum(), t.request().sequenceNum()) && this.reply == null) {
      send(new Request(this.request), serverAddress);  // same sequenceNum since same command
      set(t, CLIENT_RETRY_MILLIS);
    }
  }
}
