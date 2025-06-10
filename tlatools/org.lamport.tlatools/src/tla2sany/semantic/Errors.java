// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.

/***************************************************************************
* Every Semantic node has an errors field that is an Errors object.  A     *
* SpecObj object also has a few different kinds of Errors objects.  Here   *
* are the relevant methods:                                                *
*                                                                          *
*    addWarning                                                            *
*    addError                                                              *
*    addAbort   : These methods add the indicated level of error.          *
*                                                                          *
*    isSuccess()                                                           *
*    isFailure() : Indicates if addError or addAbort was called.           *
*                                                                          *
*    getNumErrors()                                                        *
*    getNumAbortsAndErrors()                                               *
*    getNumMessages()        : Return approximately obvious values.        *
*                                                                          *
*    toString() : Returns all the errors as a single string.               *
***************************************************************************/
package tla2sany.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tla2sany.semantic.ErrorCode.ErrorLevel;
import tla2sany.st.Location;

public class Errors {

  public static class ErrorDetails {

    private final ErrorCode code;

    private final Location location;

    private final String message;

    public ErrorDetails(ErrorCode code, Location location, String message) {
      this.code = code;
      this.location = location;
      this.message = message;
    }

    public ErrorCode getCode() {
      return this.code;
    }

    public Location getLocation() {
      return this.location;
    }

    public String getMessage() {
      return this.message;
    }

