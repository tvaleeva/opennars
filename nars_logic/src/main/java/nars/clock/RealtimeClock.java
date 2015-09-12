package nars.clock;

import nars.Memory;

import java.io.Serializable;

/**
 * Created by me on 7/2/15.
 */
abstract public class RealtimeClock implements Clock {

    private final boolean updatePerCycle;
    long t, t0 = -1;
    private long start;

    public RealtimeClock() {
        this(true);
    }

    /** update every cycle if necessary, but getting the System clock
     * is not a negligble performance cost. if many cycles are iterated per
     * frame, this could become significant
     *
     */
    public RealtimeClock(boolean updatePerCycle) {
        this.updatePerCycle = updatePerCycle;
    }

    @Override
    public void clear() {
        update();
        start = t;
    }

    @Override
    public void preCycle() {
        if (updatePerCycle)
            update();
    }


    @Override
    public void preFrame(Memory memory) {
        if (!updatePerCycle)
            update();


        if (memory.resource!=null) {
            final double frameTime = memory.resource.FRAME_DURATION.stop();

            //in real-time mode, warn if frame consumed more time than reasoner duration
            final int d = memory.param.duration.get();

            if (frameTime > d) {
                memory.eventError.emit(new Lag(d, frameTime));
            }

        }
    }

    static class Lag implements Serializable {

        private final double frameTime;
        private final int dur;

        public Lag(int duration, double frameTime) {
            this.dur= duration;
            this.frameTime = frameTime;
        }

        public String toString() {
            return "Lag frameTime=" +
                    frameTime + ", duration=" + dur + " cycles)";
        }
    }


    protected void update() {
        long now = getRealTime();

        if (this.t0 != -1) {
            this.t0 = t;
        }
        else {
            //on first cycle, set previous time to current time so that delta to previous cycle = 0
            this.t0 = now;
        }

        this.t = now;
    }


    @Override
    public long time() {
        return t;
    }

    @Override
    public long timeSinceLastCycle() {
        return t0 - t;
    }

    protected abstract long getRealTime();

    float secondsSinceStart() {
        return unitsToSeconds(t - start);
    }

    protected abstract float unitsToSeconds(long l);

    @Override
    public String toString() {
        return secondsSinceStart() + "s";
    }
}
