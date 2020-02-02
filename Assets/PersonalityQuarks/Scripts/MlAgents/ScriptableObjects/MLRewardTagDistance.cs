using UnityEngine;
using MLAgents;
using System.Collections.Generic;
using OptionalUnity;

[CreateAssetMenu(menuName="ML/Rewards/Tags Distance")]
class MLRewardTagDistance : MLReward {
    public string TagA;
    public string TagB;
    public float Reward = 1;
    public float MaxDistance = 10;

    GameObject TagAGameObject;
    GameObject TagBGameObject;

    public override void Initialize(BaseAgent agent) {
      FindTags(agent);
    }

    public void FindTags(BaseAgent agent) {
      List<GameObject> TagAObjs = 
        agent.gameObject.GetComponentInParent<PersonalityQuarksArea>().FindGameObjectsWithTagInChildren(TagA);
      if(TagAGameObject == null && TagAObjs.Count > 0) {
        TagAGameObject = TagAObjs[0];
      }

      List<GameObject> TagBObjs = 
        agent.gameObject.GetComponentInParent<PersonalityQuarksArea>().FindGameObjectsWithTagInChildren(TagB);
      if(TagBGameObject == null && TagBObjs.Count > 0) {
        TagBGameObject = TagBObjs[0];
      }
    }
      

    public override void AddReward(BaseAgent agent, float[] vectorActions) {
      if(TagAGameObject == null || TagBGameObject == null) {
        FindTags(agent);
      } else {
        float sqrmag = (TagAGameObject.transform.position - TagBGameObject.transform.position).sqrMagnitude;
        float byMaxDist = sqrmag / (MaxDistance * MaxDistance);
        float scaledReward = Mathf.Max((1 - byMaxDist) * Reward, 0) / Mathf.Max((float)agent.agentParameters.maxStep, 1);
        agent.AddReward(scaledReward);

        if(agent.area.EventSystem != null) {
          agent.area.EventSystem.RaiseEvent(DistanceEvent.Create(agent.gameObject, sqrmag));
        }
      }
    }
}
