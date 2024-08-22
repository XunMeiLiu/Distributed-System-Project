package dslabs.atmostonce;

import dslabs.framework.Address;
import dslabs.framework.Application;
import dslabs.framework.Command;
import dslabs.framework.Result;
import java.util.HashMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import java.util.Map;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class AMOApplication<T extends Application> implements Application {
  @Getter @NonNull private final T application;

  // Your code here...
  private Map<Address, AMOResult> pastResults = new HashMap<>();

  @Override
  public AMOResult execute(Command command) {
    if (!(command instanceof AMOCommand)) {
      throw new IllegalArgumentException();
    }

    AMOCommand amoCommand = (AMOCommand) command;

    if (alreadyExecuted(amoCommand)) {
      AMOResult res = pastResults.get(amoCommand.address());
      return res;
    } else {
      Result res = application.execute(amoCommand.command());
      AMOResult amoRes = new AMOResult(res, amoCommand.sequenceNum());
      pastResults.put(amoCommand.address(), amoRes);
      return amoRes;
    }
  }

  public Result executeReadOnly(Command command) {
    if (!command.readOnly()) {
      throw new IllegalArgumentException();
    }

    if (command instanceof AMOCommand) {
      return execute(command);
    }

    return application.execute(command);
  }

  public boolean alreadyExecuted(AMOCommand amoCommand) {
    // Your code here...
    return pastResults.containsKey(amoCommand.address()) && amoCommand.sequenceNum() <= pastResults.get(amoCommand.address()).sequenceNum();
  }
}
