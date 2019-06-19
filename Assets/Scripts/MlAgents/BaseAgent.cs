using System.Collections;
using System.Collections.Generic;
using MLAgents;
using UnityEngine;
using UnityEngine.UI;

public class MLMover : Agent, IResettable {
    public MLObs[] Observations;
    public MLReward[] Rewards;
    public MLAction[] Actions;

    public override void InitializeAgent()
    {
        base.InitializeAgent();

        foreach (MLObs obs in Observations) {
            obs.Initialize();
        }

        foreach (MLReward reward in Rewards) {
            reward.Initialize();
        }

        foreach (MLAction action in Actions) {
            action.Initialize();
        }
    }

    public override void CollectObservations() {
        foreach (MLObs obs in Observations) {
            obs.AddObs(this);
        }
    }

    protected void AgentAction(float[] vectorAction, string textAction) {
        foreach (MLReward reward in Rewards) {
            reward.AddReward(this, vectorAction);
        }

        foreach(MLAction action in Actions) {
            action.RunAction(vectorAction, this)
        }

    }
}