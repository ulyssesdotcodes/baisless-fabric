using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="OnCollisionParams/SlowPlayer")]
class SlowPlayerOnCollisionParams : ComponentParams {
    [SerializeField] float Amount = 0.33f;

    public override void AddComponent(GameObject gameObject)
    {
        SlowPlayerOnCollision col = gameObject.AddComponent<SlowPlayerOnCollision>();
        col.Amount = Amount;
    }
}