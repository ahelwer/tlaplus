package tlc2.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tla2sany.semantic.OpDeclNode;
import tla2sany.semantic.SymbolNode;
import tlc2.tool.StateVec;
import tlc2.tool.TLCState;
import tlc2.tool.TLCStateInfo;
import tlc2.value.IValue;
import util.ToolIO;
import util.UniqueString;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Set;

public class ErrorTraceMessagePrinterRecorderTest {
	
	private static PrintStream toolOutStream;
	
	@BeforeClass
	public static void silenceConsoleOutput() {
		toolOutStream = ToolIO.out;
		ToolIO.out = new PrintStream(OutputStream.nullOutputStream());
	}
	
	@AfterClass
	public static void resetConsoleOutput() {
		ToolIO.out = toolOutStream;
	}
	
	@Test
	public void TestPrintSafetyPropertyCounterExampleErrorTrace() {

		TLCState[] states = new TLCState[] {
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState()
		};
		
		TLCStateInfo[] expectedTrace = new TLCStateInfo[] {
				new TLCStateInfo(states[0]),
				new TLCStateInfo(states[1], ""),
				new TLCStateInfo(states[2], ""),
				new TLCStateInfo(states[3], ""),
				new TLCStateInfo(states[4], ""),
		};
		
		for (int i = 1; i < expectedTrace.length; i++) {
			expectedTrace[i].stateNumber = i + 1;
			expectedTrace[i].predecessorState = expectedTrace[i-1];
		}
		
		ErrorTraceMessagePrinterRecorder recorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(recorder);
		
		for (TLCStateInfo stateInfo : expectedTrace) {
			StatePrinter.printInvariantViolationStateTraceState(stateInfo);
		}
		
		/*
		assertTrue(recorder.getErrorTrace().map(
				traceType ->
					traceType.map(
						safety -> {
							Collection<TLCStateInfo> actualTrace = safety.getTrace().values();
							assertEquals(expectedTrace.length, actualTrace.size());
							int i = 0;
							for (TLCStateInfo actualState : actualTrace) {
								assertEquals(expectedTrace[i], actualState);
								i++;
							}

							return true;
						},
						stuttering -> false,
						lasso -> false
					)
				).orElse(false));
				*/

		MP.unsubscribeRecorder(recorder);
	}

	@Test
	public void TestPrintLivenessPropertyCounterExampleErrorTraceWithStuttering() {
		
		TLCState[] states = new TLCState[] {
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState()
		};
		
		TLCStateInfo[] expectedTrace = new TLCStateInfo[] {
				new TLCStateInfo(states[0]),
				new TLCStateInfo(states[1], ""),
				new TLCStateInfo(states[2], ""),
				new TLCStateInfo(states[3], ""),
				new TLCStateInfo(states[4], ""),
		};
		
		for (int i = 1; i < expectedTrace.length; i++) {
			expectedTrace[i].stateNumber = i + 1;
			expectedTrace[i].predecessorState = expectedTrace[i-1];
		}
		
		ErrorTraceMessagePrinterRecorder recorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(recorder);
		
		for (TLCStateInfo stateInfo : expectedTrace) {
			StatePrinter.printInvariantViolationStateTraceState(stateInfo);
		}
		
		StatePrinter.printStutteringState(expectedTrace.length + 1);
		
		/*
		assertTrue(recorder.getErrorTrace().map(
				traceType ->
					traceType.map(
						safety -> false,
						stuttering -> {
							Collection<TLCStateInfo> actualTrace = stuttering.getTrace().values();
							assertEquals(expectedTrace.length, actualTrace.size());
							int i = 0;
							for (TLCStateInfo actualState : actualTrace) {
								assertEquals(expectedTrace[i], actualState);
								i++;
							}

							return true;
						},
						lasso -> false
					)
				).orElse(false));
				*/
		
		MP.unsubscribeRecorder(recorder);
	}
	
	@Test
	public void TestPrintLivenessPropertyCounterExampleErrorTraceWithLasso() {
		
		TLCState[] states = new TLCState[] {
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState(),
				new StubTLCState()
		};
		
		TLCStateInfo[] expectedTrace = new TLCStateInfo[] {
				new TLCStateInfo(states[0]),
				new TLCStateInfo(states[1], "1"),
				new TLCStateInfo(states[2], "2"),
				new TLCStateInfo(states[3], "3"),
				new TLCStateInfo(states[4], "4"),
		};
		
		for (int i = 1; i < expectedTrace.length; i++) {
			expectedTrace[i].stateNumber = i + 1;
			expectedTrace[i].predecessorState = expectedTrace[i-1];
		}
		
		ErrorTraceMessagePrinterRecorder recorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(recorder);
		
		for (TLCStateInfo stateInfo : expectedTrace) {
			StatePrinter.printInvariantViolationStateTraceState(stateInfo);
		}
		
		StatePrinter.printBackToState(expectedTrace[2], 3);
		
		/*
		assertTrue(recorder.getErrorTrace().map(
				traceType ->
					traceType.map(
						safety -> false,
						stuttering -> false,
						lasso -> {
							Collection<TLCStateInfo> actualTrace = lasso.getTrace().values();
							assertEquals(expectedTrace.length, actualTrace.size());
							int i = 0;
							for (TLCStateInfo actualState : actualTrace) {
								assertEquals(expectedTrace[i], actualState);
								i++;
							}

							return true;
						}
					)
				).orElse(false));
				*/

		MP.unsubscribeRecorder(recorder);
	}
	
	private class StubTLCState extends TLCState {
		private static final long serialVersionUID = 1L;

		@Override
		public TLCState bind(UniqueString name, IValue value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TLCState bind(SymbolNode id, IValue value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TLCState unbind(UniqueString name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IValue lookup(UniqueString var) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean containsKey(UniqueString var) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public TLCState copy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TLCState deepCopy() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public StateVec addToVec(StateVec states) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deepNormalize() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public long fingerPrint() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean allAssigned() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Set<OpDeclNode> getUnassigned() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TLCState createEmpty() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String toString(TLCState lastState) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
