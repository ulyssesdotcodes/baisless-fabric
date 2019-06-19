using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GroundMotion : MonoBehaviour
{
    [SerializeField] public FloatVariable Position;
    [SerializeField] public FloatVariable GameSpeed;
    private float lastPosition;
    private float initialPosition;

    // Start is called before the first frame update
    void Start()
    {
        initialPosition = transform.position.z + Position.RuntimeValue;
        lastPosition = initialPosition;
    }

    // Update is called once per frame
    void Update()
    {
        float speed = Position.RuntimeValue - lastPosition;
        Vector3 newPos = new Vector3(transform.position.x, transform.position.y, transform.position.z - speed);
        transform.position = newPos;
        lastPosition = Position.RuntimeValue;
    }
}
