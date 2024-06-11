Q-Learning OSRS Bot: Efficient Cow Combat
Overview
This Old School RuneScape (OSRS) bot leverages Q-learning, a type of reinforcement learning, to efficiently combat cows. The bot focuses on optimizing two primary actions: attacking cows and avoiding the collection of cowhides. It learns and adapts over time to maximize rewards for attacking cows and minimizes penalties for picking up cowhides.

Features
Q-Learning Algorithm: Utilizes Q-learning to dynamically adapt to the game environment.
State Management: Recognizes two primary states - Fighting and Looting.
Reward System: Rewards attacking cows and penalizes picking up cowhides.
Epsilon Decay: Ensures the bot transitions from exploring different actions to exploiting the most rewarding actions over time.
Logging and Monitoring: Detailed logging to track actions, rewards, Q-value updates, and epsilon decay.

#Technical Details
Languages and Tools: Developed in Java using the DreamBot API.
Q-Table Storage: Saves and loads Q-values to persist learning across sessions.
Adaptive Behavior: The bot learns from interactions, adjusting its behavior to optimize for long-term rewards.
How It Works

Initialization: The bot loads or initializes a Q-table to store Q-values representing the expected rewards of actions in various states.
State Recognition: Determines the current state (Fighting or Looting) based on the player’s status.
Action Selection: Chooses actions based on the current state, either attacking cows or looting cowhides, with a preference for actions with higher Q-values.
Reward System: Receives rewards for attacking cows and penalties for looting cowhides, adjusting Q-values accordingly.
Epsilon Decay: Gradually reduces exploration over time, focusing on the most rewarding actions.
