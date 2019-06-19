using System.Collections;
using System.Collections.Generic;
using MLAgents;
using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(Rigidbody))]
[RequireComponent(typeof(RayPerception3D))]
public class MLMover : BaseAgent, IResettable {
    public MLRewardConstant constReward;
    public MLRewardMovement moveReward;

    public MLActionMove moveAction;

    Rigidbody agentRb;
    RayPerception3D rayPer;

    public override void CollectObservations() {
    }

    public override void InitializeAgent()
    {
        base.InitializeAgent();
        agentRb = GetComponent<Rigidbody>();
        rayPer = GetComponent<RayPerception3D>();
    }

    protected void AgentAction(float[] vectorAction, string textAction) {

        moveAction.runAction(vectorAction[0], vectorAction[1], 1, , transform)
    }
}