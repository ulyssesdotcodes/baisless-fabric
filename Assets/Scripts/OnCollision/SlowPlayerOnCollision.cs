using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[RequireComponent(typeof(GroundMotion))]
public class SlowPlayerOnCollision : MonoBehaviour
{
    [SerializeField] float Amount;
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
    }

    void OnTriggerEnter(Collider collider) {
        if(collider.tag == "Player") {
            collider.gameObject.GetComponent<CharacterControl>().OnSlow(Amount * GetComponent<GroundMotion>().GameSpeed.RuntimeValue);
        }
    }
}
