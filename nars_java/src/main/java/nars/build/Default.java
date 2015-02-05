package nars.build;

import nars.control.DefaultCore;
import nars.core.*;
import nars.core.Memory.Forgetting;
import nars.core.Memory.Timing;
import nars.io.DefaultTextPerception;
import nars.io.TextInput;
import nars.logic.entity.*;
import nars.logic.nal8.Operator;
import nars.operator.app.STMInduction;
import nars.operator.app.plan.TemporalParticlePlanner;
import nars.operator.mental.*;
import nars.util.bag.Bag;
import nars.util.bag.impl.CacheBag;
import nars.util.bag.impl.LevelBag;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static nars.operator.mental.InternalExperience.InternalExperienceMode.Full;
import static nars.operator.mental.InternalExperience.InternalExperienceMode.Minimal;

/**
 * Default set of NAR parameters which have been classically used for development.
 */
public class Default extends Build implements ConceptBuilder {

    

    
    int taskLinkBagLevels;
    
    /** Size of TaskLinkBag */
    int taskLinkBagSize;
    
    int termLinkBagLevels;
    
    /** Size of TermLinkBag */
    int termLinkBagSize;
    
    /** determines maximum number of concepts */
    int conceptBagSize;    
    
    int conceptBagLevels;
    
    /** max # subconscious "subconcept" concepts */
    int subconceptBagSize;

    /** Size of TaskBuffer */
    int taskBufferSize;
    
    int taskBufferLevels;

    InternalExperience.InternalExperienceMode internalExperience;
        
    
    transient TemporalParticlePlanner pluginPlanner = null;


    public Build level(int nalLevel) {
        this.level = nalLevel;
        return this;
    }

    /** Default DEFAULTS */
    public Default() {
        super();

        //Build Parameters
        this.level = Parameters.DEFAULT_NAL_LEVEL;
        this.internalExperience =
                level >= 8 ? InternalExperience.InternalExperienceMode.Minimal :  InternalExperience.InternalExperienceMode.None;


        setConceptBagSize(1024);
        setSubconceptBagSize(1024);
        setConceptBagLevels(32);
        
        setTaskLinkBagSize(32);
        setTaskLinkBagLevels(8);

        setTermLinkBagSize(128);
        setTermLinkBagLevels(8); //was 100, but lower value performs much better with no apparent degradation of results
        
        setNovelTaskBagSize(64);
        setNovelTaskBagLevels(8);




        //Runtime Initial Values

        param.duration.set(5);

        param.confidenceThreshold.set(0.0075);

        param.shortTermMemoryHistory.set(1);
        param.temporalRelationsMax.set(1);

        param.conceptForgetDurations.set(2.0);
        param.taskLinkForgetDurations.set(4.0);
        param.termLinkForgetDurations.set(10.0);
        param.novelTaskForgetDurations.set(2.0);

        //param.budgetThreshold.set(0.01f);

        param.conceptBeliefsMax.set(7);
        param.conceptGoalsMax.set(7);
        param.conceptQuestionsMax.set(5);

        param.inputsMaxPerCycle.set(1);
        param.conceptsFiredPerCycle.set(1);
        
        param.termLinkMaxReasoned.set(5);
        param.termLinkMaxMatched.set(10);
        param.termLinkRecordLength.set(10);
        
        param.setForgetting(Forgetting.Periodic);
        param.setTiming(Timing.Iterative);
        param.noiseLevel.set(100);

        param.reliance.set(0.9f);
        
        param.decisionThreshold.set(0.30);
    
        //add derivation filters here:
        //param.getDefaultDerivationFilters().add(new BeRational());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"[" + level +
                ((internalExperience== InternalExperience.InternalExperienceMode.None) || (internalExperience==null) ? "" : "+")
                + "]";
    }


    public Default temporalPlanner(float searchDepth, int planParticles, int inlineParticles, int maxPlans) {
        pluginPlanner = new TemporalParticlePlanner(searchDepth, planParticles, inlineParticles, maxPlans);
        return this;
    }

    @Override
    public void init(NAR n) {

        n.memory.setLevel(level);

        for (Operator o : DefaultOperators.get())
            n.memory.addOperator(o);
        for (Operator o : ExampleOperators.get())
            n.memory.addOperator(o);
        
                
        n.addPlugin(new DefaultTextPerception());
        
        n.addPlugin(new RuntimeNARSettings());

        if (level >= 7) {
            if (pluginPlanner != null) {
                n.addPlugin(pluginPlanner);
            }
            n.addPlugin(new STMInduction());
        }

        if (level >= 8) {

            n.addPlugin(new Anticipate());      // expect an event

            if (internalExperience == Minimal) {
                n.addPlugin(new InternalExperience());
            } else if (internalExperience == Full) {
                n.addPlugin(new FullInternalExperience());
                n.addPlugin(new Abbreviation());
                n.addPlugin(new Counting());
            }
        }
        
    }


    ConceptBuilder getConceptBuilder() {
        return this;
    }

