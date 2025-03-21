// Copyright (c) 2016 Markus Alexander Kuppe. All rights reserved.
package tlc2.tool.fp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static tlc2.tool.fp.OffHeapDiskFPSet.EMPTY;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import tlc2.tool.fp.LongArrays.LongComparator;
import tlc2.tool.fp.OffHeapDiskFPSet.Indexer;

public class LongArraysTest {
	
	@Before
	public void setup() {
		Assume.assumeTrue(LongArray.isSupported());
	}
	
	@Test
	public void testEmpty1() {
		doTest(new ArrayList<Long>(0), 1L, 0, new OffHeapDiskFPSet.InfinitePrecisionIndexer(0, 1));
	}
	
	@Test
	public void testEmpty2() {
		final List<Long> expected = new ArrayList<Long>();
		expected.add(0L);
		expected.add(0L);
		expected.add(0L);
		expected.add(0L);

		doTest(expected, 1L, 2, new OffHeapDiskFPSet.InfinitePrecisionIndexer(expected.size(), 1));
	}
	
	@Test
	public void testBasic1() {
		final List<Long> expected = new ArrayList<Long>();
		expected.add(5L);
		expected.add(8L);
		expected.add(1L);
		expected.add(7L);
		expected.add(0L);
		expected.add(3L);
		final LongArray array = new LongArray(expected);
		LongArrays.sort(array);
		
		// This amounts to a regular/basic insertion sort because there are no
		// sentinels in the array. doTest fails for this array, because the
		// indices calculated by the indexer are invalid.
		for (int i = 1; i < array.size(); i++) {
			assertTrue(array.get(i - 1L) < array.get(i));
		}
	}

	@Test
	public void testBasic2() {
		final List<Long> expected = new ArrayList<Long>();
		expected.add(74236458333421747L);
		expected.add(9185197375878056627L);
		expected.add(9017810141411942826L);
		expected.add(481170446028802552L);
		expected.add(587723185270146839L);
		expected.add(764880467681476738L);
		expected.add(1028380228728529428L);
		expected.add(1246117495100367611L);
		expected.add(1353681884824400499L);
		expected.add(1963327988900916594L);
		expected.add(2157942654452711468L);
		expected.add(2211701751588391467L);
		expected.add(2197266581704230150L);
		expected.add(2391118405386569995L);
		expected.add(2754416910109403115L);
		expected.add(3528296600587602855L);
		expected.add(3766154305485605955L);
		expected.add(4172091881329434331L);
		expected.add(4273360576593753745L);
		expected.add(4338054185482857322L);
		expected.add(4487790251341705673L);
		expected.add(4760603841378765728L);
		expected.add(4897534821030901381L);
		expected.add(5057347369431494228L);
		expected.add(5185984701076703188L);
		expected.add(5255556356599253415L);
		expected.add(4911921657882287345L);
		expected.add(5512811886280168498L);
		expected.add(5627022814159167180L);
		expected.add(5630009759945037387L);
		expected.add(5592096823142754761L);
		expected.add(5880489878946290534L);
		expected.add(6796173646113527960L);
		expected.add(6887096685265647763L);
		expected.add(6946033094922439935L);
		expected.add(7100083311060830826L);
		expected.add(7575172208974668528L);
		expected.add(8240485391672917634L);
		expected.add(8572429495433200993L);
		expected.add(8804495173596718076L);
		expected.add(8771524479740786626L);
		expected.add(8986659781390119011L);
		expected.add(9136953010061430590L);
		expected.add(9195197379878056627L);		
		final LongArray array = new LongArray(expected);
		LongArrays.sort(array);
		
		// This amounts to a regular/basic insertion sort because there are no
		// sentinels in the array. doTest fails for this array, because the
		// indices calculated by the indexer are invalid.
		for (int i = 1; i < array.size(); i++) {
			assertTrue(array.get(i - 1L) < array.get(i));
		}
	}

	@Test
	public void test0() {
		final List<Long> expected = new ArrayList<Long>();
		expected.add(22102288204167208L);
		expected.add(225160948165161873L);
		expected.add(0L);
		expected.add(1638602644344629957L);
		expected.add(1644442600000000000L);
		expected.add(0L);

		doTest(expected, 1L, 3, new OffHeapDiskFPSet.InfinitePrecisionIndexer(expected.size(), 1));
	}
	
	private void doTest(final  List<Long>  expected, final long partitions, final int reprobe, final Indexer indexer) {
		final LongArray array = new LongArray(expected);
		final LongComparator comparator = getComparator(indexer);
		final long length = expected.size() / partitions;
		
		// Sort each disjunct partition.
		for (long i = 0; i < partitions; i++) {
			final long start = i * length;
			final long end = i + 1L == partitions ? array.size() - 1L: start + length;
			LongArrays.sort(array, start, end, comparator);
		}
		// Stitch the disjunct partitions together. Only need if more than one
		// partition, but done with one partition anyway to see that it causes
		// no harm.
		for (long i = 0; i < partitions; i++) {
			final long end = getEnd(partitions, array, length, i);
			LongArrays.sort(array, end - reprobe, end + reprobe, comparator);
		}
		
		verify(expected, reprobe, indexer, array);
	}

