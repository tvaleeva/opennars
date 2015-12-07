package nars.guifx.demo;

import nars.Memory;
import nars.bag.impl.GuavaCacheBag;
import nars.guifx.NARide;
import nars.nar.Default2;
import nars.process.BagForgettingEnhancer;
import nars.time.RealtimeMSClock;


/**
 * Created by me on 9/7/15.
 */
public class NARideRealtimeDefault {

    public static void main(String[] arg) {


        //Global.DEBUG = true;

        Memory mem = new Memory(new RealtimeMSClock(),
            //new MapCacheBag(
                    //new WeakValueHashMap<>()
                GuavaCacheBag.make(1024*1024)
                /*new InfiniCacheBag(
                    InfiniPeer.tmp().getCache()
                )*/
            //)
        );
        Default2 nar = new Default2(mem, 1024, 1, 1, 3);
        //nar.nal(9);
        nar.setTaskLinkBagSize(32);
        nar.setTermLinkBagSize(128);

        new BagForgettingEnhancer(nar.memory, nar.core.concepts(), 0.75f, 0.75f, 0.75f);


        /*nar.memory.conceptForgetDurations.set(10);
        nar.memory.termLinkForgetDurations.set(100);*/

        nar.memory.duration.set(750 /* ie, milliseconds */);
        nar.memory.conceptForgetDurations.setValue(20);
        //nar.spawnThread(1000/60);



        NARide.show(nar.loop(), (i) -> {
            /*try {
                i.nar.input(new File("/tmp/h.nal"));
            } catch (Throwable e) {
                i.nar.memory().eventError.emit(e);
                //e.printStackTrace();
            }*/
        });

    }
}
