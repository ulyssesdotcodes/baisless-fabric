(ns game.personalities
  (use arcadia.core arcadia.linear hard.core game.core game.names)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas LightType
            Quaternion Rigidbody Camera]
           [UnityEngine.VFX VisualEffect VFXEventAttribute]
           [UnityEngine.Rendering.HighDefinition HDAdditionalLightData]
           [UnityEngine.UI Text]
           RectTransformUtility
           BaseAgent))

(defn add-agent-vfx-col [obj]
  (set! (.visualEffectAsset (cmpt+ obj VisualEffect)) (Resources/Load "VFX/AgentCollision"))
  (state+
   obj :agent-vfx-col-color
   (let [color (.color (.material (cmpt obj Renderer)))]
     (v3 (.r color) (.g color) (.b color)))))

(defn add-personality [name type]
  (let [actor (instantiate-in-area (Resources/Load (str "Prefabs/Personalities/" type)))]
    (state+ actor :actor-type type)
    (add-obj name :actor actor)))
