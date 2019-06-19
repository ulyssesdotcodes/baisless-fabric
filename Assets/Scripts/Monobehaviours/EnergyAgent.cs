using UnityEngine;

public class EnergyAgent : MonoBehaviour {
    public FloatVariable EnergyPool;
    public float RegenAmount = 1f;
    public float MaxAmount = 4f;

    void Update() {
        EnergyPool.RuntimeValue += Mathf.Min(RegenAmount * Time.deltaTime, EnergyPool.InitialValue);
    }

    public void UseAbility(float mult, Ability ability) {
        EnergyPool.RuntimeValue -= ability.EnergyCost * Time.deltaTime * mult;
    }
}