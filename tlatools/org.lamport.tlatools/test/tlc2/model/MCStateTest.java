package tlc2.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tla2sany.st.Location;
import util.TLAConstants;

public class MCStateTest {
	
	@Test
	public void testParseRoundTrips()
	{
		testParseRoundTrip(1, "Initial predicate", "", "x = 8", "y = 7");
		testParseRoundTrip(2, "YIncr", "line 8, col 10 to line 10, col 26 of module Bla", "x = 8", "y = 15");
	}
	
	private static void testParseRoundTrip(
			int ordinal,
			String name,
			String location,
			String ...assignments)
	{
		final MCState expected = buildState(ordinal, name, location, assignments);
		final List<String> inputLines = toTlcOutputFormat(expected);
		final String input = String.join(TLAConstants.CR, inputLines);
		final MCState actual = MCState.parseState(input);
		compareStates(expected, actual);
	}
	
	private static MCState buildState(
			final int ordinal,
			final String name,
			final String location,
			final String ...assignments)
	{
		MCVariable[] variables = new MCVariable[assignments.length];
		for (int i = 0; i < assignments.length; i++)
		{
			String assignment = assignments[i];
			String[] split = assignment.split("=");
			variables[i] = new MCVariable(split[0], split[1]);
		}
		
		String label = String.format("%s %s", name, location).trim();
		return new MCState(
				variables,
				name,
				String.format("<%s>", label),
				Location.parseLocation(location),
				false,
				false,
				ordinal);
	}
	
	private static List<String> toTlcOutputFormat(final MCState state)
	{
		List<String> inputLines = new ArrayList<String>();
		inputLines.add(String.format("%d: <%s>", state.getStateNumber(), state.getName()));
		for (MCVariable variable : state.getVariables())
		{
			inputLines.add(String.format("/\\ %s = %s", variable.getName(), variable.getValueAsString()));
		}

		return inputLines;
	}
	
	private static void compareStates(final MCState expected, final MCState actual)
	{
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.isStuttering(), actual.isStuttering());
		assertEquals(expected.isBackToState(), actual.isBackToState());
		assertEquals(expected.getStateNumber(), actual.getStateNumber());
		
		MCVariable[] expectedVars = expected.getVariables();
		MCVariable[] actualVars = actual.getVariables();
		assertEquals(expectedVars.length, actualVars.length);
		for (int i = 0; i < expectedVars.length; i++)
		{
			assertEquals(expectedVars[i].getName(), actualVars[i].getName());
			assertEquals(expectedVars[i].getValueAsString(), actualVars[i].getValueAsString());
		}
	}
}
