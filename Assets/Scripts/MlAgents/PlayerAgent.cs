using System.Collections;
using System.Collections.Generic;
using MLAgents;
using UnityEngine;
using UnityEngine.UI;

[RequireComponent(typeof(RaycastShooter))]
[RequireComponent(typeof(EnergyAgent))]
[RequireComponent(typeof(HealthAgent))]
[RequireComponent(typeof(RayPerception3D))]
[RequireComponent(typeof(Rigidbody))]
public class PlayerAgent : Agent, IResettable
{
    public Brain RunBrain;
    public Brain HealBrain;
    public Brain ShootBrain;
    public Slider HealthSlider;
    public Slider EnergySlider;
    public HealAbility HealAbility;
    public float InitialHealth;
    public float InitialEnergy;
    public float MoveSpeed = 3f;
    public float TurnSpeed = 300f;
    Rigidbody agentRb;
    HealthAgent HealthAgent;
    EnergyAgent EnergyAgent;
    RaycastShooter RaycastShooter;
    private RayPerception3D rayPer;
    // Start is called before the first frame update
    public override void InitializeAgent()
    {
        base.InitializeAgent();
        agentRb = GetComponent<Rigidbody>();
        rayPer = GetComponent<RayPerception3D>();
        HealthAgent = GetComponent<HealthAgent>();
        EnergyAgent = GetComponent<EnergyAgent>();
        RaycastShooter = GetComponent<RaycastShooter>();

        FloatVariable health = ScriptableObject.CreateInstance<FloatVariable>();
        health.InitialValue = InitialHealth;
        health.RuntimeValue = InitialHealth;
        HealthAgent.Health = health;

        FloatVariable energy = ScriptableObject.CreateInstance<FloatVariable>();
        energy.InitialValue = InitialEnergy;
        energy.RuntimeValue = InitialEnergy;
        EnergyAgent.EnergyPool = energy;
    }

    public override void CollectObservations() {
        float rayDistance = 50f;
        float[] rayAngles = {0f, 20f, 90f, 160f, 45f, 135f, 70f, 110f, 180f };
        string[] detectableObjects = { "enemy", "wall", "player" };
        AddVectorObs(rayPer.Perceive(rayDistance, rayAngles, detectableObjects, 0f, 0f));
        Vector3 localVelocity = transform.InverseTransformDirection(agentRb.velocity);
        AddVectorObs(localVelocity.x);
        AddVectorObs(localVelocity.z);
        AddVectorObs(HealthAgent.Health.RuntimeValue);
        AddVectorObs(EnergyAgent.EnergyPool.RuntimeValue);
    }

    public override void AgentAction(float[] vectorAction, string textAction) {
        AddReward(0.02f);
        if(Mathf.Clamp(vectorAction[2], -1, 1) > 0.5f) {
            GameObject fireResult = RaycastShooter.Fire(1);
        }

        if(Mathf.Clamp(vectorAction[3], -1, 1) > 0.5f && HealAbility.CanRun(1, EnergyAgent.EnergyPool)) {
            EnergyAgent.UseAbility(1, HealAbility);
            HealthAgent.Health.RuntimeValue += HealAbility.HealAmount * Time.deltaTime;
        }


        if (HealthAgent.Health.RuntimeValue < 0) {
            HealthAgent.Health.RuntimeValue = InitialHealth;
            EnergyAgent.EnergyPool.RuntimeValue = InitialEnergy;
            Reset();
        }

        HealthSlider.value = HealthAgent.Health.RuntimeValue / InitialHealth;
        EnergySlider.value = EnergyAgent.EnergyPool.RuntimeValue / InitialEnergy;

        Vector3 dirToGo = transform.forward * Mathf.Clamp(vectorAction[0], -0.6f, 1f);
        Vector3 rotateDir = transform.up * Mathf.Clamp(vectorAction[1], -1f, 1f);

        agentRb.AddForce(dirToGo * MoveSpeed, ForceMode.VelocityChange);
        transform.Rotate(rotateDir, Time.fixedDeltaTime * TurnSpeed);
    }

    public void ChangePlayerBrain(string brainName) {
        switch(brainName) {
            case "run":
                if (base.brain != RunBrain) {
                    GiveBrain(RunBrain);
                }
                break;
            case "shoot":
                if (base.brain != ShootBrain) {
                    GiveBrain(ShootBrain);
                }
                break;
            case "heal":
                if (base.brain != HealBrain) {
                    GiveBrain(HealBrain);
                }
                break;
        }
    }

    public void Reset() {
        int pos = (int) Mathf.Floor(Random.Range(0, 5));
        switch(pos) {
            case 0:
                transform.position = new Vector3(19, transform.position.y, 0);
                break;
            case 1:
                transform.position = new Vector3(-19, transform.position.y, 0);
                break;
            case 2:
                transform.position = new Vector3(0, transform.position.y, 19);
                break;
            case 3:
                transform.position = new Vector3(0, transform.position.y, -19);
                break;
        }
    }
}
