using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SpotToggleOnCollision : MonoBehaviour
{
    public int index;
    // Start is called before the first frame update

    void OnTriggerEnter(Collider collider){
        if(collider.name == "Player"){
            GameObject.FindGameObjectsWithTag("spot")[index].GetComponent<Light>().enabled = 
                !GameObject.FindGameObjectsWithTag("spot")[index].GetComponent<Light>().enabled; 
            Destroy(gameObject);
        }
    }
}
