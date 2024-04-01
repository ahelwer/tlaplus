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
 ******************************************************************************/
package pcal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import tlc2.output.EC;

public class FischerTest extends PCalModelCheckerTestCase {

	public FischerTest() {
		super("Fischer", "pcal", new String[] {"-wf"});
	}

	@Test
	public void testSpec() {
		assertTrue(recorder.recordedWithStringValue(EC.TLC_INIT_GENERATED1, "1"));
		assertTrue(recorder.recordedWithStringValues(EC.TLC_CHECKING_TEMPORAL_PROPS, "complete", "1002"));
		assertTrue(recorder.recorded(EC.TLC_FINISHED));
		assertFalse(recorder.recorded(EC.GENERAL));
		assertTrue(recorder.recordedWithStringValues(EC.TLC_STATS, "2487", "1002", "0"));
		assertTrue(recorder.recordedWithStringValue(EC.TLC_SEARCH_DEPTH, "24"));

		assertZeroUncovered();
	}
}
/*
C:\lamport\tla\pluscal>java -mx1000m -cp "c:/lamport/tla/newtools/tla2-inria-workspace/tla2-inria/tlatools/class" tlc2.TLC -cleanup Fischer.tla         
TLC2 Version 2.05 of 18 May 2012
Running in Model-Checking mode.
Parsing file Fischer.tla
Parsing file C:\lamport\tla\newtools\tla2-inria-workspace\tla2-inria\tlatools\class\tla2sany\StandardModules\Naturals.tla
Parsing file C:\lamport\tla\newtools\tla2-inria-workspace\tla2-inria\tlatools\class\tla2sany\StandardModules\TLC.tla
Parsing file C:\lamport\tla\newtools\tla2-inria-workspace\tla2-inria\tlatools\class\tla2sany\StandardModules\Sequences.tla
Semantic processing of module Naturals
Semantic processing of module Sequences
Semantic processing of module TLC
Semantic processing of module Fischer
Starting... (2012-08-10 17:38:12)
Implied-temporal checking--satisfiability problem has 1 branches.
"Testing Fischer's Mutual Exclusion Algorithm"  TRUE
<<" Number of processes = ", 3>>  TRUE
<<" Delta   = ", 2>>  TRUE
<<" Epsilon = ", 3>>  TRUE
"Should find a bug if N > 1 and Delta >= Epsilon"  TRUE
Computing initial states...
Finished computing initial states: 1 distinct state generated.
Checking temporal properties for the complete state space...
Model checking completed. No error has been found.
  Estimates of the probability that TLC did not check all reachable states
  because two distinct states had the same fingerprint:
  calculated (optimistic):  val = 8.1E-14
  based on the actual fingerprints:  val = 8.0E-15
2487 states generated, 1002 distinct states found, 0 states left on queue.
The depth of the complete state graph search is 24.
Finished. (2012-08-10 17:38:13)
InnerLabeledIf.tla
*/