import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

import java.io.File;
import java.io.IOException;

@ScriptManifest(category = Category.UTILITY, name = "QLearning Bot", author = "Deep Slayer", version = 0.1)
public class LearningBot extends AbstractScript {
    private static final String Q_TABLE_FILE_PATH = "D:\\QlearningSaveFile.dat"; //Change to appropriate directory
    private LearningAgent agent;
    private State currentState;
    private Action[] actions = Action.values();

    @Override
    public void onStart() {
        try {
            agent = LearningAgent.loadQTable(Q_TABLE_FILE_PATH);
            log("Loaded Q-table.");
        } catch (IOException | ClassNotFoundException e) {
            log("Q-table not found, starting fresh.");
            agent = new LearningAgent(State.values().length, actions.length);
        }
        currentState = State.FIGHTING;
    }

    @Override
    public int onLoop() {
        int currentStateIndex = currentState.ordinal();
        int actionIndex = agent.chooseAction(currentStateIndex);

        if (currentStateIndex >= State.values().length || actionIndex >= actions.length) {
            log("Invalid state or action index.");
            return 100;
        }

        log("Current State: " + currentState + ", Action: " + actions[actionIndex]);

        performAction(actions[actionIndex]);

        int nextStateIndex = getState().ordinal();
        double reward = getReward(currentState, actions[actionIndex], nextStateIndex);

        log("Next State: " + State.values()[nextStateIndex] + ", Reward: " + reward);

        agent.updateQValue(currentStateIndex, actionIndex, nextStateIndex, reward);

        currentState = State.values()[nextStateIndex];
        return 100; // Delay between actions
    }

    @Override
    public void onExit() {
        try {
            File file = new File(Q_TABLE_FILE_PATH);
            file.getParentFile().mkdirs(); // Create directories if they don't exist
            agent.saveQTable(Q_TABLE_FILE_PATH);
            log("Saved Q-table to " + Q_TABLE_FILE_PATH);
        } catch (IOException e) {
            log("Failed to save Q-table: " + e.getMessage());
        }
    }

    private void performAction(Action action) {
        switch (action) {
            case ATTACK_NPC:
                attackNpc();
                break;
            case PICKUP_COWHIDE:
                pickupCowhide();
                break;
        }
    }

    private void attackNpc() {
        if (Players.getLocal().isInCombat()) {
            return;
        }
        NPC cow = NPCs.closest(npc -> npc != null && npc.getName().equals("Cow") && !npc.isInCombat());
        if (cow != null) {
            if (cow.interact("Attack")) {
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 5000);
            }
        }
    }

    private void pickupCowhide() {

        GroundItem cowhide = GroundItems.closest(item -> item != null && item.getName().equals("Cowhide"));
        if (cowhide != null) {
            if (cowhide.interact("Take")) {
                Sleep.sleepUntil(() -> !cowhide.exists(), 3000); // Adjust timeout as needed
            }
        }
    }

    private State getState() {
        if (Players.getLocal().isInCombat()) {
            return State.FIGHTING;
        } else {
            return State.LOOTING;
        }
    }

    private double getReward(State state, Action action, int nextStateIndex) {
        switch (action) {
            case ATTACK_NPC:
                return 10; // Reward for attacking cows
            case PICKUP_COWHIDE:
                return -50; // Penalty for looting cowhides
            default:
                return -1; // Small penalty for any undefined actions
        }
    }
}
