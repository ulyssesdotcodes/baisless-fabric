using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SpotLumensOnCollision : MonoBehaviour
{
    public float lumens;
    public int index;
    // Start is called before the first frame update

    void OnTriggerEnter(Collider collider){
        if(collider.name == "Player"){
            GameObject.FindGameObjectsWithTag("spot")[index].GetComponent<Light>().intensity = lumens;
        }
    }
}
