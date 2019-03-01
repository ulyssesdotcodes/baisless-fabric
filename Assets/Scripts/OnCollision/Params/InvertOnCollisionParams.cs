using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="OnCollisionParams/Invert")]
class InvertOnCollisionParams : OnCollisionParams {

    public override void AddComponent(GameObject gameObject)
    {
        InvertOnCollision col = gameObject.AddComponent<InvertOnCollision>();
    }
}