(ns game.personalities
  (use arcadia.core arcadia.linear hard.core game.core game.names)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas LightType
            Quaternion Rigidbody Camera]
           [UnityEngine.Experimental.VFX VisualEffect VFXEventAttribute]
           [UnityEngine.Experimental.Rendering.HDPipeline HDAdditionalLightData]
           [UnityEngine.UI Text]
           RectTransformUtility
           BaseAgent))

(defn add-agent-vfx-col [obj]
  (set! (.visualEffectAsset (cmpt+ obj VisualEffect)) (Resources/Load "VFX/AgentCollision"))
  (role+ obj :agent-vfx-col on-collision)
  (state+
   obj :agent-vfx-col-color
   (let [color (.color (.material (cmpt obj Renderer)))]
     (v3 (.r color) (.g color) (.b color))))
  (update-state
   obj
   :agent-vfx-col
   #(assoc
     % :fn
     (fn [obj col]
       (let [vfxcmpt (cmpt obj VisualEffect)
             vfxevent (.CreateVFXEventAttribute vfxcmpt)
             rigidbody (cmpt obj Rigidbody)]
         (.SetVector3 vfxevent "position" (.point (.GetContact col 0)))
         (.SetVector3 vfxevent "color" (state obj :agent-vfx-col-color))
         (.SetVector3 vfxevent "velocity" (.velocity rigidbody))
         (.SendEvent vfxcmpt "OnCollision" vfxevent))))))

(defn add-personality [name type]
  (let [actor (instantiate-in-area (Resources/Load (str "Prefabs/Personalities/" type)))]
    (add-agent-vfx-col actor)
    (state+ actor :actor-type type)
    (add-obj name :actor actor)))
