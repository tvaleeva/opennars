//package nars.nal.meta.post;
//
//import nars.nal.RuleMatch;
//import nars.nal.meta.PreCondition;
//import nars.term.Term;
//
//
//public class Substitute extends PreCondition {
//
//    public final Term x;
//    public final Term y;
//
//
//
//    private final transient String id;
//
//    /**
//     *
//     * @param x  original term
//     * @param y  replacement term
//     */
//    public Substitute(Term x, Term y) {
//        this.x = x;
//        this.y = y;
//        id = getClass().getSimpleName() + ":(" + x + ',' + y + ')';
//    }
//
//    @Override
//    public String toString() {
//        return id;
//    }
//
//    @Override public final boolean test(RuleMatch m) {
//
//        Term a = m.apply(x, false);
//        if (a == null)
//            return false;
//
//        Term b = m.apply(y, false);
//        if (b == null)
//            return false;
//
//
//        //Map<Variable, Term> i = m.Inp;
//
////        if (a == null)
////            return false;
//
//        //Term M = b; //this one got substituted, but with what?
//        //Term with = m.assign.get(M); //with what assign assigned it to (the match between the rule and the premises)
//        //args[0] now encodes a variable which we want to replace with what M was assigned to
//        //(relevant for variable elimination rules)
//
//
//        if (substitute(m, a, b)) {
//            if (!a.equals(b)) {
//                m.secondary.put(a, b);
//                return true;
//            }
//        }
//
//        return (this instanceof SubstituteIfUnified);
//    }
//
//
//    protected boolean substitute(RuleMatch m, Term a, Term b) {
//        //for subclasses to override; here it just falls through true
//        return true;
//    }
//
//}
