using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class SpawnedSphere : MonoBehaviour
{
    [SerializeField] FloatVariable Position;
    [SerializeField] FloatVariable SlowAmount;
    float initialPosition;
    // Start is called before the first frame update
    void Start()
    {
        initialPosition = transform.position.z + Position.RuntimeValue;
    }

    // Update is called once per frame
    void Update()
    {
    }

    void OnCollisionEnter(Collision collision) {
        if(collision.collider.name == "Player") {
            collision.collider.gameObject.GetComponent<CharacterControl>().OnSlow(SlowAmount.RuntimeValue);
        }
        GameObject.Destroy(gameObject);
    }
}
