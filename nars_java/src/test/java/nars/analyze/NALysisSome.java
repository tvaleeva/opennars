package nars.analyze;

import nars.build.Default;
import nars.core.NewNAR;
import org.junit.Ignore;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * report filtered by failures
 */
@Ignore
public class NALysisSome extends NALysis {


    public NALysisSome(NewNAR b) {
        super(b);
    }

    public static void main(String[] args) throws FileNotFoundException {



        nal("test6", "6.24", new Default()/*.setInternalExperience(null)*/, 16);



        //results.printARFF(new PrintStream(dataOut));
        results.printCSV(new PrintStream(System.out));

    }


}
