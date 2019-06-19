using UnityEngine;

public class HealthAgent : MonoBehaviour {
    public FloatVariable Health;

    public void Damage(float damage) {
        if(GetComponent<BasePlayerAgent>() != null){
            damage *= GameObject.FindObjectOfType<CibotAcademy>().resetParameters["player_take_damage"];
        }
        Health.RuntimeValue -= damage;

        WanderingAgent wanderingAgent = GetComponent<WanderingAgent>();
        if (wanderingAgent != null && wanderingAgent.enabled) {
            wanderingAgent.TookDamage(damage);
        }


    }
}