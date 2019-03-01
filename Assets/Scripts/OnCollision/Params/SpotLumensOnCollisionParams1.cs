using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="OnCollisionParams/SpotLumens")]
class SpotLumensOnCollisionParams : OnCollisionParams {
    public float lumens;
    public int index;

    public override void AddComponent(GameObject gameObject)
    {
        SpotLumensOnCollision col = gameObject.AddComponent<SpotLumensOnCollision>();
        col.lumens = lumens;
        col.index = index;
    }
}