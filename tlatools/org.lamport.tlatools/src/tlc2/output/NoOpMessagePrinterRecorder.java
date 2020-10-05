package tlc2.output;

/**
 * Does nothing with the messages that pass through {@link tlc2.output.MP}.
 */
public class NoOpMessagePrinterRecorder implements IMessagePrinterRecorder {

	@Override
	public void record(int code, Object... objects) {
		// no-op
	}
}
