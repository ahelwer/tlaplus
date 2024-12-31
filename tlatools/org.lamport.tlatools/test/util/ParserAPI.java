package util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tla2sany.parser.SyntaxTreeNode;
import tla2sany.parser.TLAplusParser;
import tla2sany.semantic.AbortException;
import tla2sany.semantic.Context;
import tla2sany.semantic.Errors;
import tla2sany.semantic.Generator;
import tla2sany.semantic.ModuleNode;
import tla2sany.semantic.SemanticNode;

/**
 * A simple interface for parsing self-contained TLA+ modules, for use in
 * tests. This class can be viewed as a prototype of a future API to be
 * integrated into SANY itself.
 */
public class ParserAPI {

  /**
   * Fully parses and checks the given source code input.
   *
   * @param The TLA+ source to parse, as a string.
   * @return A level-checked semantic parse tree, or null if failure.
   */
  public static ModuleNode parse(String input) {
    SyntaxTreeNode syntaxRoot = processSyntax(input);
    if (null == syntaxRoot) {
      return null;
    }
    Errors log = new Errors();
    ModuleNode semanticRoot = processSemantics(syntaxRoot, log);
    if (log.isFailure() || null == semanticRoot) {
      ToolIO.err.println(log.toString());
      return null;
    }
    boolean levelOk = checkLevel(semanticRoot, log);
    if (!levelOk || log.isFailure()) {
      ToolIO.err.println(log.toString());
      return null;
    }
    return semanticRoot;
  }

  /**
   * Checks the syntax of the given input and builds a parse tree from it.
   * Parse errors are output to {@link ToolIO#out}. Future work might switch
   * this to the {@link Errors} class for uniformity & improved testability.
   *
   * @param input The input to parse, as a string.
   * @return A parse tree, or null if syntax error.
   */
  public static SyntaxTreeNode processSyntax(String input) {
    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
    InputStream inputStream = new ByteArrayInputStream(inputBytes);
    return processSyntax(inputStream);
  }

  /**
   * Checks the syntax of the given input and builds a parse tree from it.
   *
   * @param input The input to parse, as a stream.
   * @return A syntax parse tree, or null if parsing error.
   */
  public static SyntaxTreeNode processSyntax(InputStream input) {
    TLAplusParser parser = new TLAplusParser(input, StandardCharsets.UTF_8.name());
    return parser.parse() ? parser.ParseTree : null;
  }

  /**
   * Performs semantic checking & reference resolution on a parse tree.
   *
   * @param parseTree The parse tree to check.
   * @param log A log to record warnings & errors.
   * @return A semantic parse tree, or null if semantic error.
   */
  public static ModuleNode processSemantics(SyntaxTreeNode parseTree, Errors log) {
    // These two lines are annoying incantations to set global static state
    // that will hopefully be made unnecessary in the future.
    Context.reInit();
    SemanticNode.setError(log);
    // The null parameter here is the {@link ExternalModuleTable}, which will
    // need to be resolved & populated if this API is to one day support TLA+
    // modules that EXTEND or INSTANCE other TLA+ modules.
    Generator gen = new Generator(null, log);
    try {
      ModuleNode semanticRoot = gen.generate(parseTree);
      return log.isSuccess() ? semanticRoot : null;
    } catch (AbortException e) {
      Assert.fail(e.toString() + log.toString());
      return null;
    }
  }

  /**
   * Performs level-checking on the semantic parse tree.
   *
   * @param semanticRoot The root of the semantic parse tree.
   * @param log A log to record warnings & errors.
   * @return Whether levels in parse tree are correct.
   */
  public static boolean checkLevel(ModuleNode semanticRoot, Errors log) {
    SemanticNode.setError(log);
    boolean levelOk = semanticRoot.levelCheck(log);
    return levelOk && log.isSuccess();
  }
}