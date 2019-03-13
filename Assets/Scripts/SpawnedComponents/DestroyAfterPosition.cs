using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DestroyAfterPosition : MonoBehaviour
{
    public float zPos;
    public void Start(){

    }

    public void Update() {
        if(transform.position.z < zPos) {
            Destroy(gameObject);
        }

    }
}
