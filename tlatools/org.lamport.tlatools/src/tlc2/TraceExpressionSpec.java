package tlc2;

import java.io.File;
import java.io.IOException;

import tlc2.input.MCParser;
import tlc2.input.MCParserResults;
import tlc2.output.MP;
import tlc2.output.ErrorTraceMessagePrinterRecorder;
import tlc2.tool.ITool;
import tlc2.tool.TLCState;
import tlc2.tool.impl.ModelConfig;
import tlc2.tool.impl.SpecProcessor;

/**
 * Logic for generating a trace expression (TE) spec.
 */
public class TraceExpressionSpec {
	
	/**
	 * Path to the directory to which to output the TE spec.
	 */
	private String outputDirectory;
	
	/**
	 * Records TLC output as it runs, capturing the error trace if one is found.
	 */
	private ErrorTraceMessagePrinterRecorder recorder;
	
	/**
	 * Initializes a new instance of the {@link TraceExpressionSpec} class.
	 * @param outputDirectory Directory to which to output the TE spec.
	 */
	public TraceExpressionSpec(String outputDirectory)
	{
		this.outputDirectory = outputDirectory;
		this.recorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(this.recorder);
	}
	
	/**
	 * Initializes a new instance of the {@link TraceExpressionSpec} class.
	 * @param outputDirectory Directory to which to output the TE spec.
	 * @param recorder Recorder to record TLC as it runs; assumed to already be subscribed.
	 */
	public TraceExpressionSpec(
			String outputDirectory,
			ErrorTraceMessagePrinterRecorder recorder)
	{
		this.outputDirectory = outputDirectory;
		this.recorder = recorder;
	}
	
	/**
	 * Gets the TE spec's output directory.
	 * @return Path to the directory to which to output the TE spec.
	 */
	public String getOutputDirectory()
	{
		return this.outputDirectory;
	}
	
	/**
	 * Sets the TE spec's output directory.
	 */
	public void setOutputDirectory(String outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}
	
	public void generate(ITool specInfo)
	{
		this.recorder.getErrorTrace().ifPresent(errorTrace -> {
			ModelConfig cfg = specInfo.getModelConfig();
			SpecProcessor spec = specInfo.getSpecProcessor();
			String originalModuleName = specInfo.getRootName();
			String[] originalSpecVariables = TLCState.Empty.getVarsAsStrings();
			MCParserResults mcParserResults = MCParser.generateResultsFromProcessorAndConfig(spec, cfg);
			try {
				File[] files = TraceExplorer.writeSpecTEFiles(
						this.resolveOutputDirectory(),
						originalModuleName,
						originalSpecVariables,
						mcParserResults,
						errorTrace);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	
	public void appendTlcOutput(String output)
	{
		
	}
	
	private File resolveOutputDirectory()
	{
		return new File(this.outputDirectory);
	}
}
