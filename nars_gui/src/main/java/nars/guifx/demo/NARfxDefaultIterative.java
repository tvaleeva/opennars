package nars.guifx.demo;

import nars.Global;
import nars.guifx.NARide;
import nars.nar.Default2;
import nars.nar.SingleStepNAR;
import nars.op.mental.Anticipate;

/**
 * Created by me on 9/7/15.
 */
public class NARfxDefaultIterative {

    public static void main(String[] arg) {

        Global.DEBUG = false;
        Anticipate.testing = true;
        NARide.show(new Default2(1000, 1, 1, 3).loop(), (i) -> {
            /*try {
                i.nar.input(new File("/tmp/h.nal"));
            } catch (Throwable e) {
                i.nar.memory().eventError.emit(e);
                //e.printStackTrace();
            }*/
        });

    }
}
