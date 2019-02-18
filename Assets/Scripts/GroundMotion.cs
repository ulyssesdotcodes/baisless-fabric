using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GroundMotion : MonoBehaviour
{
    [SerializeField] public FloatVariable Position;
    [SerializeField] public FloatVariable GameSpeed;
    private float initialPosition;

    // Start is called before the first frame update
    void Start()
    {
        initialPosition = transform.position.z + Position.RuntimeValue;
    }

    // Update is called once per frame
    void Update()
    {
        Vector3 newPos = new Vector3(transform.position.x, transform.position.y, transform.position.z - GameSpeed.RuntimeValue * Time.deltaTime);
        transform.position = newPos;

        if(transform.position.z < -7) {
            GameObject.Destroy(gameObject);
        }
    }
}
