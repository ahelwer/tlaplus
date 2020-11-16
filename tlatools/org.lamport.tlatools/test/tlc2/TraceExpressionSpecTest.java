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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import util.TLAConstants;
import util.FileUtil;
import util.SimpleFilenameToStream;
import tlc2.model.MCError;
import tlc2.model.MCState;
import tlc2.model.Utils;
import tlc2.output.ErrorTraceMessagePrinterRecorder;
import tlc2.output.MP;
import tlc2.tool.impl.Tool;

public class TraceExpressionSpecTest {
	
	/*
	@Rule
	TemporaryFolder tempDir = new TemporaryFolder();
	*/
	
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
	public void testCreateSimpleTraceExpressionSpec() {
		final String outputDir = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		final String originalSpecName = "Original";
		final String[] constants = new String[] {
			"CONSTANT FirstConstant <- FirstConstantDef",
			"CONSTANT SecondConstant <- SecondConstantDef"
		};
		
		final String[] variables = new String[] { "x", "y" };

		final MCError error = new MCError();
		final MCState[] states = new MCState[] {
				Utils.buildState(1, "init", "", "x = 1", "y = TRUE"),
				Utils.buildState(2, "next", "", "x = 2", "y = FALSE"),
				Utils.buildState(3, "next", "", "x = 3", "y = TRUE"),
				Utils.buildState(5, "next", "", "x = 4", "y = FALSE"),
				Utils.buildState(6, "next", "", "x = 5", "y = TRUE")
		};
		
		for (MCState state : states)
		{
			error.addState(state);
		}

		final ErrorTraceMessagePrinterRecorder recorder = new FakeErrorRecorder(error);
		
		try (
				ByteArrayOutputStream tlaStream = new ByteArrayOutputStream();
				ByteArrayOutputStream cfgStream = new ByteArrayOutputStream();) {
				
			FakeStreamProvider streams = new FakeStreamProvider(outputDir, tlaStream, cfgStream);
			TraceExpressionSpec teSpec = new TraceExpressionSpec(streams, recorder);

			teSpec.generate(
					originalSpecName,
					Arrays.asList(constants),
					Arrays.asList(variables),
					error);
			System.out.println(tlaStream.toString());
			System.out.println(cfgStream.toString());
		} catch (IOException e) {
			fail(e.getMessage());
		}
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
	
	public static boolean teSpecTest(String cfgName, BiFunction<MCError, MCError, Boolean> eval) {
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
		assertTrue(ogTlc.handleParameters(new String[] {
				"-traceExpressionSpecOutDir", teDir.toString(),
				"-metadir", ogStateDir.toString(),
				"-config", ogCfgPath.toString(),
				ogTlaPath.toString() }));
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
				"-noGenerateTraceExpressionSpec",
				"-metadir", teStateDir.toString(),
				teTlaPath.toString() }));
		teTlc.process();
		MP.unsubscribeRecorder(teRecorder);

		assertTrue(teRecorder.getMCErrorTrace().isPresent());
		final MCError teError = teRecorder.getMCErrorTrace().get();
		
		assertTrue(eval.apply(originalError, teError));
		
		return true;
	}

	@Test
	public void testFileWriteExceptions() {
		final String outputDir = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		ErrorTraceMessagePrinterRecorder recorder = new FakeErrorRecorder(null);

		FakeStreamProvider stream = new FakeStreamProvider(outputDir, null, null, true, false, false, false);
		TraceExpressionSpec teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null, null, null, null);

		stream = new FakeStreamProvider(outputDir, null, null, true, false, false, false);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null, null, null, null);

		stream = new FakeStreamProvider(outputDir, null, null, false, true, false, false);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null, null, null, null);

		stream = new FakeStreamProvider(outputDir, null, null, false, false, true, false);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null, null, null, null);

		stream = new FakeStreamProvider(outputDir, null, null, false, false, false, true);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null, null, null, null);
	}
	
	private class FakeStreamProvider implements TraceExpressionSpec.IStreamProvider {
		
		private String outputDirectory;
		
		private ByteArrayOutputStream tlaStream;
		
		private ByteArrayOutputStream cfgStream;
		
		private boolean throwTlaIoException = false;
		
		private boolean throwTlaSecurityException = false;
		
		private boolean throwCfgIoException = false;
		
		private boolean throwCfgSecurityException = false;
		
		public FakeStreamProvider(
				String outputDirectory,
				ByteArrayOutputStream tlaStream,
				ByteArrayOutputStream cfgStream) {
			this.outputDirectory = outputDirectory;
			this.tlaStream = tlaStream;
			this.cfgStream = cfgStream;
		}
		
		public FakeStreamProvider(
				String outputDirectory,
				ByteArrayOutputStream tlaStream,
				ByteArrayOutputStream cfgStream,
				boolean throwTlaIoException,
				boolean throwTlaSecurityException,
				boolean throwCfgIoException,
				boolean throwCfgSecurityException) {
			this.outputDirectory = outputDirectory;
			this.tlaStream = tlaStream;
			this.cfgStream = cfgStream;
			this.throwTlaIoException = throwTlaIoException;
			this.throwTlaSecurityException = throwTlaSecurityException;
			this.throwCfgIoException = throwCfgIoException;
			this.throwCfgSecurityException = throwCfgSecurityException;
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
			if (this.throwTlaIoException) {
				throw new FileNotFoundException();
			} else if (this.throwTlaSecurityException) {
				throw new SecurityException();
			} else {
				return this.tlaStream;
			}
		}

		@Override
		public OutputStream getCfgStream() throws FileNotFoundException, SecurityException {
			if (this.throwCfgIoException) {
				throw new FileNotFoundException();
			} else if (this.throwCfgSecurityException) {
				throw new SecurityException();
			} else {
				return this.cfgStream;
			}
		}
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
