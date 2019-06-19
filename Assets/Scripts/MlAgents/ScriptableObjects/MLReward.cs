using UnityEngine;

public abstract class MLReward : ScriptableObject {
    public void Initialize() {

    }

    public abstract void AddReward(Agent agent, float[] vectorActions);
}