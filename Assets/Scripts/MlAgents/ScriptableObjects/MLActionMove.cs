using UnityEngine;

[CreateAssetMenu(menuName="ML/Actions/Move")]
class MLActionMove : MLAction {
    public int forwardIdx = 0;
    public int turnIdx = 0;
    public float MoveSpeed;
    public float MoveMin = -0.6f;
    public float MoveMax = 1f;
    public float TurnSpeed;
    public float TurnMin = -1f;
    public float TurnMax = 1f;

    public void RunAction(float[] vectorActions, GameObject gameObject) {
        Rigidbody rigidbody = gameObject.GetComponent<Rigidbody>();

        if (rigidbody == null) return;

        Vector3 dirToGo = transform.forward * Mathf.Clamp(vectorActions[forwardIdx], MoveMin, MoveMax);
        Vector3 rotateDir = transform.up * Mathf.Clamp(vectorActions[turnIdx], TurnMin, TurnMax);

        agentRb.AddForce(dirToGo * MoveSpeed * multiplier, ForceMode.VelocityChange);
        gameObject.transform.Rotate(rotateDir, Time.fixedDeltaTime * TurnSpeed);
    }
}