using UnityEngine;

[CreateAssetMenu(menuName="ML/Rewards/Movement")]
class MLRewardConstant : MLReward {
    public int ForwardIdx = 0;
    public int TurnIdx = 1;
    public float MultiplierForward;
    public float MultiplierTurn;

    public void AddReward(Agent agent, float[] vectorActions) {
        agent.AddReward(vectorActions[ForwardId] * MultiplierForward);
        agent.AddReward(vectorActions[TurnIdx] * MultiplierTurn);
    }
}