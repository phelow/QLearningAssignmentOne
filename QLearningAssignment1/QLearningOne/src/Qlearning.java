import java.text.DecimalFormat;
import java.util.Random;
 
/**
 * @author Kunuk Nykjaer
 */

public class Qlearning {
    final DecimalFormat df = new DecimalFormat("#.##");
 
    private Random random = new Random();
    
    // path finding
    final double alpha = 0.1;
    final double gamma = 0.9;
 
 
// states A,B,C,D,E,F
// e.g. from A we can go to B or D
// from C we can only go to C 
// C is goal state, reward 100 when B->C or F->C
// 
// _________
// |A|B|C|D|
// |_______|
// |E|F|G|H|
// |_______|
// |I|J|K|L|
// |_______|
// |M|N|O|P|
// |_______|
//
 
    /*
    final int stateA = 0;
    final int stateB = 1;
    final int stateC = 2;
    final int stateD = 3;
    final int stateE = 4;
    final int stateF = 5;
    final int stateG = 6;
    final int stateH = 7;
    final int stateI = 8;
    final int stateJ = 9;
    final int stateK = 10;
    final int stateL = 11;
    final int stateM = 12;
    final int stateN = 13;
    final int stateO = 14;
    final int stateP = 15;
    */
 
    
    //Stored random number seeds
    final long seedZero = 3;
    
    final int xWidth = 4;
    final int yWidth = 4;
    final int statesCount = 100;
    
    final int c_maxAdjacentSides = 4;
    
    int m_goalStateX;
    int m_goalStateY;
    
    static class Pair{
    	public int x;
    	public int y;
    }
    
    final int[][] states = new int[xWidth][yWidth];
    Pair[][][] actions;
    
    
    // http://en.wikipedia.org/wiki/Q-learning
    // http://people.revoledu.com/kardi/tutorial/ReinforcementLearning/Q-Learning.htm
 
    // Q(s,a)= Q(s,a) + alpha * (R(s,a) + gamma * Max(next state, all actions) - Q(s,a))
 
    int[][][][] R = new int[xWidth][yWidth][xWidth][yWidth]; // reward lookup
    double[][][][] Q = new double[xWidth][yWidth][xWidth][yWidth]; // Q learning
 
    String[][] stateNames;
 
    public Qlearning() {
        init();
    }
 
    
    
    public void init() {
    	Pair [] actionsForLayer;
    	
        random = new Random(seedZero);
    	stateNames = new String[xWidth][yWidth];
    	for(int x = 0; x < xWidth; x++){
    		for(int y = 0; y < yWidth; y++){
    			stateNames[x][y] = x + "," + y;
    			for(int i = 0; i < xWidth; i++){
    				for(int j = 0; j < yWidth; j++){
    					Q[x][y][i][j] = 0;
    					R[x][y][i][j] = 0;
    				}
    			}
    		}
    	}
    	
    	m_goalStateX = random.nextInt(xWidth);
    	m_goalStateY = random.nextInt(yWidth);
    	actions = new Pair[xWidth][yWidth][];
    	System.out.println("Initializing");
    	for(int x = 0; x < xWidth; x++){
    		for(int y = 0; y < yWidth; y++){

    			int numConnections = 0;
    			if(!(y+1 <0 || y+1 >= yWidth)){
    				numConnections++;
    			}
    			if(!(x+1 <0 || x+1 >= yWidth)){
    				numConnections++;
    			}
    			
    			if(!(y-1 <0 || y-1 >= yWidth)){
    				numConnections++;
    			}
    			if(!(x-1 <0 || x-1 >= yWidth)){
    				numConnections++;
    			}
    			
    			
    			actionsForLayer = new Pair[numConnections];
    			
    			int pos = 0;
    			
    			if(!(y+1 <0 || y+1 >= yWidth)){
    				Pair p = new Pair();
					p.x = x;
					p.y = y+1;
					actionsForLayer[pos] = p;
					if(p.x == m_goalStateX && p.y == m_goalStateY){
						R[x][y][p.x][p.y] = 100;
						System.out.println(R[x][y][p.x][p.y]);
						System.out.println("R has been set: " + x + " " + y + " " + p.x + " " + p.y);
					}
					
					pos++;
    			}
    			if(!(x+1 <0 || x+1 >= xWidth)){
    				Pair p = new Pair();
					p.x = x+1;
					p.y = y;
					actionsForLayer[pos] = p;
					if(p.x == m_goalStateX && p.y == m_goalStateY){
						R[x][y][p.x][p.y] = 100;
						System.out.println(R[x][y][p.x][p.y]);
						System.out.println("R has been set: " + x + " " + y + " " + p.x + " " + p.y);
					}
					
					pos++;
    			}
    			
    			if(!(y-1 <0 || y-1 >= yWidth)){
    				Pair p = new Pair();
					p.x = x;
					p.y = y-1;
					actionsForLayer[pos] = p;
					if(p.x == m_goalStateX && p.y == m_goalStateY){
						R[x][y][p.x][p.y] = 100;
						System.out.println(R[x][y][p.x][p.y]);
						System.out.println("R has been set: " + x + " " + y + " " + p.x + " " + p.y);
					}
					
					pos++;
    			}
    			if(!(x-1 <0 || x-1 >= xWidth)){
    				Pair p = new Pair();
					p.x = x-1;
					p.y = y;
					actionsForLayer[pos] = p;
					if(p.x == m_goalStateX && p.y == m_goalStateY){
						R[x][y][p.x][p.y] = 100;
						System.out.println(R[x][y][p.x][p.y]);
						System.out.println("R has been set: " + x + " " + y + " " + p.x + " " + p.y);
					}
					
					pos++;
    			}
    			
    			for(int i = 0; i < actionsForLayer.length; i++){
    				System.out.println("Insertion:" + actionsForLayer[i].x + " " + actionsForLayer[i].y);
    			}
    			
    			
    			actions[x][y] = actionsForLayer;
    		}
    	}
    	
    	//Randomly generate the graph
    	
    }
 
