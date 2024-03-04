package tla2sany;

import tla2sany.CorpusTestParser.CorpusTestFile;
import tla2sany.CorpusTestParser.CorpusTest;
import tla2sany.configuration.Configuration;
import tla2sany.parser.TLAplusParser;
import tla2sany.semantic.AbortException;
import tla2sany.semantic.BuiltInLevel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

public class ParseCorpusTest {
	
	private List<CorpusTestFile> corpus;
	
	@Before
	public void setup() throws IOException, ParseException, AbortException {
		this.corpus = CorpusTestParser.getAndParseCorpusTestFiles();
		Configuration.load(null);
		BuiltInLevel.load();
	}

	@Test
	public void test() throws ParseException {
		for (CorpusTestFile corpusTestFile : this.corpus) {
			System.out.println(corpusTestFile.path);
			for (CorpusTest corpusTest : corpusTestFile.tests) {
				if (!corpusTest.name.equals("INSTANCE With Jlist Substitutions")) {
					//continue;
				}
				System.out.println(corpusTest.name);
				String testSummary = String.format("\n%s\n%s\n%s", corpusTestFile.path, corpusTest.name, corpusTest.tlaplusInput);
				InputStream input = new ByteArrayInputStream(corpusTest.tlaplusInput.getBytes(StandardCharsets.UTF_8));
				TLAplusParser parser = new TLAplusParser(input, StandardCharsets.UTF_8.name());
				Assert.assertTrue(testSummary, parser.parse());
				AstNode actual = SanyTranslator.toAst(parser);
				corpusTest.expectedAst.testEquality(actual);
			}
		}
	}
}
