package tlc2.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import java.util.List;

public class MessagePrinterRecorderTest {
	
	@Test
	public void TestGetRecord()
	{
		final int code = 0;
		final String expected = "This is a message";
		final MessagePrinterRecorder recorder = new MessagePrinterRecorder();
		recorder.record(code, expected);
		List<Object[]> actualListOfArrays = recorder.getRecords(code);
		assertEquals(1, actualListOfArrays.size());
		Object[] actualArray = actualListOfArrays.get(0);
		assertEquals(1, actualArray.length);
		assertTrue(actualArray[0] instanceof String);
		String actual = (String)actualArray[0];
		assertEquals(expected, actual);
	}
	
	@Test
	public void TestGetRecordAsInt()
	{
		final int code = 0;
		final Integer expected = 128;
		final MessagePrinterRecorder recorder = new MessagePrinterRecorder();
		recorder.record(code, expected.toString());
		Integer actual = recorder.getRecordAsInt(code);
		assertEquals(expected, actual);
	}
}
