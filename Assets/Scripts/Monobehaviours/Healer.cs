using UnityEngine;
using System.Collections;

[RequireComponent(typeof(EnergyAgent))]
[RequireComponent(typeof(HealthAgent))]
public class Healer : MonoBehaviour {
    HealAbility HealAbility;
    [HideInInspector] public HealthAgent HealthAgent;                    // Reference to the LineRenderer component which will display our laserline.
    [HideInInspector] public EnergyAgent EnergyAgent;                    // Reference to the LineRenderer component which will display our laserline.

    private bool Active = false;

    void Start() {
        HealthAgent = GetComponent<HealthAgent>();
        EnergyAgent = GetComponent<EnergyAgent>();
    }

    public void Heal()
    {
        HealthAgent.Damage(-HealAbility.HealAmount * Time.deltaTime);
        EnergyAgent.UseAbility(1, HealAbility);
    }
}