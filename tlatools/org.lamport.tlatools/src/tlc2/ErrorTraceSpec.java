package tlc2;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

import tlc2.input.MCOutputPipeConsumer;
import tlc2.input.MCParser;
import tlc2.input.MCParserResults;
import tlc2.output.EC;
import tlc2.output.IMessagePrinterRecorder;
import tlc2.output.MP;
import tlc2.output.ErrorTraceMessagePrinterRecorder;
import tlc2.output.TeeOutputStream;
import tlc2.tool.ITool;
import tlc2.tool.TLCState;
import tlc2.tool.impl.ModelConfig;
import tlc2.tool.impl.SpecProcessor;
import util.FileUtil;
import util.TLAConstants;
import util.ToolIO;

public class ErrorTraceSpec {
	public static void initialize(
			Phaser waitingOnGenerationCompletion,
			ITool tool,
			Consumer<String> printErrorMsg)
	{
		ErrorTraceMessagePrinterRecorder recorder = new ErrorTraceMessagePrinterRecorder();
		MP.setRecorder(recorder);
		
		// This reads the output (ToolIO.out) on stdout of all other TLC threads. The
		// output is parsed to reconstruct the error trace, from which the code below
		// generates the SpecTE file. It might seem as if it would have been easier to
		// reuse the MPRecorder to collect the output that's written to ToolIO, but this
		// would work with two TLC processes where the first runs model-checking and
		// pipes its output to the second.
		try {
			final ByteArrayOutputStream temporaryMCOutputStream = new ByteArrayOutputStream();
			final BufferedOutputStream bos = new BufferedOutputStream(temporaryMCOutputStream);
			final PipedInputStream pis = new PipedInputStream();
			final TeeOutputStream tos1 = new TeeOutputStream(bos, new PipedOutputStream(pis));
			final TeeOutputStream tos2 = new TeeOutputStream(ToolIO.out, tos1);
			ToolIO.out = new PrintStream(tos2);
			final MCOutputPipeConsumer mcOutputConsumer = new MCOutputPipeConsumer(pis, null);
			
			// Note, this runnable's thread will not finish consuming output until just
			// 	before the app exits and we will use the output consumer in the TLC main
			//	thread while it is still consuming (but at a point where the model checking
			//	itself has finished and so the consumer is as populated as we need it to be
			//	- but prior to the output consumer encountering the EC.TLC_FINISHED message.)
			final Runnable r = () ->
				ErrorTraceSpec.generate(
						bos,
						temporaryMCOutputStream,
						mcOutputConsumer,
						waitingOnGenerationCompletion,
						tool,
						recorder);

			new Thread(r).start();

		} catch (final IOException ioe) {
			printErrorMsg.accept("Failed to set up piped output consumers; no potential "
								+ TLAConstants.TraceExplore.TRACE_EXPRESSION_MODULE_NAME + " will be generated: "
								+ ioe.getMessage());
		}
	}
	
	private static void generate(
			BufferedOutputStream bos,
			ByteArrayOutputStream temporaryMCOutputStream,
			MCOutputPipeConsumer mcOutputConsumer,
			Phaser waitingOnGenerationCompletion,
			ITool tool,
			ErrorTraceMessagePrinterRecorder recorder)
	{
		boolean haveClosedOutputStream = false;
		try {
			waitingOnGenerationCompletion.register();
			mcOutputConsumer.consumeOutput();
			
			bos.flush();
			temporaryMCOutputStream.close();
			haveClosedOutputStream = true;
			
			if ((mcOutputConsumer != null) && (mcOutputConsumer.getError() != null)) {
				// We need not synchronize the access to tool (which might appear racy), because
				// the consumeOutput above will block until TLC's finish message, which is written
				// *after* tool has been created.
				final SpecProcessor sp = tool.getSpecProcessor();
				final ModelConfig mc = tool.getModelConfig();
				final String originalSpecName = mcOutputConsumer.getSpecName();

				// Use provided output directory if present; if path is not valid, fall back to
				// source directory.
						/*
				Path outputDirectory = 
				final File outputDirectory =
						this.errorTraceSpecOutputDirectory
							.map(path -> new File(path))
							.filter(file -> file.exists() && file.isDirectory())
							.orElse(mcOutputConsumer.getSourceDirectory());
							*/
				final File outputDirectory = mcOutputConsumer.getSourceDirectory();
				
				final MCParserResults mcParserResults = MCParser.generateResultsFromProcessorAndConfig(sp, mc);

				// Write the files SpecTE.tla and SpecTE.cfg
				// At this point SpecTE.cfg contains the content of MC.cfg.
				// SpecTE.tla contains the newly generated SpecTE and the content of MC.tla.
				// See https://github.com/tlaplus/tlaplus/issues/475 for why copying MC.tla/MC.cfg is wrong.
				final File[] files = TraceExplorer.writeSpecTEFiles(
						outputDirectory,
						originalSpecName,
						TLCState.Empty.getVarsAsStrings(),
						mcParserResults,
						mcOutputConsumer.getError());
				
				// *Append* TLC's stdout/stderr output to final SpecTE.tla. The content of SpecTE.tla
				// is now MonolithMC, MonolithSpecTE, stdout/stderr. Most users won't care for
				// stderr/stdout and want to look at SpecTE. Thus, SpecTE is at the top.
				try (final FileOutputStream fos = new FileOutputStream(files[0], true))
				{
					FileUtil.copyStream(new ByteArrayInputStream(temporaryMCOutputStream.toByteArray()), fos);
				}
			}
			
		} catch (final Exception e) {
			MP.printMessage(EC.GENERAL,
							"A model checking error occurred while parsing tool output; the execution "
									+ "ended before the potential "
									+ TLAConstants.TraceExplore.TRACE_EXPRESSION_MODULE_NAME
									+ " generation stage.");
		} finally {
			if (!haveClosedOutputStream) {
				try {
					bos.flush();
					temporaryMCOutputStream.close();
				} catch (final Exception e) { }
			}
			// Signal the main method to continue to termination.
				waitingOnGenerationCompletion.arriveAndDeregister();
		}
	}
}
