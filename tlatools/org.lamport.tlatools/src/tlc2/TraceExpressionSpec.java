package tlc2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import tlc2.input.MCParser;
import tlc2.input.MCParserResults;
import tlc2.output.MP;
import tlc2.output.ErrorTraceMessagePrinterRecorder;
import tlc2.tool.ITool;
import tlc2.tool.TLCState;
import tlc2.tool.impl.ModelConfig;
import tlc2.tool.impl.SpecProcessor;
import util.TLAConstants;

/**
 * Logic for generating a trace expression (TE) spec.
 */
public class TraceExpressionSpec {
	
	/**
	 * Resolves TE spec files & provides output streams to them.
	 */
	private IStreamProvider streamProvider;
	
	/**
	 * Records TLC output as it runs, capturing the error trace if one is found.
	 */
	private ErrorTraceMessagePrinterRecorder recorder;
	
	/**
	 * Initializes a new instance of the {@link TraceExpressionSpec} class.
	 * @param outputDirectory Directory to which to output the TE spec.
	 */
	public TraceExpressionSpec(String outputDirectory) {
		this.streamProvider = new FileStreamProvider(outputDirectory);
		this.recorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(this.recorder);
	}
	
	/**
	 * Initializes a new instance of the {@link TraceExpressionSpec} class.
	 * This constructor is usually used for dependency injection by tests.
	 * @param streamProvider Provides output streams to which to write TE files.
	 * @param recorder Recorder to record TLC as it runs; assumed to already be subscribed.
	 */
	public TraceExpressionSpec(
			IStreamProvider streamProvider,
			ErrorTraceMessagePrinterRecorder recorder) {
		this.streamProvider = streamProvider;
		this.recorder = recorder;
	}
	
	/**
	 * Gets the TE spec's output directory.
	 * @return Path to the directory to which to output the TE spec.
	 */
	public String getOutputDirectory() {
		return this.streamProvider.getOutputDirectory();
	}
	
	/**
	 * Sets the TE spec's output directory.
	 * @param outputDirectory Path to directory to which to output the TE spec.
	 */
	public void setOutputDirectory(String outputDirectory) {
		this.streamProvider.setOutputDirectory(outputDirectory);
	}
	
	/**
	 * Generates the TE spec and writes it to TLA and CFG files.
	 * @param specInfo Information about the original spec.
	 */
	public void generate(ITool specInfo) {
		ModelConfig cfg = specInfo.getModelConfig();
		SpecProcessor spec = specInfo.getSpecProcessor();
		String originalSpecName = specInfo.getRootName();
		List<String> variables = Arrays.asList(TLCState.Empty.getVarsAsStrings());
		MCParserResults parserResults = MCParser.generateResultsFromProcessorAndConfig(spec, cfg);
		List<String> constants = parserResults.getModelConfig().getRawConstants();
		this.generate(originalSpecName, constants, variables);
	}

	/**
	 * Generates the TE spec and writes it to TLA and CFG files.
	 * @param originalSpecName Name of the original spec.
	 * @param constants Constants from the original spec.
	 * @param variables Variables from the original spec.
	 */
	public void generate(String originalSpecName, List<String> constants, List<String> variables) {
		this.recorder.getMCErrorTrace().ifPresent(errorTrace -> {
			try (
					OutputStream tlaStream = this.streamProvider.getTlaStream();
					OutputStream cfgStream = this.streamProvider.getCfgStream();
			) {
				TraceExplorer.writeSpecTEStreams(
						originalSpecName,
						constants,
						variables,
						errorTrace,
						tlaStream,
						cfgStream);
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (SecurityException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		});
	}
	
	/**
	 * Interface for creating streams to which to write the TE spec.
	 */
	public interface IStreamProvider {
		
		/**
		 * Gets the TE spec's output directory.
		 * @return The TE spec's output directory.
		 */
		public String getOutputDirectory();

		/**
		 * Sets the TE spec's output directory.
		 * @param outputDirectory The TE spec's output directory.
		 */
		public void setOutputDirectory(String outputDirectory);

		/**
		 * Creates an output stream to which to write the TE spec.
		 * Caller is responsible for managing stream lifecycle.
		 * @return A new output stream to which to write the TE spec.
		 * @throws FileNotFoundException Thrown if filepath is inaccessible.
		 * @throws SecurityException Thrown if lacking perms to write file.
		 */
		public OutputStream getTlaStream() throws FileNotFoundException, SecurityException;
		
		/**
		 * Creates an output stream to which to write the TE spec's CFG file.
		 * Caller is responsible for managing stream lifecycle.
		 * @return A new output stream to which to write the CFG file.
		 * @throws FileNotFoundException Thrown if filepath is inaccessible.
		 * @throws SecurityException Thrown if lacking perms to write file.
		 */
		public OutputStream getCfgStream() throws FileNotFoundException, SecurityException;
	}
	
	/**
	 * Provides streams to actual files on disk.
	 */
	public class FileStreamProvider implements IStreamProvider {
		
		/**
		 * Directory to which to output the files.
		 */
		private String outputDirectory;
		
		public FileStreamProvider(String outputDirectory) {
			this.outputDirectory = outputDirectory;
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
			this.ensureDirectoryExists();
			final File tlaFile = new File(
					this.outputDirectory,
					TLAConstants.TraceExplore.TRACE_EXPRESSION_MODULE_NAME
					+ TLAConstants.Files.TLA_EXTENSION);
			return new FileOutputStream(tlaFile);
		}
		
		@Override
		public OutputStream getCfgStream() throws FileNotFoundException, SecurityException {
			this.ensureDirectoryExists();
			final File cfgFile = new File(
					this.outputDirectory,
					TLAConstants.TraceExplore.TRACE_EXPRESSION_MODULE_NAME
					+ TLAConstants.Files.CONFIG_EXTENSION);
			return new FileOutputStream(cfgFile);
		}
		
		/**
		 * Recursively creates directories until the desired path is present.
		 * @throws SecurityException Access issue when creating directories.
		 */
		private void ensureDirectoryExists() throws SecurityException {
			Path outputDirPath = Paths.get(this.outputDirectory);
			for (int i = 1; i <= outputDirPath.getNameCount(); i++) {
				Path subPath = outputDirPath.subpath(0, i);
				if (!subPath.toFile().exists()) {
					subPath.toFile().mkdir();
				}
			}
		}
	}
}
