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
         targetrot (Quaternion/Lerp (-> obj .transform .localRotation) velocityrot 0.1) 
         finalrot (qq* velocityrot (Quaternion/Euler 90 0 0))
         velocitypos (v3* (qforward targetrot) -3)
         finalpos (v3+ velocitypos (v3 0 3 0))]
     (sets! (.transform obj) 
            localRotation targetrot
            localPosition finalpos))))

(defn follow-cam [name]
  (child+ (get-obj name) (main-cam))
  (role+ (main-cam) :face-parent-velocity face-parent-velocity)
  (sets! (.transform (main-cam))
         localPosition (v3 0 3 -5)
         localRotation (Quaternion/Euler 30 0 0)))

(defn unfollow-cam []
  (child- (parent (main-cam)) (main-cam))
  (role- (main-cam) :face-parent-velocity)
  (sets! (.transform (main-cam))
         localPosition (v3 -21.51 39.44 -20.65)
         localRotation (Quaternion/Euler 60 45 0)))