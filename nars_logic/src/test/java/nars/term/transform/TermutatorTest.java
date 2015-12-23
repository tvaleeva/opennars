package nars.term.transform;

import nars.Global;
import nars.Op;
import nars.term.Term;
import nars.util.data.random.XorShift128PlusRandom;
import org.junit.Test;

import java.util.Set;

import static nars.$.$;
import static nars.$.p;
import static org.junit.Assert.assertEquals;

/**
 * Created by me on 12/22/15.
 */
public class TermutatorTest {

    final FindSubst f = new FindSubst(Op.VAR_PATTERN, new XorShift128PlusRandom(1)) {
        @Override
        public boolean onMatch() {
            return true;
        }
    };

    @Test
    public void testChoose1_2() {

        testChoice(
                new Choose1(f, $("%A..+"), $("%X"),
                        p("a", "b").toSet()), 2);

    }
    @Test
    public void testChoose1_3() {

        testChoice(
                new Choose1(f, $("%A..+"), $("%X"),
                        p("a", "b", "c").toSet()), 3);
    }
    @Test
    public void testChoose1_4() {

        testChoice(
                new Choose1(f, $("%A..+"), $("%X"),
                        p("a", "b", "c", "d").toSet()), 4);
    }


    @Test public void testChoose2_2() {

        testChoice(
                new Choose2(f, $("%A..+"),
                        new Term[] { $("%X"), $("%Y") },
                        p("a", "b").toSet()), 2);
    }

    @Test public void testChoose2_3() {

        testChoice(
                new Choose2(f, $("%A..+"),
                        new Term[] { $("%X"), $("%Y") },
                        p("a", "b", "c").toSet()), 6);
    }

    @Test public void testComm2() {
        testChoice(
                new CommutivePermutations(f, $("{%A,%B}"),
                        $("{x,y}")), 2);
    }
    @Test public void testComm3() {
        testChoice(
                new CommutivePermutations(f, $("{%A,%B,%C}"),
                        $("{x,y,z}")), 6);
    }
    @Test public void testComm4() {
        testChoice(
                new CommutivePermutations(f, $("{%A,%B,%C,%D}"),
                        $("{w,x,y,z}")), 24);
    }

    void testChoice(Termutator t, int num) {

        t.reset();
        assertEquals(num, t.getEstimatedPermutations());

        Set<String> s = Global.newHashSet(num);
        int actual = 0;
        int blocked = 0;
        int duplicates = 0;
        int i = 0;
        while (t.hasNext()) {
            f.revert(0);

            System.out.println(i++);
            if (t.next()) {
                if (s.add( f.xy.toString() )) {
                    actual++;
                } else {
                    duplicates++;
                }

            } else {
                System.err.println( f.xy.toString() );
                blocked++;
            }
        }

        System.out.println(s);

        assertEquals(num, s.size());
        assertEquals(num, actual);
        assertEquals(0, blocked);
        assertEquals(0, duplicates);

    }

}