    public static void main(String[] args) {
        long BEGIN = System.currentTimeMillis();

        System.out.println("0");
        Qlearning obj = new Qlearning();
        
        System.out.println("1");
        obj.run();
        System.out.println("2");
        obj.printResult();
        System.out.println("3");
        obj.showPolicy();
 
        long END = System.currentTimeMillis();
        System.out.println("Time: " + (END - BEGIN) / 1000.0 + " sec.");
    }
 
    void run() {
        /*
         1. Set parameter , and environment reward matrix R 
         2. Initialize matrix Q as zero matrix 
         3. For each episode: Select random initial state 
            Do while not reach goal state o 
                Select one among all possible actions for the current state o 
                Using this possible action, consider to go to the next state o 
                Get maximum Q value of this next state based on all possible actions o 
                Compute o Set the next state as the current state
         */
 
        // For each episode
        System.out.println("Filling");
        
        
        for (int i = 0; i < 1000; i++) { // train episodes
        	
        	//TODO: implement epsilon value
        	
            // Select random initial state
        	Pair state = new Pair();
        	
            state.x = random.nextInt(xWidth);
            state.y = random.nextInt(yWidth);
            while (!(state.x == m_goalStateX && state.y == m_goalStateY)) // goal state
            {
                // Select one among all possible actions for the current state
                Pair[] actionsFromState = actions[state.x][state.y];
                if(actionsFromState.length > 0)
                {

                	System.out.println("state:" + state.x + "" + state.y + "Goal state: " + m_goalStateX + " " + m_goalStateY );
        			for(int u = 0; u < actionsFromState.length; u++){
        				System.out.println("Retrieval:" + actionsFromState[u].x + " " + actionsFromState[u].y);
        			}
                	
	                // Selection strategy is random in this example
	                int index = random.nextInt(actionsFromState.length);
	                Pair action = actionsFromState[index];
	 
	                
	               // System.out.println("state: " + state.x + " " + state.y + " action: " + action.x + " " + action.y);
	                
	                // Action outcome is set to deterministic in this example
	                // Transition probability is 1
	                Pair nextState = action; // data structure
	                // Using this possible action, consider to go to the next state
	                double q = Q(state, nextState);
                	//System.out.println(state.x + " " + state.y + " " + action.x + " " + action.y);
	                double maxQ = maxQ(nextState);
	                int r = R(state, nextState);
	                
	                
	                
	                double value = q + alpha * (r + gamma * maxQ - q);
	                if(r != 0){
	                	System.out.println("NOT ZERO:" + r);
	                }
	                setQ(state, action, value);
	 
	                // Set the next state as the current state
	                state = nextState;
                }
                else{
                	System.out.println("NO actions from this state we are at position:" + state.x + " " + state.y);
                    state.x = random.nextInt(xWidth);
                    state.y = random.nextInt(yWidth);
                }
            }
            System.out.println("Goal State Reached: " + state.x + " " + state.y);
            
            
        }
    }
 
    double maxQ(Pair nextState2) {
        Pair[] actionsFromState = actions[nextState2.x][nextState2.y];
        
        double maxValue = Double.MIN_VALUE;
       // System.out.println(actionsFromState.length);
        for (int i = 0; i < actionsFromState.length; i++) {
            Pair nextState = actionsFromState[i];
            double value = 0;
           // System.out.println(nextState2.x + " " + nextState2.y + " " + nextState.x + " " + nextState.y);
            value = Q[nextState2.x][nextState2.y][nextState.x][nextState.y];
 
            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }
 
    // get policy from state
    Pair policy(Pair state) {
        Pair[] actionsFromState = actions[state.x][state.y];
        double maxValue = Double.MIN_VALUE;
        Pair policyGotoState = state; // default goto self if not found
        for (int i = 0; i < actionsFromState.length; i++) {
        	Pair nextState = actionsFromState[i];
            double value = Q[state.x][state.y][nextState.x][nextState.y];
 
            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }
 
    double Q(Pair state, Pair action) {
    	return Q[state.x][state.y][action.x][action.y];
    	
    }
 
    void setQ(Pair s, Pair a, double value) {
    	//System.out.println("SetQ: " +  Q[s.x][s.y][a.x][a.y] + " " + value);
    	
        Q[s.x][s.y][a.x][a.y] = value;
    }
 
    int R(Pair s, Pair a) {
        return R[s.x][s.y][a.x][a.y];
    }
 
    void printResult() {
    	//TODO: redo this function
        System.out.println("Print result");
        for (int i = 0; i < stateNames.length; i++) {
            for (int j = 0; j < stateNames[i].length; j++) {
                System.out.print("out from " + stateNames[i][j] + ":  ");
                for(int k = 0; k < Q[i][j].length; k++){
                	for(int l = 0; l < Q[i][j][k].length; l++){
                        System.out.print(df.format(Q[i][j][k][l]) + " ");
                		
                	}
                }
            }
            System.out.println();
        }
    }
 
    // policy is maxQ(states)
    void showPolicy() {
    	//TODO: redo this function
        System.out.println("\nshowPolicy Has been deleted");  
    }
}
