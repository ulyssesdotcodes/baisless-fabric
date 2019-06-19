using UnityEngine;

[CreateAssetMenu(menuName="ML/Obs/Ray Perceive")]
class MLRewardConstant : MLObs {
    public string[] DetectableObjects;
    public float[] rayAngles = { 0f, 20f, 90f, 160f, 45f, 135f, 70f, 110f, 180f };
    public float rayDistance = 50f;
    public float offsetX = 0f;
    public float offsetY = 0f;

    public void AddObs(Agent agent) {
        RayPerception rayPer = agent.gameObject.GetComponent<RayPerception>();
        return rayPer.Perceive(rayDistance, rayAngles, detectableObjects, offsetX, offsetY);
    }
}