using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="OnCollisionParams/SpotToggle")]
class SpotToggleOnCollisionParams : ComponentParams {
    public int index;

    public override void AddComponent(GameObject gameObject)
    {
        SpotToggleOnCollision col = gameObject.AddComponent<SpotToggleOnCollision>();
        col.index = index;
    }
}