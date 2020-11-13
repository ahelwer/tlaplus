package tlc2.output;

import java.util.Optional;
import java.util.TreeMap;
import java.util.SortedMap;

import tlc2.model.MCError;
import tlc2.model.MCState;
import tlc2.tool.TLCStateInfo;
import util.OneOf;

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
 *  - DFID local model checking in {@link tlc2.tool.DFIDModelChecker#doNext}
 *  - Simulator local model checking in {@link tlc2.tool.Simulator#simulate}
 *  - Distributed model checking in {@link tlc2.tool.distributed.TLCServerThread#run}
 *  
 * The purpose of this class is to record error trace output from all of those sources while
 * ignoring output that is not an error trace (some of which superficially resembles error
 * traces, for example printing out an invalid/incomplete state transition).
 */
public class ErrorTraceMessagePrinterRecorder implements IMessagePrinterRecorder {
	
	/**
	 * We can either have:
	 *  - no state trace
	 *  - a state trace counterexample to a safety property
	 *  - a state trace counterexample to a liveness property:
	 *  	* ending in stuttering
	 *  	* ending in a lasso
	 * This field captures these possibilities in a way which forces consumers
	 * to explicitly handle each.
	 */
	private
		Optional<
			OneOf<
				SafetyPropertyCounterExampleStateTrace,
				LivenessPropertyCounterExampleStateTraceWithStuttering,
				LivenessPropertyCounterExampleStateTraceWithLasso
			>
		> trace = Optional.empty();
	
	@Override
	public void record(int code, Object... objects) {
		if (objects.length >= 2 && objects[0] instanceof TLCStateInfo && objects[1] instanceof Integer) {
			TLCStateInfo stateInfo = (TLCStateInfo)objects[0];
			Integer stateOrdinal = (Integer)objects[1];
			switch (code) {
				case EC.TLC_STATE_TRACE:
					stateInfo.stateNumber = stateOrdinal;

					// Idempotent transition from no trace to safety trace
					this.trace = Optional.of(this.trace.orElse(toSafetyTrace()));
					
					// Add state to existing safety trace; if we've seen the
					// stuttering or lasso markers, the state is ignored
					this.trace = this.trace.map(traceType ->
						traceType.mapFirst(safety ->
							safety.addState(stateOrdinal, stateInfo)));
					break;
				case EC.TLC_STUTTER_STATE:
					// Transition from safety trace to liveness trace ending in stuttering
					this.trace = this.trace.map(traceType -> 
						traceType.flatMapFirst(safety ->
							toStutteringTrace(safety)));
					break;
				case EC.TLC_BACK_TO_STATE:
					// Transition from safety trace to liveness trace ending in lasso
					this.trace = this.trace.map(traceType ->
						traceType.flatMapFirst(safety ->
							toLassoTrace(safety, stateOrdinal)));
					break;
				default:
					break;
			}
		}
	}
	
	public Optional<MCError> getMCErrorTrace() {
		MCError error = new MCError();
		this.trace.ifPresent(traceType -> traceType.ifPresent(
				safety -> {
					for (TLCStateInfo tlcState : safety.getTrace().values()) {
						MCState mcState = new MCState(tlcState);
						error.addState(mcState);
					}
				},
				stuttering -> { },
				lasso -> { }));
		return Optional.of(error);
	}
	
	/**
	 * Helper function to create a type with a very long name because Java
	 * doesn't have typedef.
	 * @return A safety property counter-example state trace.
	 */
	private
		OneOf<
			SafetyPropertyCounterExampleStateTrace,
			LivenessPropertyCounterExampleStateTraceWithStuttering,
			LivenessPropertyCounterExampleStateTraceWithLasso
		> toSafetyTrace() {
		return OneOf.first(new SafetyPropertyCounterExampleStateTrace());
	}

	/**
	 * Helper function to create a type with a very long name because Java
	 * doesn't have typedef.
	 * @return A liveness property stuttering counter-example state trace.
	 */
	private
		OneOf<
			SafetyPropertyCounterExampleStateTrace,
			LivenessPropertyCounterExampleStateTraceWithStuttering,
			LivenessPropertyCounterExampleStateTraceWithLasso
		> toStutteringTrace(SafetyPropertyCounterExampleStateTrace trace) {
		return OneOf.second(new LivenessPropertyCounterExampleStateTraceWithStuttering(trace));
	}

	/**
	 * Helper function to create a type with a very long name because Java
	 * doesn't have typedef.
	 * @return A liveness property lasso counter-example state trace.
	 */
	private
		OneOf<
			SafetyPropertyCounterExampleStateTrace,
			LivenessPropertyCounterExampleStateTraceWithStuttering,
			LivenessPropertyCounterExampleStateTraceWithLasso
		> toLassoTrace(SafetyPropertyCounterExampleStateTrace trace, int ordinal) {
		return OneOf.third(new LivenessPropertyCounterExampleStateTraceWithLasso(trace, ordinal));
	}

	/**
	 * State trace providing a counter-example to some hoped-for model property.
	 */
	public abstract class CounterExampleStateTrace {

		/**
		 * We use a sorted map in case states are given out of order.
		 */
		protected SortedMap<Integer, TLCStateInfo> trace;

		/**
		 * Gets a copy of the trace.
		 * @return A copy of the trace.
		 */
		public SortedMap<Integer, TLCStateInfo> getTrace() {
			return new TreeMap<Integer, TLCStateInfo>(this.trace);
		}
	}
	
	/**
	 * State trace providing a counter-example to a safety property.
	 */
	public class SafetyPropertyCounterExampleStateTrace extends CounterExampleStateTrace {

		/**
		 * Creates a new instance of this class.
		 */
		public SafetyPropertyCounterExampleStateTrace() {
			this.trace = new TreeMap<Integer, TLCStateInfo>();
		}
		
		/**
		 * Creates a new instance of this class.
		 * Copy constructor.
		 * @param other Class instance from which to copy.
		 */
		private SafetyPropertyCounterExampleStateTrace(SafetyPropertyCounterExampleStateTrace other) {
			this.trace = new TreeMap<Integer, TLCStateInfo>(other.trace);
		}
		
		/**
		 * Adds state to trace immutably.
		 * @param ordinal Position of state in the trace.
		 * @param info The state to add to the trace.
		 * @return A copy of this class instance with the state added.
		 */
		public SafetyPropertyCounterExampleStateTrace addState(int ordinal, TLCStateInfo info) {
			SafetyPropertyCounterExampleStateTrace other = new SafetyPropertyCounterExampleStateTrace(this);
			other.trace.put(ordinal, info);
			return other;
		}
	}
	
	/**
	 * State trace providing a stuttering counter-example to a liveness property.
	 */
	public class LivenessPropertyCounterExampleStateTraceWithStuttering extends CounterExampleStateTrace {

		/**
		 * Creates a new instance of this class.
		 * @param safetyTrace A safety property counter-example state trace.
		 */
		public LivenessPropertyCounterExampleStateTraceWithStuttering(
				SafetyPropertyCounterExampleStateTrace safetyTrace) {
			this.trace = new TreeMap<Integer, TLCStateInfo>(safetyTrace.getTrace());
		}
	}
	
	/**
	 * State trace providing a lasso counter-example to a liveness property.
	 */
	public class LivenessPropertyCounterExampleStateTraceWithLasso extends CounterExampleStateTrace {

		/**
		 * Position of state in trace at which trace terminates, in lasso.
		 */
		private int finalStateOrdinal = 0;

		/**
		 * Creates a new instance of this class.
		 * @param safetyTrace A safety property counter-example state trace.
		 * @param finalStateOrdinal Position of final state in trace.
		 */
		public LivenessPropertyCounterExampleStateTraceWithLasso(
				SafetyPropertyCounterExampleStateTrace safetyTrace,
				int finalStateOrdinal) {
			this.trace = new TreeMap<Integer, TLCStateInfo>(safetyTrace.getTrace());
			this.finalStateOrdinal = finalStateOrdinal;
		}
		
		/**
		 * Gets position of state in trace at which trace terminates.
		 * @return Position of state in trace at which trace terminates.
		 */
		public int getFinalStateOrdinal() {
			return this.finalStateOrdinal;
		}
	}
}
