package tlc2;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import util.TLAConstants;
import tlc2.model.MCError;
import tlc2.output.ErrorTraceMessagePrinterRecorder;

public class TraceExpressionSpecTest {

	@Test
	public void testSetOutputDirectory()
	{
		String expected = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		TraceExpressionSpec teSpec = new TraceExpressionSpec(expected);
		assertEquals(expected, teSpec.getOutputDirectory());
		expected = "some-other-directory-name";
		teSpec.setOutputDirectory(expected);
		assertEquals(expected, teSpec.getOutputDirectory());
	}
	
	@Test
	public void testCreateSimpleTraceExpressionSpec()
	{
		MCError error = new MCError();
		final String outputDir = TLAConstants.Directories.TRACE_EXPRESSION_SPEC;
		ErrorTraceMessagePrinterRecorder recorder = new TestErrorTraceMessagePrinterRecorder(error);
		TraceExpressionSpec teSpec = new TraceExpressionSpec(outputDir, recorder);
	}

	public class TestErrorTraceMessagePrinterRecorder
		extends ErrorTraceMessagePrinterRecorder
	{
		private MCError error;
		
		public TestErrorTraceMessagePrinterRecorder(MCError error)
		{
			this.error = error;
		}
		
		@Override
		public Optional<MCError> getErrorTrace()
		{
			return Optional.of(this.error);
		}
	}
}
