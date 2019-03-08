using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[RequireComponent(typeof(Collider))]
public class StopCameraShakeOnCollision : MonoBehaviour
{
    public CameraShake CameraShake;
    void OnTriggerEnter(Collider other){
        if(other.gameObject.tag != "Player") {
            return;
        }

        Camera.main.GetComponent<CameraShakeManager>().Stop(CameraShake);
        Destroy(gameObject);
    }
}
