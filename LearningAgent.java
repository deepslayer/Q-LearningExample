import org.dreambot.api.utilities.Logger;

import java.io.*;

public class LearningAgent implements Serializable {
    private static final long serialVersionUID = 1L;
    private double[][] qTable;
    private double alpha = 0.2; // Learning rate
    private double gamma = 0.9; // Discount factor
    private double epsilon = 0.4; // Initial exploration rate
    private double minEpsilon = 0.05; // Minimum exploration rate
    private double decayRate = 0.995; // Decay rate for epsilon
    private int iterationCount = 0; // Track iterations
    private boolean epsilonDecayed = false; // Track if epsilon has decayed fully

    public LearningAgent(int numStates, int numActions) {
        qTable = new double[numStates][numActions];
    }

 /*   public int chooseAction(int state) { //Allows for minimal exploring to keep learning
        iterationCount++;
        if (Math.random() < epsilon) {
            int randomAction = (int) (Math.random() * qTable[state].length); // Exploration
            System.out.println("Exploring action: " + randomAction + " for state: " + state);
            return randomAction;
        } else {
            int bestAction = getMaxAction(state); // Exploitation
            System.out.println("Exploiting action: " + bestAction + " for state: " + state);
            return bestAction;
        }
    }*/
 public int chooseAction(int state) { //Stops the bot from completely exploring once decayed
     iterationCount++;
     if (epsilon > minEpsilon && Math.random() < epsilon) {
         int randomAction = (int) (Math.random() * qTable[state].length); // Exploration
         Logger.log("Exploring action: " + randomAction + " for state: " + state);
         return randomAction;
     } else {
         int bestAction = getMaxAction(state); // Exploitation
         Logger.log("Exploiting action: " + bestAction + " for state: " + state);
         return bestAction;
     }
 }


    public void updateQValue(int state, int action, int nextState, double reward) {
        double maxQ = qTable[nextState][getMaxAction(nextState)];
        qTable[state][action] = qTable[state][action] + alpha * (reward + gamma * maxQ - qTable[state][action]);
        System.out.println("Updated Q-value for state " + state + ", action " + action + " to " + qTable[state][action]);
        decayEpsilon();
    }

    private int getMaxAction(int state) {
        int maxAction = 0;
        double maxValue = qTable[state][0];
        for (int i = 1; i < qTable[state].length; i++) {
            if (qTable[state][i] > maxValue) {
                maxValue = qTable[state][i];
                maxAction = i;
            }
        }
        return maxAction;
    }

    private void decayEpsilon() {
        Logger.log("Iteration: " + iterationCount + ", Decayed epsilon: " + epsilon);
        if (epsilon > minEpsilon) {
            epsilon *= decayRate;
            if (epsilon < minEpsilon) {
                epsilon = minEpsilon; // Ensure epsilon doesn't go below the minimum
                if (!epsilonDecayed) {
                    epsilonDecayed = true;
                    Logger.log("Epsilon has fully decayed. Exploration is minimal now.");
                }
            }
            if (iterationCount % 100 == 0) { // Log epsilon every 100 iterations
                Logger.log("Iteration: " + iterationCount + ", Decayed epsilon: " + epsilon);
            }
        }
    }

    public void setEpsilon(double value) {
        this.epsilon = value;
        System.out.println("Manually set epsilon to: " + epsilon);
    }

    public void saveQTable(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this);
        }
    }

    public static LearningAgent loadQTable(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (LearningAgent) in.readObject();
        }
    }
}
