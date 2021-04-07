import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GridWindow;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.Rand;
import HAL.Util;
import picocli.CommandLine;

import static HAL.Util.*;

class Cell extends AgentSQ2Dunstackable<BirthDeath> {
    int color;

    public void Step() {
        if (G.rn.Double() < G.DEATH_PROB) {
            Dispose();
            return;
        }
        if (G.rn.Double() < G.BIRTH_PROB) {
            int nOptions = G.MapEmptyHood(G.mooreHood, Xsq(), Ysq());
            if(nOptions>0) {
                G.NewAgentSQ(G.mooreHood[G.rn.Int(nOptions)]).color=color;
            }
        }
    }
}

class BirthDeath extends AgentGrid2D<Cell> {
    int BLACK=RGB(0,0,0);
    double DEATH_PROB=PARAMS.DEATH_PROB;
    double BIRTH_PROB=PARAMS.BIRTH_PROB;
    Rand rn=new Rand();
    int[]mooreHood=MooreHood(false);
    int color;
    public BirthDeath(int x, int y,int color) {
        super(x, y, Cell.class);
        this.color=color;
    }
    public void Setup(double rad){
        int[]coords= CircleHood(true,rad);
        int nCoords= MapHood(coords,xDim/2,yDim/2);
        for (int i = 0; i < nCoords ; i++) {
            NewAgentSQ(coords[i]).color=color;
        }
    }
    public void Step() {
        for (Cell c : this) {
            c.Step();
        }
        CleanAgents();
        ShuffleAgents(rn);
    }
    public void Draw(GridWindow vis){
        for (int i = 0; i < vis.length; i++) {
            Cell c = GetAgent(i);
            vis.SetPix(i, c == null ? BLACK : c.color);
        }
    }
}

class PARAMS {
    public static int DIM=100;
    public static int RUNTIME=100000;
    public static double DEATH_PROB=0.01;
    public static double BIRTH_PROB=0.2;
    public static boolean JAR=false;
}


// Method 1 parse from command line

//public class Example {
//
//    public static void main(String[] args) {
//
//        if(args.length > 0){//if arguments, get argments
//            PARAMS.DIM = Integer.parseInt(args[0]);
//            PARAMS.RUNTIME = Integer.parseInt(args[1]);
//            PARAMS.DEATH_PROB = Double.parseDouble(args[2]);
//            PARAMS.DEATH_PROB = Double.parseDouble(args[3]);
//        } else{//no arguments
//            System.out.println("No arguments");
//        }
//
//        BirthDeath t=new BirthDeath(PARAMS.DIM,PARAMS.DIM, Util.RED);
//        GridWindow win=new GridWindow(PARAMS.DIM,PARAMS.DIM,10);
//        t.Setup(10);
//        for (int i = 0; i < PARAMS.RUNTIME; i++) {
//            win.TickPause(10);
//            t.Step();
//            t.Draw(win);
//        }
//    }
//}

// Method 2 parse from command line

//public class Example {
//
//    public static void main(String[] args) {
//
//        for (int i = 0; i < args.length; i+=2) {
//            if (args[i].equalsIgnoreCase("-dim")) {
//                PARAMS.DIM = Integer.parseInt(args[i + 1]);
//            }
//            if (args[i].equalsIgnoreCase("-t")) {
//                PARAMS.RUNTIME = Integer.parseInt(args[i + 1]);
//            }
//            if (args[i].equalsIgnoreCase("-death_prob")) {
//                PARAMS.DEATH_PROB = Double.parseDouble(args[i + 1]);
//            }
//            if (args[i].equalsIgnoreCase("-birth_prob")) {
//                PARAMS.DEATH_PROB = Double.parseDouble(args[i + 1]);
//            }
//        }
//
//        BirthDeath t=new BirthDeath(PARAMS.DIM,PARAMS.DIM, Util.RED);
//        GridWindow win=new GridWindow(PARAMS.DIM,PARAMS.DIM,10);
//        t.Setup(10);
//        for (int i = 0; i < PARAMS.RUNTIME; i++) {
//            win.TickPause(10);
//            t.Step();
//            t.Draw(win);
//        }
//    }
//}

// Method 3 picocli

@CommandLine.Command(name = "Example model",
        header="Example",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        sortOptions = true,
        headerHeading = "@|bold,underline,green Usage|@:%n%n",
        synopsisHeading = "%n@|red Put as many freaking|@:%n %n@|underline,bold,green words as you want here to give in detail information!|@:%n%n",
        descriptionHeading = "%n@|bold,underline,red Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline,blue Parameters|@:%n",
        optionListHeading = "%n@|bold,underline,red Options|@:%n",
        version = "v3.14",
        description = "Example Command Line Interface Model")
public class Example implements Runnable {
    @CommandLine.Option(names={"-s","--size"}, description="Size to use for x and y dimensions") int dim=PARAMS.DIM;
    @CommandLine.Option(names={"-t","--time"}, description="Run time. Default") int runtime=PARAMS.RUNTIME ;
    @CommandLine.Option(names={"-d","--death_rate"}, description="Death rate.") double deathProb=PARAMS.DEATH_PROB;
    @CommandLine.Option(names={"-b","--birth_rate"}, description="Birth rate.") double birthProb=PARAMS.BIRTH_PROB;



    public void run(){
        PARAMS.DIM = dim;
        PARAMS.RUNTIME = runtime;
        PARAMS.DEATH_PROB = deathProb;
        PARAMS.BIRTH_PROB = birthProb;

        try {
            RunModel();
        } catch (Exception e){
            System.out.println("IOException for RunModel()");
        }
    }

    public static void main(String[] args) {
        if(PARAMS.JAR){
            CommandLine.run(new Example(), args);
        } else {
            RunModel();
        }
    }

    public static void RunModel(){
        BirthDeath t=new BirthDeath(PARAMS.DIM,PARAMS.DIM, Util.RED);
        GridWindow win=new GridWindow(PARAMS.DIM,PARAMS.DIM,10);
        t.Setup(10);
        for (int i = 0; i < PARAMS.RUNTIME; i++) {
            win.TickPause(10);
            t.Step();
            t.Draw(win);
        }
    }
}


