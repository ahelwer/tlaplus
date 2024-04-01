// Copyright (c) 2011 Microsoft Corporation.  All rights reserved.
package tlc2.tool.fp;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Markus Alexander Kuppe
 */
public class MultiFPSetTest {

	protected static final String tmpdir = System.getProperty("java.io.tmpdir") + File.separator + "MultiFPSetTest"
			+ System.currentTimeMillis();

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		new File(tmpdir).mkdirs();
	}

	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#new}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testCTorLowerMin() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, MemFPSet.class.getName());
		try {
			System.setProperty(FPSetFactory.IMPL_PROPERTY, MemFPSet.class.getName());
			FPSetConfiguration conf = new FPSetConfiguration();
			conf.setFpBits(0);
			new MultiFPSet(conf);
		} catch (RuntimeException e) {
			return;
		}
		fail("Negative fpbits must fail");
	}
	
	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#new}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testCTorMin() throws IOException {
		try {
			FPSetConfiguration conf = new FPSetConfiguration();
			conf.setFpBits(1);
			new MultiFPSet(conf);
		} catch (RuntimeException e) {
			fail();
		}
		return;
	}

	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#new}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testCTorMax() throws IOException {
		try {
			FPSetConfiguration conf = new FPSetConfiguration();
			conf.setFpBits(30);
			new MultiFPSet(conf);
		} catch (OutOfMemoryError e) {
			// might happen depending on test machine setup
			return;
		} catch (IllegalArgumentException e) {
			// Happens when MultiFPSetConfiguration is invalid (too many fpsets
			// leaving no room/memory for each individual fpset).
			if (e.getMessage().equals("Given fpSetConfig results in zero or negative fp count.")) {
				return;
			}
			// some other cause for the IAE
			fail();
		} catch (RuntimeException e) {
			fail();
		}
		return;
	}

	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#new}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testCTorHigherMax() throws IOException {
		try {
			FPSetConfiguration conf = new FPSetConfiguration();
			conf.setFpBits(31);
			new MultiFPSet(conf);
		} catch (RuntimeException e) {
			return;
		}
		fail();
	}
	
	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#put(long)}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testPutMax() throws IOException {
		FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		final MultiFPSet mfps = new MultiFPSet(conf);

		// put a random fp value into set
		try {
			mfps.put(Long.MAX_VALUE);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail();
		}
	}

	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#put(long)}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testPutMin() throws IOException {
		FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		final MultiFPSet mfps = new MultiFPSet(conf);

		// put a random fp value into set
		try {
			mfps.put(Long.MIN_VALUE);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail();
		}
	}

	/**
	 * Test method for {@link tlc2.tool.fp.MultiFPSet#put(long)}.
	 * @throws IOException Not supposed to happen
	 */
	@Test
	public void testPutZero() throws IOException {
		FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		final MultiFPSet mfps = new MultiFPSet(conf);

		// put a random fp value into set
		try {
			mfps.put(0);
		} catch (ArrayIndexOutOfBoundsException e) {
			fail();
		}
	}
	
	@Test
	public void testGetFPSet() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, MSBDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		
		MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSet");
		
		final long a = (1L << 62) + 1; // 01...0
		printBinaryString("a01...1", a);
		final long b = 1L; // 0...1
		printBinaryString("b00...1", b);
		
		FPSet aFPSet = mfps.getFPSet(a);
		Assertions.assertTrue(aFPSet == mfps.getFPSet(b));
		
		// Initially neither a nor b are in the set.
		Assertions.assertFalse(aFPSet.contains(a));
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add a to the set and verify it's in the
		// set and b isn't.
		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add b to the set as well. Now both
		// are supposed to be set members.
		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));

		Assertions.assertTrue(aFPSet.contains(a));
		Assertions.assertTrue(aFPSet.contains(b));
		Assertions.assertEquals(2, aFPSet.size());
		
		// Get the other FPSet
		FPSet[] fpSets = mfps.getFPSets();
		Set<FPSet> s = new HashSet<FPSet>();
		for (int i = 0; i < fpSets.length; i++) {
			s.add(fpSets[i]);
		}
		s.remove(aFPSet);
		FPSet bFPSet = (FPSet) s.toArray()[0];
		
		Assertions.assertFalse(bFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(b));
		Assertions.assertEquals(0, bFPSet.size());
		
		Assertions.assertTrue(mfps.checkInvariant());
	}

	@Test
	public void testGetFPSet0() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, MSBDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		
		MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSet0");
		
		final long a = (1L << 63) + 1; // 10...1
		printBinaryString("a1...1", a);
		final long b = 1L;             // 00...1
		printBinaryString("b0...1", b);
		final long c = (1L << 62) + 1; // 01...1
		printBinaryString("c1...1", c);
		final long d = (3L << 62) + 1; // 11...1
		printBinaryString("d0...1", d);
		
		FPSet aFPSet = mfps.getFPSet(a);
		FPSet bFPSet = mfps.getFPSet(b);
		Assertions.assertTrue(aFPSet != bFPSet);
		
		// Initially neither a nor b are in the set.
		Assertions.assertFalse(aFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(b));
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		// Add a to the set and verify it's in the
		// set and b isn't.
		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		// Add b to the set as well. Now both
		// are supposed to be set members.
		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(c));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));
		
		Assertions.assertFalse(mfps.put(d));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertTrue(mfps.contains(d));
		
		for (FPSet fpSet : mfps.getFPSets()) {
			Assertions.assertEquals(2, fpSet.size());
			// Expect to have two buckets
			Assertions.assertEquals(2, ((FPSetStatistic) fpSet).getTblLoad());
		}
		
		Assertions.assertTrue(mfps.checkInvariant());
	}
	
	@Test
	public void testGetFPSet1() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, MSBDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(2);
		final MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSet1");
		
		final long a = 1L; // 00...1
		printBinaryString("a02", a);
		final long b = (1L << 62) + 1; // 01...1
		printBinaryString("b02", b);
		final long c = (1L << 63) + 1; // 10...1
		printBinaryString("c02", c);
		final long d = (3L << 62) + 1; // 11...1
		printBinaryString("d02", d);
		
		final Set<FPSet> s = new HashSet<FPSet>();
		final FPSet aFPSet = mfps.getFPSet(a);
		s.add(aFPSet);
		final FPSet bFPSet = mfps.getFPSet(b);
		s.add(bFPSet);
		final FPSet cFPSet = mfps.getFPSet(c);
		s.add(cFPSet);
		final FPSet dFPSet = mfps.getFPSet(d);
		s.add(dFPSet);
		Assertions.assertEquals(4, s.size());
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(c));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(d));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertTrue(mfps.contains(d));
		
		for (FPSet fpSet : s) {
			Assertions.assertEquals(1, fpSet.size());
			// Expect to have two buckets
			Assertions.assertEquals(1, ((FPSetStatistic) fpSet).getTblLoad());
		}
		
		// a & c and b & d have collisions at the individual DiskFPSet level.
		Assertions.assertTrue(aFPSet.contains(a));
		Assertions.assertFalse(aFPSet.contains(b));
		Assertions.assertTrue(aFPSet.contains(c)); // expected collision
		Assertions.assertFalse(aFPSet.contains(d));
		
		Assertions.assertTrue(bFPSet.contains(b));
		Assertions.assertFalse(bFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(c));
		Assertions.assertTrue(bFPSet.contains(d)); // expected collision

		Assertions.assertTrue(cFPSet.contains(c));
		Assertions.assertFalse(cFPSet.contains(b));
		Assertions.assertTrue(cFPSet.contains(a)); // expected collision
		Assertions.assertFalse(cFPSet.contains(d));

		Assertions.assertTrue(dFPSet.contains(d));
		Assertions.assertTrue(dFPSet.contains(b)); // expected collision
		Assertions.assertFalse(dFPSet.contains(c));
		Assertions.assertFalse(dFPSet.contains(a));

		Assertions.assertTrue(mfps.checkInvariant());
	}

	@Test
	public void testGetFPSetL() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, LSBDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		
		MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSetL");
		
		final long a = (1L << 62) + 1;
		printBinaryString("a01", a);
		final long b = 1L;
		printBinaryString("b01", b);
		
		FPSet aFPSet = mfps.getFPSet(a);
		Assertions.assertTrue(aFPSet == mfps.getFPSet(b));
		
		// Initially neither a nor b are in the set.
		Assertions.assertFalse(aFPSet.contains(a));
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add a to the set and verify it's in the
		// set and b isn't.
		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add b to the set as well. Now both
		// are supposed to be set members.
		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));

		Assertions.assertTrue(aFPSet.contains(a));
		Assertions.assertTrue(aFPSet.contains(b));
		Assertions.assertEquals(2, aFPSet.size());
		
		// Get the other FPSet
		FPSet[] fpSets = mfps.getFPSets();
		Set<FPSet> s = new HashSet<FPSet>();
		for (int i = 0; i < fpSets.length; i++) {
			s.add(fpSets[i]);
		}
		s.remove(aFPSet);
		FPSet bFPSet = (FPSet) s.toArray()[0];
		
		Assertions.assertFalse(bFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(b));
		Assertions.assertEquals(0, bFPSet.size());

		Assertions.assertTrue(mfps.checkInvariant());
	}

	@Test
	public void testGetFPSet0L() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, LSBDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		
		MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSet0L");
		
		final long a = (1L << 63) + 1;
		printBinaryString("a01", a);
		final long b = 1L;
		printBinaryString("b01", b);
		
		FPSet aFPSet = mfps.getFPSet(a);
		FPSet bFPSet = mfps.getFPSet(b);
		Assertions.assertTrue(aFPSet != bFPSet);
		
		// Initially neither a nor b are in the set.
		Assertions.assertFalse(aFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(b));
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add a to the set and verify it's in the
		// set and b isn't.
		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add b to the set as well. Now both
		// are supposed to be set members.
		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));

		Assertions.assertTrue(mfps.checkInvariant());
	}
	
	@Test
	public void testGetFPSet1L() throws IOException {
		System.setProperty(FPSetFactory.IMPL_PROPERTY, LSBDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(2);
		final MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSet1L");
		
		final long a = 1L; // 00...1
		printBinaryString("a02", a);
		final long b = (1L << 62) + 1; // 01...1
		printBinaryString("b02", b);
		final long c = (1L << 63) + 1; // 10...1
		printBinaryString("c02", c);
		final long d = (3L << 62) + 1; // 11...1
		printBinaryString("d02", d);
		
		final Set<FPSet> s = new HashSet<FPSet>();
		final FPSet aFPSet = mfps.getFPSet(a);
		s.add(aFPSet);
		final FPSet bFPSet = mfps.getFPSet(b);
		s.add(bFPSet);
		final FPSet cFPSet = mfps.getFPSet(c);
		s.add(cFPSet);
		final FPSet dFPSet = mfps.getFPSet(d);
		s.add(dFPSet);
		Assertions.assertEquals(4, s.size());
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(c));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(d));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertTrue(mfps.contains(d));
		
		for (FPSet fpSet : s) {
			Assertions.assertEquals(1, fpSet.size());
		}
		
		// a & c and b & d have collisions at the individual DiskFPSet level.
		Assertions.assertTrue(aFPSet.contains(a));
		Assertions.assertFalse(aFPSet.contains(b));
		Assertions.assertTrue(aFPSet.contains(c)); // expected collision
		Assertions.assertFalse(aFPSet.contains(d));
		
		Assertions.assertTrue(bFPSet.contains(b));
		Assertions.assertFalse(bFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(c));
		Assertions.assertTrue(bFPSet.contains(d)); // expected collision

		Assertions.assertTrue(cFPSet.contains(c));
		Assertions.assertFalse(cFPSet.contains(b));
		Assertions.assertTrue(cFPSet.contains(a)); // expected collision
		Assertions.assertFalse(cFPSet.contains(d));

		Assertions.assertTrue(dFPSet.contains(d));
		Assertions.assertTrue(dFPSet.contains(b)); // expected collision
		Assertions.assertFalse(dFPSet.contains(c));
		Assertions.assertFalse(dFPSet.contains(a));

		Assertions.assertTrue(mfps.checkInvariant());
	}
	
	@Test
	public void testGetFPSetOffHeap() throws IOException {
		if (!System.getProperty("sun.arch.data.model").equals("64")) {
			// LongArray only works on 64bit architectures. See comment in
			// LongArray ctor.
			return;
		}
		System.setProperty(FPSetFactory.IMPL_PROPERTY, OffHeapDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		
		MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSetOffHeap");
		
		final long a = (1L << 62) + 1; // 01...0
		printBinaryString("a01...1", a);
		final long b = 1L; // 0...1
		printBinaryString("b00...1", b);
		
		FPSet aFPSet = mfps.getFPSet(a);
		Assertions.assertTrue(aFPSet == mfps.getFPSet(b));
		
		// Initially neither a nor b are in the set.
		Assertions.assertFalse(aFPSet.contains(a));
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add a to the set and verify it's in the
		// set and b isn't.
		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));

		// Add b to the set as well. Now both
		// are supposed to be set members.
		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));

		Assertions.assertTrue(aFPSet.contains(a));
		Assertions.assertTrue(aFPSet.contains(b));
		Assertions.assertEquals(2, aFPSet.size());
		
		// Get the other FPSet
		FPSet[] fpSets = mfps.getFPSets();
		Set<FPSet> s = new HashSet<FPSet>();
		for (int i = 0; i < fpSets.length; i++) {
			s.add(fpSets[i]);
		}
		s.remove(aFPSet);
		FPSet bFPSet = (FPSet) s.toArray()[0];
		
		Assertions.assertFalse(bFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(b));
		Assertions.assertEquals(0, bFPSet.size());
		
		Assertions.assertTrue(mfps.checkInvariant());
	}

	@Test
	public void testGetFPSetOffHeap0() throws IOException {
		if (!System.getProperty("sun.arch.data.model").equals("64")) {
			// LongArray only works on 64bit architectures. See comment in
			// LongArray ctor.
			return;
		}
		System.setProperty(FPSetFactory.IMPL_PROPERTY, OffHeapDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(1);
		
		MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSetOffHeap0");
		
		final long a = (1L << 63) + 1; // 10...1
		printBinaryString("a1...1", a);
		final long b = 1L;             // 00...1
		printBinaryString("b0...1", b);
		final long c = (1L << 62) + 1; // 01...1
		printBinaryString("c1...1", c);
		final long d = (3L << 62) + 1; // 11...1
		printBinaryString("d0...1", d);
		
		FPSet aFPSet = mfps.getFPSet(a);
		FPSet bFPSet = mfps.getFPSet(b);
		Assertions.assertTrue(aFPSet != bFPSet);
		
		// Initially neither a nor b are in the set.
		Assertions.assertFalse(aFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(b));
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		// Add a to the set and verify it's in the
		// set and b isn't.
		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		// Add b to the set as well. Now both
		// are supposed to be set members.
		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(c));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));
		
		Assertions.assertFalse(mfps.put(d));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertTrue(mfps.contains(d));
		
		for (FPSet fpSet : mfps.getFPSets()) {
			Assertions.assertEquals(2, fpSet.size());
			// Expect to have two buckets
			Assertions.assertEquals(2, ((FPSetStatistic) fpSet).getTblLoad());
		}
		
		Assertions.assertTrue(mfps.checkInvariant());
	}
	
	@Test
	public void testGetFPSetOffHeap1() throws IOException {
		if (!System.getProperty("sun.arch.data.model").equals("64")) {
			// LongArray only works on 64bit architectures. See comment in
			// LongArray ctor.
			return;
		}
		System.setProperty(FPSetFactory.IMPL_PROPERTY, OffHeapDiskFPSet.class.getName());
		final FPSetConfiguration conf = new FPSetConfiguration();
		conf.setFpBits(2);
		final MultiFPSet mfps = new MultiFPSet(conf);
		mfps.init(1, tmpdir, "testGetFPSetOffHeap1");
		
		final long a = 1L; // 00...1
		printBinaryString("a02", a);
		final long b = (1L << 62) + 1; // 01...1
		printBinaryString("b02", b);
		final long c = (1L << 63) + 1; // 10...1
		printBinaryString("c02", c);
		final long d = (3L << 62) + 1; // 11...1
		printBinaryString("d02", d);
		
		final Set<FPSet> s = new HashSet<FPSet>();
		final FPSet aFPSet = mfps.getFPSet(a);
		s.add(aFPSet);
		final FPSet bFPSet = mfps.getFPSet(b);
		s.add(bFPSet);
		final FPSet cFPSet = mfps.getFPSet(c);
		s.add(cFPSet);
		final FPSet dFPSet = mfps.getFPSet(d);
		s.add(dFPSet);
		Assertions.assertEquals(4, s.size());
		
		Assertions.assertFalse(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(a));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertFalse(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(b));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertFalse(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(c));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertFalse(mfps.contains(d));

		Assertions.assertFalse(mfps.put(d));
		Assertions.assertTrue(mfps.contains(a));
		Assertions.assertTrue(mfps.contains(b));
		Assertions.assertTrue(mfps.contains(c));
		Assertions.assertTrue(mfps.contains(d));
		
		for (FPSet fpSet : s) {
			Assertions.assertEquals(1, fpSet.size());
			// Expect to have two buckets
			Assertions.assertEquals(1, ((FPSetStatistic) fpSet).getTblLoad());
		}
		
		// a & c and b & d have collisions at the individual DiskFPSet level.
		Assertions.assertTrue(aFPSet.contains(a));
		Assertions.assertFalse(aFPSet.contains(b));
		Assertions.assertTrue(aFPSet.contains(c)); // expected collision
		Assertions.assertFalse(aFPSet.contains(d));
		
		Assertions.assertTrue(bFPSet.contains(b));
		Assertions.assertFalse(bFPSet.contains(a));
		Assertions.assertFalse(bFPSet.contains(c));
		Assertions.assertTrue(bFPSet.contains(d)); // expected collision

		Assertions.assertTrue(cFPSet.contains(c));
		Assertions.assertFalse(cFPSet.contains(b));
		Assertions.assertTrue(cFPSet.contains(a)); // expected collision
		Assertions.assertFalse(cFPSet.contains(d));

		Assertions.assertTrue(dFPSet.contains(d));
		Assertions.assertTrue(dFPSet.contains(b)); // expected collision
		Assertions.assertFalse(dFPSet.contains(c));
		Assertions.assertFalse(dFPSet.contains(a));

		Assertions.assertTrue(mfps.checkInvariant());
	}

	private void printBinaryString(final String id, final long a) {
//		System.out.println(String.format(id + ":%64s", Long.toBinaryString(a)).replace(' ', '0'));
	}
}
