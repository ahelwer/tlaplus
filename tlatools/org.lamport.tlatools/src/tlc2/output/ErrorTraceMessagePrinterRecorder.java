/*******************************************************************************
 * Copyright (c) 2015 Microsoft Research. All rights reserved. 
 *
 * The MIT License (MIT)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/

package tlc2.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import tlc2.model.MCError;
import tlc2.tool.TLCStateInfo;
import tlc2.tool.TLCState;

/**
 * Saves all messages containing info about error traces that pass through {@link tlc.output.MP}.
 * Ideally this will eventually go away and all of TLC's model checking implementations will
 * bubble their error traces up through their top-level .run() methods, but until that
 * refactoring takes place this is how we get the error trace: by hooking into the static
 * console output handler class and intercepting TLC output.
 * 
 * There are a number of places that error traces are generated within TLC:
 *  - Basic local BFS model checking in {@link tlc2.tool.ModelChecker#doNextCheckInvariants}
 *  - Concurrent local BFS model checking in {@link tlc2.tool.Worker#doNextCheckInvariants}
 *  - Local DFID model checking in {@link tlc2.tool.DFIDModelChecker#doNext}
 *  - Simulator model checking in {@link tlc2.tool.Simulator#simulate}
 *  - Distributed model checking in {@link tlc2.tool.distributed.TLCServerThread#run}
 *  
 * The purpose of this class is to record error trace output from all of those sources while
 * ignoring output that is not an error trace (some of which superficially resembles error
 * traces, for example printing out an invalid/incomplete state transition).
 */
public class ErrorTraceMessagePrinterRecorder implements IMessagePrinterRecorder {
	private Optional<String> failedInvariantName = Optional.empty();
	
	private List<TLCState> trace = new ArrayList<TLCState>();
	
	/**
	 * The {@link EC#TLC_STATE_PRINT1} error code is generally used to record runtime errors
	 * (incomplete next state definition, invalid steps, etc.) which we want to ignore. The
	 * exception is with DFID model checking, which also prints its state traces using the 
	 * {@link EC#TLC_STATE_PRINT1} error code. We can distinguish this case because DFID
	 * model checking will first print the {@link EC#TLC_INVARIANT_VIOLATED_BEHAVIOR} error
	 * code. If this recorder sees that error code, this flag is set to true.
	 */
	private boolean recordTlcStatePrint1 = false;
	
	@Override
	public void record(int code, Object... objects) {
		if (EC.TLC_INVARIANT_VIOLATED_BEHAVIOR == code)
		{
			
		}
		switch (code) {
			case EC.TLC_INVARIANT_VIOLATED_BEHAVIOR:
				if (objects.length >= 1 && objects[0] instanceof String) {
					this.failedInvariantName = Optional.ofNullable((String)objects[0]);
				}

				break;
			case EC.TLC_STATE_PRINT1:
				// Unknown
				break;
			case EC.TLC_STATE_TRACE:
				if (objects.length >= 2 && objects[0] instanceof TLCStateInfo && objects[1] instanceof Integer) {

				}
				break;
			case EC.TLC_STUTTER_STATE:
				// Stuttering?
				break;
			case EC.TLC_BACK_TO_STATE:
				// Liveness checking?
				break;
			default:
				break;
		}
	}
	
	/**
	 * Returns an error trace reconstructed from recorded messages, if one exists.
	 */
	public Optional<MCError> getErrorTrace()
	{
		MCError error = new MCError(this.failedInvariantName.orElse(null));
		return Optional.empty();
	}
	
	public class SafetyErrorTrace
	{
	}
}
