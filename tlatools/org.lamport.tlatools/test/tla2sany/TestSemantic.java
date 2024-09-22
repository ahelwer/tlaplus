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
import tla2sany.semantic.Errors;
import tla2sany.semantic.Generator;
import tla2sany.semantic.ModuleNode;
import tla2sany.semantic.SemanticNode;
import tla2sany.st.TreeNode;

public class TestSemantic {
	
	private static TreeNode checkSyntax(String input) throws AbortException {
		Configuration.load(null);
		BuiltInLevel.load();
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(inputBytes);
		TLAplusParser parser = new TLAplusParser(inputStream, StandardCharsets.UTF_8.name());
		Assert.assertTrue(input, parser.parse());
		return parser.rootNode();
	}
	
	private static ModuleNode checkSemantic(TreeNode parseTree) throws AbortException {
		Errors errors = new Errors();
		Generator gen = new Generator(null, errors);
		SemanticNode.setError(errors);
		ModuleNode semanticTree = gen.generate(parseTree);
		Assert.assertTrue(errors.toString(), errors.isSuccess());
		Assert.assertNotNull(errors.toString(), semanticTree);
		return semanticTree;
	}
	
	private static void checkLevel(ModuleNode semanticTree) {
		boolean result = semanticTree.levelCheck(1);
		Assert.assertTrue(result);
	}
	
	@Test
	public void generate() throws AbortException {
		String input = "---- MODULE Test ----\nx == ([]5) = 2\n====";
		TreeNode parseTree = checkSyntax(input);
		ModuleNode semanticTree = checkSemantic(parseTree);
		checkLevel(semanticTree);
	}
}