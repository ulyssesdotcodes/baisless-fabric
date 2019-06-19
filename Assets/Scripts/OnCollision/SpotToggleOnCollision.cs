using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SpotToggleOnCollision : MonoBehaviour
{
    public int index;

    void OnTriggerEnter(Collider collider){
        if(collider.name == "Player"){
            GameObject.FindGameObjectsWithTag("spot")[index].GetComponent<Light>().enabled = 
                !GameObject.FindGameObjectsWithTag("spot")[index].GetComponent<Light>().enabled; 
            Destroy(gameObject);
        }
    }
}