    @Override
    public Concept newConcept(BudgetValue b, Term t, Memory m) {        
        Bag<Sentence, TaskLink> taskLinks = new LevelBag<>(getTaskLinkBagLevels(), getConceptTaskLinks());
        Bag<String, TermLink> termLinks = new LevelBag<>(getTermLinkBagLevels(), getConceptTermLinks());
        
        return new Concept(b, t, taskLinks, termLinks, m);        
    }

    
    public Bag<Term, Concept> newConceptBag() {
        return new LevelBag(getConceptBagLevels(), getConceptBagSize());
    }
    
    CacheBag<Term,Concept> newSubconceptBag() {        
        if (getSubconceptBagSize() == 0) return null;
        return new CacheBag(getSubconceptBagSize());
    }

    @Override
    public Core newCore() {
        return new DefaultCore(newConceptBag(), newSubconceptBag(), getConceptBuilder(), newNovelTaskBag());
    }
    
    public Bag<Sentence<CompoundTerm>, Task<CompoundTerm>> newNovelTaskBag() {
        return new LevelBag<>(getNovelTaskBagLevels(), getNovelTaskBagSize());
    }

    public Default setSubconceptBagSize(int subconceptBagSize) {
        this.subconceptBagSize = subconceptBagSize;
        return this;
    }

    public int getSubconceptBagSize() {
        return subconceptBagSize;
    }
 
    
    
    public int getConceptBagSize() { return conceptBagSize; }    
    public Default setConceptBagSize(int conceptBagSize) { this.conceptBagSize = conceptBagSize; return this;   }

    
    
    public int getConceptBagLevels() { return conceptBagLevels; }    
    public Default setConceptBagLevels(int bagLevels) { this.conceptBagLevels = bagLevels; return this;  }
        
    /**
     * @return the taskLinkBagLevels
     */
    public int getTaskLinkBagLevels() {
        return taskLinkBagLevels;
    }
       
    public Default setTaskLinkBagLevels(int taskLinkBagLevels) {
        this.taskLinkBagLevels = taskLinkBagLevels;
        return this;
    }

    public Default setNovelTaskBagSize(int taskBufferSize) {
        this.taskBufferSize = taskBufferSize;
        return this;
    }

    public int getNovelTaskBagSize() {
        return taskBufferSize;
    }
    
    public Default setNovelTaskBagLevels(int l) {
        this.taskBufferLevels = l;
        return this;
    }

    public int getNovelTaskBagLevels() {
        return taskBufferLevels;
    }
    

    public int getConceptTaskLinks() {
        return taskLinkBagSize;
    }

    public Default setTaskLinkBagSize(int taskLinkBagSize) {
        this.taskLinkBagSize = taskLinkBagSize;
        return this;
    }

    public int getTermLinkBagLevels() {
        return termLinkBagLevels;
    }

    public Default setTermLinkBagLevels(int termLinkBagLevels) {
        this.termLinkBagLevels = termLinkBagLevels;
        return this;
    }

    public int getConceptTermLinks() {
        return termLinkBagSize;
    }

    public Default setTermLinkBagSize(int termLinkBagSize) {
        this.termLinkBagSize = termLinkBagSize;
        return this;
    }

    
    public Default realTime() {
        param.setTiming(Timing.Real);
        param.setForgetting(Forgetting.Periodic);
        return this;
    }
    public Default simulationTime() {
        param.setTiming(Timing.Simulation);
        param.setForgetting(Forgetting.Periodic);
        return this;
    }




    public static class CommandLineNARBuilder extends Default {
        
        List<String> filesToLoad = new ArrayList();
        
        public CommandLineNARBuilder(String[] args) {
            super();

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ("--silence".equals(arg)) {
                    arg = args[++i];
                    int sl = Integer.parseInt(arg);                
                    param.noiseLevel.set(100-sl);
                }
                else if ("--noise".equals(arg)) {
                    arg = args[++i];
                    int sl = Integer.parseInt(arg);                
                    param.noiseLevel.set(sl);
                }    
                else {
                    filesToLoad.add(arg);
                }
                
            }        
        }

        @Override
        public void init(NAR n) {
            super.init(n);
            
            for (String x : filesToLoad) {
                try {
                    n.addInput( new TextInput(new File(x) ) );
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                n.run(1);
            }
            
        }

        
        
        
        /**
         * Decode the silence level
         *
         * @param param Given argument
         * @return Whether the argument is not the silence level
         */
        public static boolean isReallyFile(String param) {
            return !"--silence".equals(param);
        }
    }

    public InternalExperience.InternalExperienceMode getInternalExperience() {
        return internalExperience;
    }

    public Default setInternalExperience(InternalExperience.InternalExperienceMode i) {
        if (i == null) i = InternalExperience.InternalExperienceMode.None;
        this.internalExperience = i;
        return this;
    }

    
    
    public static Default fromJSON(String filePath) {
        
        try {
            String c = readFile(filePath, Charset.defaultCharset());                        
            return Param.json.fromJson(c, Default.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    static String readFile(String path, Charset encoding) 
        throws IOException  {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
      }
}
