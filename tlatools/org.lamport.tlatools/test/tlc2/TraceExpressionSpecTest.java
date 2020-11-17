package tlc2;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.junit.Test;

import util.TLAConstants;
import util.SimpleFilenameToStream;
import tlc2.model.MCError;
import tlc2.model.MCState;
import tlc2.model.Utils;
import tlc2.output.ErrorTraceMessagePrinterRecorder;
import tlc2.output.MP;

public class TraceExpressionSpecTest {
	
	@Test
	public void testSetOutputDirectory() {
		String expected = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		FakeStreamProvider streams = new FakeStreamProvider(expected, null, null);
		ErrorTraceMessagePrinterRecorder recorder = new FakeErrorRecorder(null);
		TraceExpressionSpec teSpec = new TraceExpressionSpec(streams, recorder);
		assertEquals(expected, teSpec.getOutputDirectory());
		expected = "some-other-directory-name";
		teSpec.setOutputDirectory(expected);
		assertEquals(expected, teSpec.getOutputDirectory());
	}
	
	@Test
	public void integrationTestSafetyViolationTESpec() {
		BiFunction<MCError, MCError, Boolean> eval = (originalError, teError) -> {
			List<MCState> originalStates = originalError.getStates();
			List<MCState> teStates = teError.getStates();
			assertEquals(originalStates.size(), teStates.size());
			for (int i = 0; i < originalStates.size(); i++)
			{
				MCState originalState = originalStates.get(i);
				MCState teState = teStates.get(i);

				assertEquals(i + 1, originalState.getStateNumber());
				assertEquals(originalState.getStateNumber(), teState.getStateNumber());

				assertFalse(originalState.isBackToState());
				assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				
				assertFalse(originalState.isStuttering());
				assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				
				assertEquals(originalState.asSimpleRecord(), teState.asSimpleRecord());
			}
			
			return true;
		};

		assertTrue(teSpecTest("TESpecSafetyTest", eval));
	}
	
	@Test
	public void integrationTestStutteringTESpec() {
		BiFunction<MCError, MCError, Boolean> eval = (originalError, teError) -> {
			List<MCState> originalStates = originalError.getStates();
			List<MCState> teStates = teError.getStates();
			assertEquals(originalStates.size(), teStates.size());
			for (int i = 0; i < originalStates.size(); i++)
			{
				MCState originalState = originalStates.get(i);
				MCState teState = teStates.get(i);

				assertFalse(originalState.isBackToState());
				
				if (originalStates.size() == i + 1) {
					assertTrue(originalState.isStuttering());
					assertEquals(i, originalState.getStateNumber());
					assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				} else {
					assertEquals(i + 1, originalState.getStateNumber());
					assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				}
				
				assertEquals(originalState.asSimpleRecord(), teState.asSimpleRecord());
			}
			
			return true;
		};

		assertTrue(teSpecTest("TESpecStutteringTest", eval));
	}
	
	@Test
	public void integrationTestLassoTESpec() {
		BiFunction<MCError, MCError, Boolean> eval = (originalError, teError) -> {
			List<MCState> originalStates = originalError.getStates();
			List<MCState> teStates = teError.getStates();
			assertEquals(originalStates.size(), teStates.size());
			for (int i = 0; i < originalStates.size(); i++)
			{
				MCState originalState = originalStates.get(i);
				MCState teState = teStates.get(i);

				if (originalStates.size() == i + 1) {
					assertTrue(originalState.isBackToState());
					assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				} else {
					assertEquals(i + 1, originalState.getStateNumber());
					assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				}
				
				assertEquals(originalState.asSimpleRecord(), teState.asSimpleRecord());
			}
			
			return true;
		};
		
		assertTrue(teSpecTest("TESpecLassoTest", eval));
	}
	
	@Test
	public void integrationTestToolLassoTESpec() {
		BiFunction<MCError, MCError, Boolean> eval = (originalError, teError) -> {
			List<MCState> originalStates = originalError.getStates();
			List<MCState> teStates = teError.getStates();
			assertEquals(originalStates.size(), teStates.size());
			for (int i = 0; i < originalStates.size(); i++)
			{
				MCState originalState = originalStates.get(i);
				MCState teState = teStates.get(i);

				if (originalStates.size() == i + 1) {
					assertTrue(originalState.isBackToState());
					assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				} else {
					assertEquals(i + 1, originalState.getStateNumber());
					assertEquals(originalState.getStateNumber(), teState.getStateNumber());
				}
				
				assertEquals(originalState.asSimpleRecord(), teState.asSimpleRecord());
			}
			
			return true;
		};
		
		assertTrue(teSpecTest("TESpecLassoTest", eval, "-tool"));
	}
	
