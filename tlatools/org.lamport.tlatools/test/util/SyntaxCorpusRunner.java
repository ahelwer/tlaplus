package util;

import java.text.ParseException;
import java.util.List;

import util.SyntaxCorpusFileParser.CorpusTest;
import util.SyntaxCorpusFileParser.CorpusTestFile;

import org.junit.Assert;

/**
 * Functionality to run a parser against a corpus of syntax tests.
 */
public class SyntaxCorpusRunner {

  /**
   * A parser can implement this interface to be subjected to a corpus of
   * syntax tests.
   */
  public interface IParserTestTarget {

    /**
     * Performs parsing. Returns null in case of parse error. Returns
     * standardized AST if successful. Throws ParseException if process
     * of translating to standardized AST encounters an error.
     *
     * @param input The input to be parsed.
     * @return Null if parse error, AST if successful.
     * @throws ParseException If translation to standardized AST fails.
     */
    public AstNode parse(String input) throws ParseException;
  }

  /**
   * Runs a single test from the corpus in order to debug it.
   *
   * @param corpus A set of corpus test files, each containing some tests.
   * @param parser A parser target to subject to testing.
   * @param testName The name of the test to run.
   * @throws ParseException If translating parse output fails.
   */
  public static void debugSingleTest(List<CorpusTestFile> corpus, IParserTestTarget parser, String testName) throws ParseException {
    // This makes the parser print out messages on entry & exit to each rule.
    System.setProperty("TLA-StackTrace", "on");
    for (CorpusTestFile corpusTestFile : corpus) {
      for (CorpusTest corpusTest : corpusTestFile.tests) {
        if (corpusTest.name.equals(testName)) {
          String testSummary = String.format(
            "%s/%s\n%s",
            corpusTestFile.path,
            corpusTest.name,
            corpusTest.tlaplusInput);
          AstNode actual = parser.parse(corpusTest.tlaplusInput);
          if (corpusTest.attributes.contains(CorpusTest.Attribute.ERROR)) {
            System.out.println("Expecting failure.");
            Assert.assertNull(testSummary, actual);
          } else {
            Assert.assertNotNull(testSummary, actual);
            System.out.println(String.format("Expect: %s", corpusTest.expectedAst));
            System.out.println(String.format("Actual: %s", actual));
            corpusTest.expectedAst.testEquality(actual);
          }
        }
      }
    }
  }

  /**
   * Runs the given test corpus against the given parser target.
   *
   * @param corpus A set of corpus test files, each containing some tests.
   * @param parser A parser target to subject to testing.
   * @throws ParseException If translating parse output fails.
   */
  public static void run(List<CorpusTestFile> corpus, IParserTestTarget parser) throws ParseException {
    int testCount = 0;
    for (CorpusTestFile corpusTestFile : corpus) {
      System.out.println(corpusTestFile.path);
      for (CorpusTest corpusTest : corpusTestFile.tests) {
        System.out.println(corpusTest.name);

        if (corpusTest.attributes.contains(CorpusTest.Attribute.SKIP)) {
          System.out.println("Skipped.");
          continue;
        }

        testCount++;

        String testSummary = String.format(
            "%s/%s\n%s",
            corpusTestFile.path,
            corpusTest.name,
            corpusTest.tlaplusInput);
        AstNode actual = parser.parse(corpusTest.tlaplusInput);
        if (corpusTest.attributes.contains(CorpusTest.Attribute.ERROR)) {
          System.out.println("Expecting failure.");
          Assert.assertNull(testSummary, actual);
        } else {
          Assert.assertNotNull(testSummary, actual);
          System.out.println(String.format("Expect: %s", corpusTest.expectedAst));
          System.out.println(String.format("Actual: %s", actual));
          corpusTest.expectedAst.testEquality(actual);
        }
      }
    }

    Assert.assertTrue(testCount > 0);
    System.out.println(String.format("Total corpus test count: %d", testCount));
  }
}
