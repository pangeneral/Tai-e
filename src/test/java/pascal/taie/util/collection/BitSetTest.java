/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.util.collection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static pascal.taie.util.collection.SparseBitSet.SHIFT1;
import static pascal.taie.util.collection.SparseBitSet.SHIFT2;
import static pascal.taie.util.collection.SparseBitSet.SHIFT3;

public abstract class BitSetTest {

    protected abstract BitSet of(int... indexes);

    // ------------------------------------------------------------------------
    // test set operations
    // ------------------------------------------------------------------------
    @Test
    public void test() {
        BitSet s = of();
        assertTrue(s.set(1));
        assertFalse(s.set(1));
        assertTrue(s.set(10000));
        assertFalse(s.set(10000));
        assertEquals(2, s.cardinality());
        System.out.println(s);
    }

    @Test
    public void testClear() {
        BitSet s = of(1, 10000);
        assertEquals(2, s.cardinality());
        s.clear(1);
        assertEquals(1, s.cardinality());
        s.clear(10000);
        assertEquals(0, s.cardinality());
    }

    @Test
    public void testIntersects() {
        BitSet s1 = of();
        BitSet s2 = of();
        assertFalse(s1.intersects(s2));
        s2 = of(1, 233, 666);
        assertFalse(s1.intersects(s2));
        assertFalse(s2.intersects(s1));
        s1 = of(666, 777, 888);
        assertTrue(s1.intersects(s2));
        assertTrue(s2.intersects(s1));
        s1.clear(666);
        assertFalse(s1.intersects(s2));
    }

    @Test
    public void testContains() {
        assertTrue(of().contains(of()));
        assertTrue(of(1, 2, 3).contains(of()));
        assertTrue(of(1, 2, 3).contains(of(1)));
        assertTrue(of(1, 2, 3).contains(of(1, 2, 3)));
        assertFalse(of(1).contains(of(1, 2, 3)));

        BitSet s = of(1, 2, 3);
        int cardinality = s.cardinality();
        assertFalse(s.contains(of(11111)));
        assertEquals(cardinality, s.cardinality());
        s = of(1, 2, 3, 11111);
        cardinality = s.cardinality();
        assertTrue(s.contains(of(11111)));
        assertEquals(cardinality, s.cardinality());
    }

    @Test
    public void testContains2() {
        BitSet s1 = of(762);
        BitSet s2 = of(188);
        assertFalse(s1.contains(s2));
        assertFalse(s1.isEmpty());
    }

    @Test
    public void testContains3() {
        BitSet s1 = of(1000);
        BitSet s2 = of(1000, 3000);
        s2.clear(3000);
        assertTrue(s1.contains(s2));
    }

    @Test
    public void testAnd() {
        BitSet s = of(1, 2, 3);
        assertFalse(s.and(of(1, 2, 3)));
        assertTrue(s.and(of(1)));
        assertEquals(1, s.cardinality());
        assertFalse(s.and(of(1, 11111, 22222, 333333)));
        assertTrue(s.and(of(11111, 22222)));
        assertTrue(s.isEmpty());
        System.out.println(s);
    }

    @Test
    public void testAndNot() {
        BitSet s = of(1, 2, 3, 6666);
        assertFalse(s.andNot(of()));
        assertTrue(s.andNot(of(1, 2, 3, 6666)));
        assertTrue(s.isEmpty());
        s = of(1, 2, 3, 6666);
        assertTrue(s.andNot(of(6666)));
        assertFalse(s.isEmpty());

        s = of();
        assertTrue(s.or(of(1, 1, 1)));
        assertFalse(s.andNot(of(2)));
        assertEquals(1, s.cardinality());
        assertTrue(s.or(of(1, 333, 5555, 777, 99999)));
        assertFalse(s.andNot(of(222, 444, 666, 888, 10000)));
        assertEquals(5, s.cardinality());
        assertTrue(s.andNot(of(333, 777, 99999)));
        assertEquals(2, s.cardinality());
        System.out.println(s);
    }

