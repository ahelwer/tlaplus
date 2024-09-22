package tla2sany;

import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.junit.Assert;

import tla2sany.configuration.Configuration;
import tla2sany.parser.TLAplusParser;
import tla2sany.semantic.AbortException;
import tla2sany.semantic.BuiltInLevel;
import tla2sany.semantic.Generator;
import tla2sany.semantic.ModuleNode;

public class TestSemantic {

	@Test
	public void generate() throws AbortException {
		Configuration.load(null);
		BuiltInLevel.load();
		String input = "---- MODULE Test ----\nx == 5\n====";
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		TLAplusParser parser = new TLAplusParser(inputStream, StandardCharsets.UTF_8.name());
		Assert.assertTrue(parser.parse());
		Generator gen = new Generator(null, null);
		ModuleNode semanticTree = gen.generate(parser.rootNode());
	}
}