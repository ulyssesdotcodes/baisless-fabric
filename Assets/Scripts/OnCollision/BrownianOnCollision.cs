using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Klak.Motion;

[RequireComponent(typeof(GroundMotion))]
public class BrownianOnCollision : MonoBehaviour
{
    private GroundMotion ground;
    // Start is called before the first frame update
    void Start()
    {
        ground = gameObject.GetComponent<GroundMotion>();
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    void OnTriggerEnter(Collider collider){
        if(collider.name == "Player" && ground.enabled){
            gameObject.AddComponent<BrownianMotion>();
            ground.enabled = false;
        }
    }
}
