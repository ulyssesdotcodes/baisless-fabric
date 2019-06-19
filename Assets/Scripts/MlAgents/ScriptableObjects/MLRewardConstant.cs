using UnityEngine;

[CreateAssetMenu(menuName="ML/Rewards/Constant")]
class MLRewardConstant : MLReward {
    public float Amount;

    public void AddReward(Agent agent, float[] vectorAction) {
        return agent.AddReward(Amount);
    }
}