    @Test
    public void testOr() {
        BitSet s = of(1, 2, 3);
        assertFalse(s.or(of(1, 2, 3)));
        assertFalse(s.or(of(1)));
        assertEquals(3, s.cardinality());
        assertTrue(s.or(of(1, 11111, 22222, 333333)));
        assertFalse(s.or(of(11111, 22222, 333333)));
        assertEquals(6, s.cardinality());
        System.out.println(s);
    }

    @Test
    @Ignore
    public void testRandomOr() {
        final int MAX = 80000, TIMES = 100;
        final Random random = new Random(0);
        int bits = 0;
        int values = 0;
        for (int i = 0; i < 1000; ++i) {
            BitSet big = of();
            for (int j = 0; j < 1000; ++j) {
                BitSet small = of();
                for (int k = 0; k < TIMES; ++k) {
                    small.set(Math.abs(random.nextInt() + 1) % MAX);
                }
                big.or(small);
            }
            bits += big.size();
            values += big.cardinality();
        }
        System.out.printf("%s: %d KB for %d values%n",
                of().getClass(), bits / 8 / 1024, values);
    }

    @Test
    public void testClearOr() {
        BitSet s = of(1, 555, 66666);
        BitSet s2 = of(1, 555, 66666);
        s2.clear(66666);
        assertFalse(s.or(s2));
        assertEquals(3, s.cardinality());

        s = of();
        s2.clear(1);
        s2.clear(555);
        assertFalse(s.or(s2));
        assertTrue(s.isEmpty());
    }

    @Test
    public void testOrDiff() {
        BitSet s = of();
        BitSet diff = s.orDiff(of(1, 333, 66666));
        assertEquals(of(1, 333, 66666), diff);
        diff = s.orDiff(of(5555, 333, 777));
        assertEquals(of(5555, 777), diff);
        diff = s.orDiff(s);
        assertTrue(diff.isEmpty());
        diff = s.orDiff(of());
        assertTrue(diff.isEmpty());
        diff = s.orDiff(of(200000, 300000, 300001));
        assertEquals(of(200000, 300000, 300001), diff);
    }

    @Test
    public void testXor() {
        BitSet s = of(1, 2, 300);
        assertTrue(s.xor(s));
        assertTrue(s.isEmpty());
        s = of(1, 444, 7777);
        assertTrue(s.xor(of(1, 444, 7777)));
        assertTrue(s.isEmpty());
        s = of(22, 333, 4444, 55555);
        assertTrue(s.xor(of(333, 2222, 55555, 66666)));
        assertEquals(4, s.cardinality());
        System.out.println(s);
    }

    @Test
    public void testSetTo() {
        BitSet s = of(1, 2, 300);
        s.setTo(of());
        assertTrue(s.isEmpty());
        s.setTo(of(111, 222, 333));
        assertEquals(s, of(111, 222, 333));
        s.setTo(of(1));
        assertEquals(s, of(1));
        s.setTo(of(11111));
        assertEquals(s, of(11111));
    }

    @Test
    public void testCopy() {
        BitSet s = of(1, 3333, 66666);
        BitSet copy = s.copy();
        assertEquals(s, copy);
        s.set(7777);
        assertNotEquals(s, copy);
    }

    // ------------------------------------------------------------------------
    // test initial with zero
    // ------------------------------------------------------------------------
    private BitSet set;

    @Before
    public void setUp() {
        set = of();
    }

    @Test
    public void testPreviousSetBit() {
        assertEquals(-1, set.previousSetBit(0));
    }

    @Test
    public void testPreviousClearBit() {
        assertEquals(0, set.previousClearBit(0));
    }

    @Test
    public void testNextSetBit() {
        assertEquals(-1, set.nextSetBit(0));
    }

