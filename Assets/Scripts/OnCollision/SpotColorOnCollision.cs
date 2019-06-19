using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SpotColorOnCollision : MonoBehaviour
{
    public Color color;
    public int index;
    // Start is called before the first frame update

    void OnTriggerEnter(Collider collider){
        if(collider.name == "Player"){
            GameObject.FindGameObjectsWithTag("spot")[index].GetComponent<Light>().color = color;
        }
    }
}
