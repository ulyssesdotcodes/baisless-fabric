using UnityEngine;

[CreateAssetMenu(menuName="OnCollisionParams/Explode")]
class ExplodeOnCollisionParams : OnCollisionParams {
    [SerializeField] float CubeSize = 0.1f;
    [SerializeField] float ExplosionForce = 2f;
    [SerializeField] float UpForce = 10f;

    public override void AddComponent(GameObject gameObject)
    {
        ExplodeOnCollision col = gameObject.AddComponent<ExplodeOnCollision>();
        col.CubeSize = CubeSize;
        col.ExplosionForce = ExplosionForce;
        col.UpForce = UpForce;
    }
}