    @Test
    public void testNextClearBit() {
        assertEquals(0, set.nextClearBit(0));
    }

    // ------------------------------------------------------------------------
    // test previousClearBit(int)
    // ------------------------------------------------------------------------
    @Test
    public void minusOne() {
        final int ret = set.previousClearBit(-1);

        assertEquals(-1, ret);
    }

    @Test
    public void empty() {
        final int ret = set.previousClearBit(0);

        assertEquals(0, ret);
    }

    @Test
    public void bottomBit() {
        final int ret = set.previousClearBit(1);

        assertEquals(1, ret);
    }

    @Test
    public void sameBit() {
        set.set(12345);
        final int ret = set.previousClearBit(12345);

        assertEquals(12344, ret);
    }

    @Test
    public void level1Miss() {
        final int i = (1 << (SHIFT1 + SHIFT3));
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level1MissPlus1() {
        final int i = (1 << (SHIFT1 + SHIFT3)) + 1;
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level1MissMinus1() {
        final int i = (1 << (SHIFT1 + SHIFT3)) - 1;
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level2Miss() {
        final int i = (1 << (SHIFT3 + SHIFT2));
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level2MissPlus1() {
        final int i = (1 << (SHIFT3 + SHIFT2)) + 1;
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level2MissMinus1() {
        final int i = (1 << (SHIFT3 + SHIFT2)) - 1;
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level3Miss() {
        final int i = (1 << SHIFT3);
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level3MissPlus1() {
        final int i = (1 << SHIFT3) + 1;
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void level3MissMinus1() {
        final int i = (1 << SHIFT3) - 1;
        set.set(i);
        final int ret = set.previousClearBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void noneBelow() {
        set.set(1);
        final int ret = set.previousClearBit(1);

        assertEquals(0, ret);
    }

    @Test
    public void oneBelow() {
        set.set(1);
        final int ret = set.previousClearBit(2);

        assertEquals(2, ret);
    }

    @Test
    public void threeNo() {
        set.set(1);
        final int ret = set.previousClearBit(3);

        assertEquals(3, ret);
    }

    @Test
    public void three() {
        set.set(3);
        final int ret = set.previousClearBit(3);

        assertEquals(2, ret);
    }

    @Test
    public void topBit() {
        final int i = Integer.MAX_VALUE - 1;
        final int ret = set.previousClearBit(i);

        assertEquals(i, ret);
    }

    @Test
    public void randomSingleEntry() {
        final Random random = new Random(0);
        for (int i = 0; i < 10000; ++i) {
            set = of();
            final int x = Math.abs(random.nextInt() + 1);
            final int ret = set.previousClearBit(x);
            assertEquals("Failed on i = " + i, x, ret);
        }
    }

    @Test
    public void bug15() {
        set.set(1);
        set.set(64);
        assertEquals(63, set.previousClearBit(64));
        set.clear(0);
        set.set(1);
        assertEquals(63, set.previousClearBit(64));
    }

    @Test
    public void randomMultiEntry() {
        if (getClass() != SparseBitSetTest.class) {
            // skip this for non-sparse bit set test
            return;
        }
        final Random random = new Random(0);
        final Set<Integer> values = new HashSet<>();
        for (int i = 0; i < 10000; ++i) {
            BitSet set = of();
            for (int j = 0; j < 1000; ++j) {
                final int x = Math.abs(random.nextInt() + 1);
                set.set(x);
                values.add(x);
            }
            final int x = Math.abs(random.nextInt() + 1);
            int expected = x;
            while (values.contains(expected)) {
                --expected;
            }
            final int ret = set.previousClearBit(x);
            assertEquals("Failed on i = " + i + " x = " + x, expected, ret);
            values.clear();
        }
    }

    // ------------------------------------------------------------------------
    // test previousSetBit(int)
    // ------------------------------------------------------------------------
    @Test
    public void setEmpty() {
        final int ret = set.previousSetBit(0);

        assertEquals(-1, ret);
    }

    @Test
    public void setBottomBit() {
        set.set(0);
        final int ret = set.previousSetBit(0);

        assertEquals(0, ret);
    }

    @Test
    public void setBetweenTwo() {
        set.set(4);
        set.set(8);
        final int ret = set.previousSetBit(5);

        assertEquals(4, ret);
    }

    @Test
    public void setInRun() {
        set.set(4);
        set.set(8);
        set.set(13);
        set.set(25);
        set.set(268);
        final int ret = set.previousSetBit(22);

        assertEquals(13, ret);
    }

    @Test
    public void setSameBit() {
        set.set(12345);
        final int ret = set.previousSetBit(12345);

        assertEquals(12345, ret);
    }

    @Test
    public void setNoneBelow() {
        set.set(1);
        final int ret = set.previousSetBit(0);

        assertEquals(-1, ret);
    }

    @Test
    public void setOneBelow() {
        set.set(1);
        final int ret = set.previousSetBit(2);

        assertEquals(1, ret);
    }

    @Test
    public void setTwoBelow() {
        set.set(1);
        final int ret = set.previousSetBit(3);

        assertEquals(1, ret);
    }

    @Test
    public void setTopBit() {
        final int i = Integer.MAX_VALUE - 1;
        set.set(i);
        final int ret = set.previousSetBit(i);

        assertEquals(i, ret);
    }

    @Test
    public void setLevel1Miss() {
        final int i = (1 << (SHIFT1 + SHIFT3));
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel1MissPlus1() {
        final int i = (1 << (SHIFT1 + SHIFT3)) + 1;
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel1MissMinus1() {
        final int i = (1 << (SHIFT1 + SHIFT3)) - 1;
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel2Miss() {
        final int i = (1 << (SHIFT3 + SHIFT2));
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel2MissPlus1() {
        final int i = (1 << (SHIFT3 + SHIFT2)) + 1;
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel2MissMinus1() {
        final int i = (1 << (SHIFT3 + SHIFT2)) - 1;
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel3Miss() {
        final int i = (1 << SHIFT3);
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel3MissPlus1() {
        final int i = (1 << SHIFT3) + 1;
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setLevel3MissMinus1() {
        final int i = (1 << SHIFT3) - 1;
        set.set(i - 1);
        final int ret = set.previousSetBit(i);

        assertEquals(i - 1, ret);
    }

    @Test
    public void setRandomSingleEntry() {
        if (getClass() != SparseBitSetTest.class) {
            // skip this for non-sparse bit set test
            return;
        }
        final int max = Integer.MAX_VALUE - 1;
        final Random random = new Random(0);
        for (int i = 0; i < 10000; ++i) {
            set = of();
            final int x = Math.abs(random.nextInt() + 1);
            set.set(x);
            final int ret = set.previousSetBit(max);
            assertEquals("Failed on i = " + i, x, ret);
        }
    }

    @Test
    public void setRandomMultiEntry() {
        if (getClass() != SparseBitSetTest.class) {
            // skip this for non-sparse bit set test
            return;
        }
        setRandomMultiEntry(Integer.MAX_VALUE);
    }

    @Test
    public void setRandomMultiEntryTight() {
        setRandomMultiEntry(2000);
    }

    public void setRandomMultiEntry(final int max) {
        final Random random = new Random(0);
        final List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 10000; ++i) {
            set = of();
            for (int j = 0; j < 1000; ++j) {
                final int x = Math.abs(random.nextInt() + 1) % max;
                set.set(x);
                values.add(x);
            }
            final int x = Math.abs(random.nextInt() + 1) % max;
            Collections.sort(values);
            int expected = -1;
            for (final Integer val : values) {
                if (val > x) {
                    break;
                }
                expected = val;
            }
            final int ret = set.previousSetBit(x);
            assertEquals("Failed on i = " + i, expected, ret);
            values.clear();
        }
    }
}
