(ns game.cam
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

(defn main-cam [] (.gameObject (Camera/main)))


(defrole face-parent-velocity
  :state {}
  (update 
   [obj k]
   (let [parentrb (cmpt (parent obj) Rigidbody)
         parentvelocity (.velocity parentrb)
         parentlocalvelocity (.InverseTransformDirection (.transform (parent obj)) parentvelocity)
         velocityrot (Quaternion/LookRotation parentlocalvelocity)
         targetrot (Quaternion/Lerp (-> obj .transform .localRotation) velocityrot 0.8) 
         finalrot (qq* velocityrot (Quaternion/Euler 30 0 0))
         velocitypos (v3* (qforward targetrot) -3)
         finalpos (v3+ velocitypos (v3 0 0.3 2))]
     (sets! (.transform obj) 
            localRotation targetrot
            localPosition finalpos))))

(defn follow-cam [name]
  (child+ (get-obj name) (main-cam))
  (role+ (main-cam) :face-parent-velocity face-parent-velocity)
  (sets! (.transform (main-cam))
         localPosition (v3 0 1 -20)
         localRotation (Quaternion/Euler 30 0 0)))

(defn unfollow-cam []
  (child- (parent (main-cam)) (main-cam))
  (role- (main-cam) :face-parent-velocity)
  (sets! (.transform (main-cam))
         localPosition (v3 -25.51 47.44 -23.65)
         localRotation (Quaternion/Euler 60 45 0)))
