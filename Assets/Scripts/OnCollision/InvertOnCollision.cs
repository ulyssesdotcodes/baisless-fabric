using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;

[RequireComponent(typeof(Collider))]
public class InvertOnCollision : MonoBehaviour
{
    void OnTriggerEnter(Collider other){
        if(other.gameObject.tag != "Player") {
            return;
        }

        PostProcessVolume layer = GameObject.FindGameObjectWithTag("Volume").GetComponent<PostProcessVolume>();
        Invert settings = layer.profile.GetSetting<Invert>();
        settings.invert.value = Mathf.Abs(settings.invert.value - 1);
        Destroy(gameObject);
    }
}