    @Override
    public String toString() {
      return this.location.toString() + "\n\n" + this.message;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ErrorDetails)) {
        return false;
      }
      final ErrorDetails other = (ErrorDetails)o;
      return this.code.equals(other.code)
          && this.location.equals(other.location)
          && this.message.equals(other.message);
    }
  }

  private List<ErrorDetails> messages = new ArrayList<ErrorDetails>();
  
  private static boolean atLeastLevel(ErrorCode code, ErrorLevel base) {
    return code.getSeverityLevel().compareTo(base) >= 0;
  }

  public List<ErrorDetails> getMessagesOfLevel(ErrorLevel level) {
    return this.messages.stream().filter(
        msg -> msg.getCode().getSeverityLevel().equals(level)
      ).collect(Collectors.toList());
  }
  
  public List<ErrorDetails> getMessagesOfAtLeastLevel(ErrorLevel level) {
    return this.messages.stream().filter(
        msg -> Errors.atLeastLevel(msg.getCode(), level)
      ).collect(Collectors.toList());
  }

  public List<ErrorDetails> getAbortDetails() {
    return this.getMessagesOfLevel(ErrorLevel.ABORT);
  }

  public String[] getAborts() {
    return this.getAbortDetails().stream()
        .map(ErrorDetails::toString).toArray(String[]::new);
  }

  public List<ErrorDetails> getErrorDetails() {
    return this.getMessagesOfLevel(ErrorLevel.ERROR);
  }

  public String[] getErrors() {
    return this.getErrorDetails().stream()
        .map(ErrorDetails::toString).toArray(String[]::new);
  }

  public List<ErrorDetails> getWarningDetails() {
    return this.getMessagesOfLevel(ErrorLevel.WARNING);
  }

  public String[] getWarnings() {
    return this.getWarningDetails().stream()
        .map(ErrorDetails::toString).toArray(String[]::new);
  }

  public List<ErrorDetails> getLintDetails() {
    return this.getMessagesOfLevel(ErrorLevel.LINT);
  }

  public String[] getLint() {
    return this.getLintDetails().stream()
        .map(ErrorDetails::toString).toArray(String[]::new);
  }

  /**
   * Ensures the given error code is of the expected level, and throws an
   * illegal argument exception if not. Ideally in the future this method
   * will not be necessary as all logging shifts to use addMessage() instead
   * of level-specific methods; currently the error level information is
   * duplicated in both the {@link ErrorCode} and choice of method.
   *
   * @param code The code associated with the message under consideration.
   * @param expected The expected code level.
   */
  private void validateLevel(ErrorCode code, ErrorLevel expected) {
    if (!code.getSeverityLevel().equals(expected)) {
      throw new IllegalArgumentException(
        "Expected message of level " + expected.toString()
        + " but received message of level " + code.getSeverityLevel().toString()
      );
    }
  }

  /**
   * Whether to record the message in the log. To avoid unnecessary memory
   * usage, {@link ErrorLevel.DEBUG} and {@link ErrorLevel.INFO} messages are
   * not logged; only messages of {@link ErrorLevel.LINT} and higher are
   * logged for later retrieval.
   *
   * @param code The code associated with the message under consideration.
   * @return Whether to store the message in the log.
   */
  private boolean shouldRecordMessage(ErrorCode code) {
    return code.getSeverityLevel().compareTo(ErrorLevel.LINT) >= 0;
  }

  /**
   * Append a message to the log. The message will only be recorded if it is
   * {@link ErrorLevel.LINT} level or higher. If location is null, the value
   * {@link Location.nullLoc} is assigned. Idempotent; will not append the
   * same message to the log multiple times.
   *
   * @param code The standardized error code associated with the message.
   * @param loc A spec location associated with the message.
   * @param str A human-readable message.
   */
  public final void addMessage(ErrorCode code, Location loc, String str) {
    loc = null == loc ? Location.nullLoc : loc;
    final ErrorDetails message = new ErrorDetails(code, loc, str);
    if (shouldRecordMessage(code) && !this.messages.contains(message)) {
      this.messages.add(message);
    }
  }

  public final void addWarning(ErrorCode code, Location loc, String str) {
    validateLevel(code, ErrorLevel.WARNING);
    this.addMessage(code, loc, str);
  }

  public final void addError(ErrorCode code, Location loc, String str) {
    validateLevel(code, ErrorLevel.ERROR);
    this.addMessage(code, loc, str);
  }

  public final void addAbort(
      ErrorCode code,
      Location loc,
      String str,
      boolean abort
  ) throws AbortException {
    validateLevel(code, ErrorLevel.ABORT);
    this.addMessage(code, loc, str);
    if (abort){
      throw new AbortException();
    }
  }


  public final boolean isSuccess() {
    return this.getAbortDetails().isEmpty()
        && this.getErrorDetails().isEmpty();
  }

  public final boolean isFailure() {
    return !this.isSuccess();
  }

  public final int getNumErrors() {
    return this.getErrorDetails().size();
  }

  public final int getNumAbortsAndErrors() {
    return this.getAbortDetails().size() + this.getErrorDetails().size();
  }

  public final int getNumMessages() {
    return this.messages.size();
  }

  public final String toString()  {
    StringBuffer ret = new StringBuffer("");

    final List<ErrorDetails> aborts = this.getAbortDetails();
    ret.append((aborts.size() > 0) ? "*** Abort messages: " + aborts.size() + "\n\n" : "");
    for (final ErrorDetails error : aborts)   {
      ret.append(error.toString() + "\n\n\n");
    }

    final List<ErrorDetails> errors = this.getErrorDetails();
    ret.append((errors.size() > 0) ? "*** Errors: " + errors.size() + "\n\n" : "");
    for (final ErrorDetails error : errors)   {
      ret.append(error.toString() + "\n\n\n");
    }

    final List<ErrorDetails> warnings = this.getWarningDetails();
    ret.append((warnings.size() > 0) ? "*** Warnings: " + warnings.size() + "\n\n" : "");
    for (final ErrorDetails error : warnings) {
      ret.append(error.toString() + "\n\n\n");
    }

    final List<ErrorDetails> lint = this.getWarningDetails();
    ret.append((lint.size() > 0) ? "*** Lint: " + lint.size() + "\n\n" : "");
    for (final ErrorDetails error : lint) {
      ret.append(error.toString() + "\n\n\n");
    }

    return ret.toString();
  }
}
