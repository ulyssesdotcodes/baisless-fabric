using System.Collections;
using System.Collections.Generic;
using MLAgents;
using UnityEngine;

[RequireComponent(typeof(HealthAgent))]
[RequireComponent(typeof(EnergyAgent))]
[RequireComponent(typeof(RayPerception3D))]
[RequireComponent(typeof(Rigidbody))]
public class WanderingAgent : BasePlayerAgent, IResettable
{
    CibotAcademy Academy;
    public float MoveSpeed = 3f;
    public float TurnSpeed = 300f;
    Rigidbody agentRb;
    HealthAgent HealthAgent;
    EnergyAgent EnergyAgent;
    public float InitialEnergy = 4f;
    private RayPerception3D rayPer;
    // Start is called before the first frame update
    public override void InitializeAgent()
    {
        base.InitializeAgent();
        agentRb = GetComponent<Rigidbody>();
        rayPer = GetComponent<RayPerception3D>();
        HealthAgent = GetComponent<HealthAgent>();
        EnergyAgent = GetComponent<EnergyAgent>();

        FloatVariable health = ScriptableObject.CreateInstance<FloatVariable>();
        health.InitialValue = InitialHealth;
        health.RuntimeValue = InitialHealth;
        HealthAgent.Health = health;

        FloatVariable energy = ScriptableObject.CreateInstance<FloatVariable>();
        energy.InitialValue = InitialEnergy;
        energy.RuntimeValue = InitialEnergy;
        EnergyAgent.EnergyPool = energy;
        EnergyAgent.MaxAmount = InitialEnergy;

        Academy = GameObject.FindObjectOfType<CibotAcademy>();
    }

    public override void CollectObservations() {
        float rayDistance = 50f;
        float[] rayAngles = { 0f, 20f, 90f, 160f, 45f, 135f, 70f, 110f, 180f };
        string[] detectableObjects = { "enemy", "wall", "enemy" };
        AddVectorObs(rayPer.Perceive(rayDistance, rayAngles, detectableObjects, 0f, 0f));
        Vector3 localVelocity = transform.InverseTransformDirection(agentRb.velocity);
        AddVectorObs(localVelocity.x);
        AddVectorObs(localVelocity.z);
        AddVectorObs(HealthAgent.Health.RuntimeValue / InitialHealth);
        AddVectorObs(EnergyAgent.EnergyPool.RuntimeValue / InitialEnergy);
    }

    public override void AgentAction(float[] vectorAction, string textAction) {
        AddReward(0.002f);

        if (HealthAgent.Health.RuntimeValue <= 0) {
            Debug.Log("Dead!");
            AddReward(-1f);
            Done();
            Reset();
        }

        if (Mathf.Abs(transform.position.x) > 18 || Mathf.Abs(transform.position.z) > 18) {
            AddReward(-0.004f);
        }

        if (Mathf.Abs(transform.position.x) > 20 || Mathf.Abs(transform.position.z) > 20) {
            AddReward(-1f);
            Done();
            transform.position = new Vector3(0, transform.position.y, 0);
        }

        Vector3 dirToGo = transform.forward * Mathf.Clamp(vectorAction[0], 0.6f, 1f);
        Vector3 rotateDir = transform.up * Mathf.Clamp(vectorAction[1], -1f, 1f);

        agentRb.AddForce(dirToGo * Academy.resetParameters["player_speed"], ForceMode.VelocityChange);
        transform.Rotate(rotateDir, Time.fixedDeltaTime * TurnSpeed);
    }

    public void TookDamage(float amount) {
        AddReward(amount * -0.01f);
    }

    public void Reset() {
        HealthAgent.Health.RuntimeValue = InitialHealth;
        transform.position = new Vector3(0, transform.position.y, 0);
        agentRb.velocity = Vector3.zero;
        Done();
    }
}
