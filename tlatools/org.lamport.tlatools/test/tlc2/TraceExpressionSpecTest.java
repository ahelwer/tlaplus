package tlc2;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Optional;

import org.junit.Test;

import util.TLAConstants;
import tlc2.model.MCError;
import tlc2.output.ErrorTraceMessagePrinterRecorder;

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
	public void testCreateSimpleTraceExpressionSpec() {
		final String outputDir = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		ErrorTraceMessagePrinterRecorder recorder = new FakeErrorRecorder(null);
		FakeStreamProvider streams = new FakeStreamProvider(outputDir, null, null);
		TraceExpressionSpec teSpec = new TraceExpressionSpec(streams, recorder);
		teSpec.generate(null);
	}
	
	@Test
	public void testFileWriteExceptions() {
		final String outputDir = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		ErrorTraceMessagePrinterRecorder recorder = new FakeErrorRecorder(null);

		FakeStreamProvider stream = new FakeStreamProvider(outputDir, null, null, true, false, false, false);
		TraceExpressionSpec teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null);

		stream = new FakeStreamProvider(outputDir, null, null, true, false, false, false);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null);

		stream = new FakeStreamProvider(outputDir, null, null, false, true, false, false);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null);

		stream = new FakeStreamProvider(outputDir, null, null, false, false, true, false);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null);

		stream = new FakeStreamProvider(outputDir, null, null, false, false, false, true);
		teSpec = new TraceExpressionSpec(stream, recorder);
		teSpec.generate(null);
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