	public static boolean teSpecTest(String cfgName, BiFunction<MCError, MCError, Boolean> eval, String... otherArgs) {
		final Path modelDir = Paths.get("test-model", "TESpecTest");
		final Path tempDir = modelDir.resolve("temp");
		final Path ogStateDir = tempDir.resolve(UUID.randomUUID().toString());
		final Path teDir = tempDir.resolve(UUID.randomUUID().toString());
		final Path ogTlaPath = modelDir.resolve("TESpecTest" + TLAConstants.Files.TLA_EXTENSION);
		final Path ogCfgPath = modelDir.resolve(cfgName + TLAConstants.Files.CONFIG_EXTENSION);

		final ErrorTraceMessagePrinterRecorder originalRecorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(originalRecorder);
		final TLC ogTlc = new TLC();
		ogTlc.setResolver(new SimpleFilenameToStream(modelDir.toString()));
		
		String[] baseArgs = new String[] {
			"-traceExpressionSpecOutDir", teDir.toString(),
			"-metadir", ogStateDir.toString(),
			"-config", ogCfgPath.toString(),
		};
		
		String[] args = new String[baseArgs.length + otherArgs.length + 1];
		for (int i = 0; i < baseArgs.length; i++) { args[i] = baseArgs[i]; }
		for (int i = 0; i < otherArgs.length; i++) { args[baseArgs.length + i] = otherArgs[i]; }
		args[args.length - 1] = ogTlaPath.toString();

		assertTrue(ogTlc.handleParameters(args));
		ogTlc.process();
		MP.unsubscribeRecorder(originalRecorder);

		assertTrue(originalRecorder.getMCErrorTrace().isPresent());
		final MCError originalError = originalRecorder.getMCErrorTrace().get();

		final Path teStateDir = tempDir.resolve(UUID.randomUUID().toString());
		final Path teTlaPath = teDir.resolve(TLAConstants.TraceExplore.TRACE_EXPRESSION_MODULE_NAME + TLAConstants.Files.TLA_EXTENSION);
		
		ErrorTraceMessagePrinterRecorder teRecorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(teRecorder);
		final TLC teTlc = new TLC();
		teTlc.setResolver(new SimpleFilenameToStream(new String[] { modelDir.toString(), teDir.toString() }));
		assertTrue(teTlc.handleParameters(new String[] {
				"-metadir", teStateDir.toString(),
				teTlaPath.toString() }));
		teTlc.process();
		MP.unsubscribeRecorder(teRecorder);

		assertTrue(teRecorder.getMCErrorTrace().isPresent());
		final MCError teError = teRecorder.getMCErrorTrace().get();
		
		return eval.apply(originalError, teError);
	}

	@Test
	public void testFileWriteExceptions() {
		final String outputDir = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		ErrorTraceMessagePrinterRecorder recorder = new FakeErrorRecorder(null);

		FakeStreamProvider stream = new FakeStreamProvider(outputDir, null, null, ThrowException.TLA_FILENOTFOUND);
		TraceExpressionSpec teSpec = new TraceExpressionSpec(stream, recorder);
		assertFalse(teSpec.generate(null, null, null, null));

		stream = new FakeStreamProvider(outputDir, null, null, ThrowException.TLA_SECURITY);
		teSpec = new TraceExpressionSpec(stream, recorder);
		assertFalse(teSpec.generate(null, null, null, null));

		stream = new FakeStreamProvider(outputDir, null, null, ThrowException.CFG_FILENOTFOUND);
		teSpec = new TraceExpressionSpec(stream, recorder);
		assertFalse(teSpec.generate(null, null, null, null));

		stream = new FakeStreamProvider(outputDir, null, null, ThrowException.CFG_SECURITY);
		teSpec = new TraceExpressionSpec(stream, recorder);
		assertFalse(teSpec.generate(null, null, null, null));
	}
	
	private class FakeStreamProvider implements TraceExpressionSpec.IStreamProvider {
		
		private String outputDirectory;
		
		private ByteArrayOutputStream tlaStream;
		
		private ByteArrayOutputStream cfgStream;
		
		private ThrowException ex;
		
		public FakeStreamProvider(
				String outputDirectory,
				ByteArrayOutputStream tlaStream,
				ByteArrayOutputStream cfgStream) {
			this.outputDirectory = outputDirectory;
			this.tlaStream = tlaStream;
			this.cfgStream = cfgStream;
			this.ex = ThrowException.NONE;
		}
		
		public FakeStreamProvider(
				String outputDirectory,
				ByteArrayOutputStream tlaStream,
				ByteArrayOutputStream cfgStream,
				ThrowException ex) {
			this.outputDirectory = outputDirectory;
			this.tlaStream = tlaStream;
			this.cfgStream = cfgStream;
			this.ex = ex;
		}

		@Override
		public String getOutputDirectory() {
			return this.outputDirectory;
		}

		@Override
		public void setOutputDirectory(String outputDirectory) {
			this.outputDirectory = outputDirectory;
		}

		@Override
		public OutputStream getTlaStream() throws FileNotFoundException, SecurityException {
			switch (this.ex) {
				case TLA_SECURITY:
					throw new SecurityException("TLA_SECURITY");
				case TLA_FILENOTFOUND:
					throw new FileNotFoundException("TLA_FILENOTFOUND");
				default:
					return this.tlaStream;
			}
		}

		@Override
		public OutputStream getCfgStream() throws FileNotFoundException, SecurityException {
			switch (this.ex) {
				case CFG_SECURITY:
					throw new SecurityException("CFG_SECURITY");
				case CFG_FILENOTFOUND:
					throw new FileNotFoundException("CFG_FILENOTFOUND");
				default:
					return this.cfgStream;
			}
		}
	}
	
	private enum ThrowException {
		NONE,
		TLA_SECURITY,
		TLA_FILENOTFOUND,
		CFG_SECURITY,
		CFG_FILENOTFOUND
	}
	
	private class FakeErrorRecorder extends ErrorTraceMessagePrinterRecorder {
		
		private Optional<MCError> trace;
		
		public FakeErrorRecorder(MCError trace)
		{
			this.trace = Optional.ofNullable(trace);
		}
		
		@Override
		public Optional<MCError> getMCErrorTrace() {
			return this.trace;
		}
	}
}
