using UnityEngine;
using MidiJack;

[CreateAssetMenu(menuName="OnCollisionParams/SpotColor")]
class SpotColorOnCollisionParams : OnCollisionParams {
    public Color color;
    public int index;

    public override void AddComponent(GameObject gameObject)
    {
        SpotColorOnCollision col = gameObject.AddComponent<SpotColorOnCollision>();
        col.color = color;
        col.index = index;
    }
}