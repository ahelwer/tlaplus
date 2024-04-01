/*******************************************************************************
 * Copyright (c) 2018 Microsoft Research. All rights reserved.
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 *   Ian Morris Nieves - added support for fingerprint stack trace
 ******************************************************************************/
package tlc2.value.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.stream.DoubleStream;

import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import tlc2.util.FP64;

@RunWith(Parameterized.class)
public class SubsetEnumeratorTest {

	private static final Value[] getValue(String... strs) {
		final List<Value> values = new ArrayList<>(strs.length);
		for (int i = 0; i < strs.length; i++) {
			values.add(new StringValue(strs[i]));
		}
		return values.toArray(new Value[values.size()]);
	}

	@Parameters
	public static List<Enumerable> getEnumerable() {
		final List<Enumerable> params = new ArrayList<Enumerable>();
		
		// IntervalValue
		params.add(new IntervalValue(1, 10));
		
		// SetEnumValue
		final ValueVec vec = new ValueVec();
		final String input = "ABCDEFGHIJ";
		input.chars().forEach(new IntConsumer() {
			@Override
			public void accept(final int value) {
				vec.addElement(ModelValue.make(String.valueOf(value)));
			}
		});
		params.add(new SetEnumValue(vec, false));
		
		// SetOfTuplesValue
		params.add(new SetOfTuplesValue(new IntervalValue(1, 5), new IntervalValue(1, 5)));
		params.add(new SetOfTuplesValue(new SetEnumValue(), new SetEnumValue())); // empty
		
		// UnionValue
		params.add(new UnionValue(
				new SetEnumValue(new Value[] { new IntervalValue(1, 5), new IntervalValue(5, 11) }, true)));
		params.add(new UnionValue(new SetEnumValue())); // empty
		
		// SetOfFcnsValue
		params.add(new SetOfFcnsValue(new IntervalValue(2, 5),
				new SetEnumValue(new Value[] { new StringValue("a"), new StringValue("b"), new StringValue("c") }, true)));
		params.add(new SetOfFcnsValue(new IntervalValue(3, 5), new SetEnumValue())); // empty range

		// SetOfFcnsValue with SubsetValue as range.
		params.add(new SetOfFcnsValue(
				new SetEnumValue(new Value[] { ModelValue.make("m1"), ModelValue.make("m2"), ModelValue.make("m3") },
						true),
				new SubsetValue(new SetEnumValue(
						new Value[] { new StringValue("a"), new StringValue("b"), new StringValue("c") }, true))));

		// SetOfFcnsValue
		final SetEnumValue domain = new SetEnumValue(getValue("A1", "A2", "A3"), true);
		final SetEnumValue range = new SetEnumValue(getValue("v1", "v2", "v3"), true);
		params.add(new SetOfFcnsValue(domain, range));

		// SubsetValue
		params.add(new SubsetValue(new SetEnumValue(
				new Value[] { new StringValue("a"), new StringValue("b"), new StringValue("c") }, true)));
		params.add(new SubsetValue(new SetEnumValue())); // empty
		
		// Adding values to Set<Value> requires fingerprinting.
		FP64.Init();

		return params;
	}
	
	private final Enumerable enumerable;

	public SubsetEnumeratorTest(final Enumerable enumerable) {
		this.enumerable = enumerable;
	}

	@Test
	public void testElementsInt() {
		// for various fractions...
		DoubleStream.of(0, .1, .2, .3, .4, .55, .625, .775, .8, .9, 1).forEach(new DoubleConsumer() {
			@Override
			public void accept(double fraction) {
				final int k = (int) Math.ceil(enumerable.size() * fraction);
				final List<Value> values = enumerable.elements(k).all();
				
				// Expected size.
				Assertions.assertEquals(k, values.size(), String.format("Failed for fraction: %s", fraction));

				// Unique values.
				Assertions.assertEquals(
					values.size(),
					new HashSet<Value>(values).size(),
					String.format("Failed for fraction: %s", fraction));

				// Each value is actually a member of enumerable.
				for (Value v : values) {
					Assertions.assertTrue(enumerable.member(v), String.format("Failed for fraction: %s", fraction));
				}
			}
		});
	}
	
	@Test
	public void testGetRandomSubset() {
		DoubleStream.of(0, .1, .2, .3, .4, .55, .625, .775, .8, .9, 1).forEach(new DoubleConsumer() {
			@Override
			public void accept(final double fraction) {
				final int k = (int) Math.ceil(enumerable.size() * fraction);
				
				final Enumerable enumValue = enumerable.getRandomSubset(k);
				
				// Expected size.
				assertEquals(k, enumValue.size(), String.format("Failed for fraction: %s", fraction));

				final Set<Value> values = new HashSet<>(enumValue.size());
				
				// Each value is actually a member of enumerable.
				ValueEnumeration elements = enumValue.elements();
				Value v = null;
				while ((v = elements.nextElement()) != null) {
					Assertions.assertTrue(enumerable.member(v), String.format("Failed for fraction: %s", fraction));
					values.add(v);
				}

				// Unique values.
				Assertions.assertEquals(
					enumValue.size(),
					new HashSet<Value>(values).size(),
					String.format("Failed for fraction: %s", fraction));
				
			}
		});
	}
}
