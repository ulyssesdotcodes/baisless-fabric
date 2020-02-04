(ns game.cam
  (use arcadia.core arcadia.linear hard.core game.core game.names)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            Vector3 PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas LightType
            Quaternion Rigidbody Camera]
           [UnityEngine.VFX VisualEffect VFXEventAttribute]
           [UnityEngine.Rendering.HighDefinition HDAdditionalLightData]
           [UnityEngine.UI Text]
           RectTransformUtility
           BaseAgent))

(def main-cam (.gameObject (Camera/main)))

(defn update-focus [obj k]
  (let [objs ((state obj k) :objs)
        zoom ((state obj k) :zoom)
        opos ((state obj k) :pos)
        center (v3div (reduce 
                 (fn [center target]
                   (v3+ center (.. target transform position)))
                 (v3 0 0 0)
                 objs) (count objs))
        forward (.normalized (v3- opos center))
        dist (reduce (fn [dist target] (Mathf/Min (Vector3/Distance (.. target transform position) opos) dist)) 1000000 objs)
        mult (* (Mathf/Cos (/ (* Mathf/PI 40) 180)) dist)
        camcmpt (cmpt obj Camera)
        fov (.fieldOfView camcmpt)
        angle (reduce 
                (fn [dist target] 
                  (Mathf/Max 
                    (* 2 (+ 5 (Vector3/Angle 
                      forward 
                      (v3- opos (.. target transform position)))))
                    dist)) 
                (* 0.95 fov) 
                objs
                )
        ]
    (.. obj transform (LookAt center))
    ; (Debug/Log angle)
    ; (Debug/Log center)
    ; (Debug/Log mult)
    (when zoom
      (set! 
        (.fieldOfView camcmpt)
        angle))
      ; (set! 
      ;   (.. obj transform position)
      ;   (v3+ center (v3* forward mult))))
  ))


(defrole focus-objs
  :state { :objs [] :zoom false :pos (v3 0 0 0) }
  :update #'update-focus)

(defn focus-cam 
  ([objs pos]
    (set! (-> main-cam .transform .localPosition) pos)
    (role+ main-cam :focus focus-objs)
    (state+ main-cam :focus { :objs objs :pos (.TransformPoint (.transform (parent main-cam)) pos) :zoom false }))
  ([objs pos zoom]
    (focus-cam objs pos)
    (update-state main-cam :focus (fn [n] (assoc n :zoom zoom)))
    (when (not zoom) (set! (.fieldOfView (cmpt main-cam Camera)) 60)))
  )

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
