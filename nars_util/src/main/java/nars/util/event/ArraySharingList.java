package nars.util.event;

import com.google.common.collect.Iterators;
import nars.util.data.list.FasterList;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * Thread safe list which produces arrays for fast iteration
 * these arrays are like copy-on-write-array-list except
 * are reusable and null-terminated (and padded). so if the size shrinks,
 * it does not need to reallocate.
 *
 * null padding is applied to assist garbage collection fully
 *
 * unless the synchronized methods are used,
 * udpates may contain inconsistent data.
 *
 * use C[] nullTerminatedArray() to access this array, don't
 * change it without a good reason (it will be shared), and
 * iterate it in sequence and stop at the first null (this is the
 * end).
 */
public class ArraySharingList<C> implements Iterable<C> {

    protected final FasterList<C> data = new FasterList();
    private final IntFunction<C[]> arrayBuilder;
    protected transient C[] array = null;
    private transient final AtomicBoolean change = new AtomicBoolean(true);

    public ArraySharingList(IntFunction<C[]> arrayBuilder) {
        super();
        this.arrayBuilder = arrayBuilder;
    }

//    @Override
//    public void writeExternal(ObjectOutput objectOutput) throws IOException {
//        objectOutput.writeInt(size());
//        this.forEach(t -> {
//            try {
//                objectOutput.writeObject(t);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
//        int num = objectInput.readInt();
//        for (int i = 0; i < num; i++)
//            add((C) objectInput.readObject());
//    }

    public final boolean add(C x) {
        if (data.add(x)) {
            change.set(true);
            return true;
        }
        return false;
    }



    public final void add(int index, C element) {
        data.add(index, element);
        change.set(true);
    }

    public boolean addAll(int index, Collection<? extends C> source) {
        if (data.addAll(index, source)) {
            change.set(true);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public final C remove(int index) {
        C removed = data.remove(index);
        if (removed!=null) {
            change.set(true);
        }
        return removed;
    }

    public final boolean remove(C x) {
        if (data.remove(x)) {
            change.set(true);
            return true;
        }
        return false;
    }

    public final boolean isEmpty() {
        return getCachedNullTerminatedArray()==null;
    }

    public final void clear() {
        if (isEmpty()) return;
        data.clear();
        change.set(true);
    }

    public final int size() {
        C[] a = getCachedNullTerminatedArray();
        if (a == null) return 0;
        return a.length-1; //not including the null
    }

    /** may be null; ignore its size, it will be at least 1 element larger than the size of the list */
    final public C[] getCachedNullTerminatedArray() {
        if (change.compareAndSet(true,false))
            updateArray();
        return this.array;
    }

    /** for thread-safe mode */
    final synchronized public C[] getCachedNullTerminatedArraySynch() {
        return getCachedNullTerminatedArray();
    }

    private final C[] updateArray() {

        //TODO for safe atomicity while the events are populated, buffer additions to a sub-list,
        //and apply them if a flag is set on the next read

        final FasterList<C> d = this.data;

        C[] a;
        if (!d.isEmpty()) {
            if ((a = this.array) == null)
                a = arrayBuilder.apply(d.size()+1);  //+1 for padding
            a = d.fillArrayNullPadded(a);
        }
        else {
            a = null;
        }

        return this.array = a;
    }

    @Override
    public void forEach(Consumer<? super C> with) {
        forEach(with, -1);
    }

    private void forEach(Consumer<? super C> with, int max) {
        C[] a = getCachedNullTerminatedArray();
        if (a == null) return;
        if (max == -1) max = a.length;
        for (int i = 0; i < max; i++) {
            C c = a[i];
            if (c == null) break;
            with.accept(c);
        }
    }

    /** not recommended to use this since it involves instantiating an iterator */
    @Override public Iterator<C> iterator() {
        C[] a = getCachedNullTerminatedArray();
        if (a == null) return Iterators.emptyIterator();

        return new Iterator<C>() {

            public C next;
            final C[] array = a;

            int i = 0;

            @Override
            public final boolean hasNext() {
                return (next = array[i]) != null;
            }

            @Override
            public final C next() {
                i++;
                return next;
            }
        };
    }
}