	private long getEnd(final long partitions, final LongArray array, final long length, long idx) {
		return idx + 1L == partitions ? array.size() - 1L: (idx + 1L) * length;
	}

	private static LongComparator getComparator(final Indexer indexer) {
		return new LongComparator() {
			public int compare(final long fpA, final long posA, final long fpB, final long posB) {
				// Elements not in Nat \ {0} remain at their current
				// position.
				if (fpA <= EMPTY || fpB <= EMPTY) {
					return 0;
				}
				
				final boolean wrappedA = indexer.getIdx(fpA) > posA;
				final boolean wrappedB = indexer.getIdx(fpB) > posB;
				
				if (wrappedA == wrappedB && posA > posB) {
					return fpA < fpB ? -1 : 1;
				} else if ((wrappedA ^ wrappedB)) {
					if (posA < posB && fpA < fpB) {
						// Swap fpB, which is at the end of array a, with fpA.
						// fpA is less than fpB. fpB was inserted into array a
						// before fpA.
						return -1;
					}
					if (posA > posB && fpA > fpB) {
						return -1;
					}
				}
				return 0;
			}
		};
	}

	private void verify(final List<Long> expected, final int reprobe, final Indexer indexer, final LongArray array) {
		// Verify that negative and EMPTY elements remain at their position.
		// Lets call them sentinels.
		int sentinel = 0;
		OUTER: for (int j = 0; j < expected.size(); j++) {
			final long l = expected.get(j);
			if (l == EMPTY) {
				// EMPTY remain at their original positions.
				assertEquals(EMPTY, array.get(j));
				sentinel++;
			} else if (l < EMPTY) {
				// Negative remain at their original positions.
				assertEquals(l, array.get(j));
				sentinel++;
			} else {
				// Verify that all non-sentinels are still
				// array members.
				for (int k = 0; k < array.size(); k++) {
					if (array.get(k) == l) {
						continue OUTER;
					}
				}
				fail(String.format("long %s not found.", l));
			}
		}
		
		// Verify elements stayed within their lookup range.
		for (int pos = 0; pos < array.size(); pos++) {
			final long l = array.get(pos);
			if (l <= EMPTY) {
				continue;
			}
			final long idx = indexer.getIdx(l);
			assertTrue(String.format("%s, pos: %s, idx: %s, r: %s (was at: %s)", l, pos, idx, reprobe,
					expected.indexOf(l)), isInRange(idx, reprobe, pos, array.size()));
		}
		
		// Verify that non-sentinels are sorted is ascending order. Take
		// care of wrapped elements too. A) First find the first non-sentinel,
		// non-wrapped element.
		long pos = 0;
		final List<Long> seen = new ArrayList<Long>(expected.size());
		while (pos < array.size()) {
			long e = array.get(pos);
			if (e <= EMPTY || indexer.getIdx(e) > pos) {
				// Either sentinel or wrapped.
				pos++;
				continue;
			}
			seen.add(e);
			pos++;
			break;
		}
		// B) Collect all elements into seen but skip those at the beginning that
		// wrapped, and those that didn't wrap at the end (array.size + reprobe).
		for (; pos < array.size() + reprobe; pos++) {
			long actual = array.get(pos % array.size());
			if (actual <= EMPTY) {
				continue;
			}
			final long idx = indexer.getIdx(actual);
			if (pos < array.size() && idx > pos) {
				// When not wrapped, ignore elements belonging to the end that wrapped.
				continue;
			}
			if (pos > array.size() - 1L && idx + reprobe < pos) {
				// When wrapped, ignore elements at beginning which do not
				// belong to the end.
				continue;
			}
			seen.add(actual);
		}
		// C) Verify that all elements are sorted.
		for (int i = 1; i < seen.size(); i++) {
			final long lo = seen.get(i - 1);
			final long hi = seen.get(i);
			assertTrue(String.format("%s > %s", lo, hi), lo < hi);
		}
		// D) Verify we saw all expected elements.
		assertEquals(expected.size() - sentinel, seen.size());
	}
	
	@Test
	public void testIsInRange() {
		assertTrue(isInRange(0, 0, 0, 4));

		assertFalse(isInRange(0, 0, 1, 4));
		assertFalse(isInRange(0, 0, 2, 4));
		assertFalse(isInRange(0, 0, 3, 4));
		assertFalse(isInRange(0, 0, 4, 4));

		assertTrue(isInRange(0, 1, 1, 4));
		assertFalse(isInRange(0, 1, 2, 4));
		assertTrue(isInRange(0, 2, 2, 4));
		assertFalse(isInRange(0, 2, 3, 4));
		assertTrue(isInRange(0, 3, 3, 4));
		assertFalse(isInRange(0, 3, 4, 4));
		
		assertTrue(isInRange(3, 0, 3, 4));
		assertTrue(isInRange(3, 1, 0, 4));
		assertTrue(isInRange(3, 2, 1, 4));
		assertFalse(isInRange(3, 2, 2, 4));
	}

	private static boolean isInRange(long idx, int reprobe, int pos, long size) {
		if (idx + reprobe >= size && pos < idx) {
			return pos <= (idx + reprobe) % size;
		} else {
			return idx <= pos && pos <= idx + reprobe;
		}
	}
}