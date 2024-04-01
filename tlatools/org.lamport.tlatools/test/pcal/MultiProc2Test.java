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

public class MultiProc2Test extends PCalModelCheckerTestCase {

	public MultiProc2Test() {
		super("MultiProc2", "pcal", new String[] {"-wf", "-termination"});
	}

	@Test
	public void testSpec() {
		assertTrue(recorder.recordedWithStringValue(EC.TLC_INIT_GENERATED1, "4"));
		assertTrue(recorder.recordedWithStringValues(EC.TLC_CHECKING_TEMPORAL_PROPS, "complete", "504"));
		assertTrue(recorder.recorded(EC.TLC_FINISHED));
		assertFalse(recorder.recorded(EC.GENERAL));
		assertTrue(recorder.recordedWithStringValues(EC.TLC_STATS, "1212", "504", "0"));
		assertTrue(recorder.recordedWithStringValue(EC.TLC_SEARCH_DEPTH, "14"));
		assertZeroUncovered();
	}
}
/*
C:\lamport\tla\pluscal>java -mx1000m -cp "c:/lamport/tla/newtools/tla2-inria-workspace/tla2-inria/tlatools/class" tlc2.TLC -cleanup MultiProc2.tla         
TLC2 Version 2.05 of 18 May 2012
Running in Model-Checking mode.
Parsing file MultiProc2.tla
Parsing file C:\lamport\tla\newtools\tla2-inria-workspace\tla2-inria\tlatools\class\tla2sany\StandardModules\Sequences.tla
Parsing file C:\lamport\tla\newtools\tla2-inria-workspace\tla2-inria\tlatools\class\tla2sany\StandardModules\Naturals.tla
Parsing file C:\lamport\tla\newtools\tla2-inria-workspace\tla2-inria\tlatools\class\tla2sany\StandardModules\TLC.tla
Semantic processing of module Naturals
Semantic processing of module Sequences
Semantic processing of module TLC
Semantic processing of module MultiProc2
Starting... (2012-08-10 17:38:19)
Implied-temporal checking--satisfiability problem has 1 branches.
Computing initial states...
Finished computing initial states: 4 distinct states generated.
13  TRUE
13  TRUE
14  TRUE
14  TRUE
14  TRUE
14  TRUE
15  TRUE
15  TRUE
Checking temporal properties for the complete state space...
Model checking completed. No error has been found.
  Estimates of the probability that TLC did not check all reachable states
  because two distinct states had the same fingerprint:
  calculated (optimistic):  val = 1.9E-14
  based on the actual fingerprints:  val = 3.7E-13
1212 states generated, 504 distinct states found, 0 states left on queue.
The depth of the complete state graph search is 14.
Finished. (2012-08-10 17:38:20)
*/