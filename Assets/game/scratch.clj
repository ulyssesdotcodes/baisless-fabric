(ns game.spawner
  (use arcadia.core arcadia.linear hard.core game.core game.names)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas LightType
            Quaternion]
           [UnityEngine.Experimental.VFX VisualEffect]
           [UnityEngine.Experimental.Rendering.HDPipeline HDAdditionalLightData]
           [UnityEngine.UI Text]
           RectTransformUtility))

(use 'game.core)

(bftime)

;Atoms

(def bfstate (atom {}))

(def cljtimescale (atom 1))


(swap! bfstate dissoc :names)


(defn move-update [obj k]
  (let [t (.transform obj)]
    (set! (.position t) (v3 (mod (time) 4) 0.5 0))))

(add-obj "Sphere" 
    (let [sph (create-primitive :sphere)]
    (role+ sph :movement {:state {} :update #'move-update})
    sph))
(rem-obj "Sphere")

(swap! bfstate assoc :hi "hi")

(deref bfstate)

(add-obj :testobj (create-primitive :sphere))
(rem-obj :testobj)

(get DATA :testobj)

(destroy! (get DATA :testobj))


; this works
(map #(add-personality % "Mover") (take 20 (repeatedly #(randomname))))
(map #(add-personality % "Avoider") (take 10 (repeatedly #(randomname))))

(doseq [[k v] @bfstate] (prn k)])

(doseq [k (keys @bfstate)] (prn k)) 
(doseq [k (keys @bfstate)] 
  (when (not= (@bfstate k))(destroy! (@bfstate k))))



(defn add-personality [name type]
  (add-obj 
    name
    (instantiate (Resources/Load (str "Prefabs/Personalities/" type)))))

(rem-obj "Karen")

(add-obj 
 "Silas"
 (instantiate (Resources/Load "Prefabs/Personalities/Avoider")))

(add-obj
 "Spot"
 (blink-light LightType/Spot 800 (Color/red) 2)
 (v3 0 8 0)
 (v3 90 0 0))
(rem-obj "Spot")

; vfx

(defn add-fire [obj]
  (let [vfxc (cmpt+ obj VisualEffect)]
    (set! (.visualEffectAsset vfxc) (Resources/Load "VFX/Fire"))))

; add timeout to trigger fall apart effect - fall apart effect triggers destroy
; pare down to no lights? Gotta have something to fill it up
; 

; Lights

(defrole blink
  :state {:speed 0}
  (update [obj k] ))

(defn create-light 
    ([^LightType type lumens ^Color color] 
        (let [gobj (new GameObject)
              light (cmpt+ gobj Light)
              hdlight (cmpt+ gobj HDAdditionalLightData)]
          (set! (.type light) type)
          (set! (.intensity hdlight) lumens)
          gobj)))

; Text that follows an object

(defn follow-update [roleobj k]
    (let [rolestate (state roleobj k)
          followobj (rolestate :obj)
          followobjpos (.. followobj transform position)
          pos (v3 (.x followobjpos) (+ (.y followobjpos) 0.25) (.z followobjpos))
          screenPoint (.WorldToScreenPoint Camera/main pos)
          textcmpt (cmpt roleobj Text)
          canvasRect (cmpt (@bfstate :names) UnityEngine.RectTransform)
          point Vector2/zero]
      (UnityEngine.RectTransformUtility/ScreenPointToLocalPointInRectangle canvasRect (v2 (.x screenPoint) (.y screenPoint)) nil (by-ref point))
      (set! (.. roleobj transform localPosition) (v3 (.x point) (.y point) 0))))

(defn followobj-role [obj] { :state {:obj obj} :update #'follow-update })

(defn title-follow [title obj]
  (let [textobj (instantiate (Resources/Load "Prefabs/Title"))
        text (cmpt textobj Text)]
    (set! (.name textobj) (str title "-title"))
    (set! (.text text) title)
    (child+ (@bfstate :names) textobj)
    (swap! bfstate assoc (str title "-title") textobj)
    (role+ textobj :followobj (followobj-role obj))))


; Initial state

(defn cljtimescalef [] @cljtimescale)

(defn add-obj 
  ([n obj]
   (swap! bfstate assoc n obj)
   (if (not (keyword? n)) 
     (do (set! (.name obj) n) (title-follow n obj)) ())
   obj)
  ([n obj pos] 
   (add-obj n obj) 
   (position! obj pos))
  ([n obj pos rot] 
   (add-obj n obj pos) 
   (rotation! obj (Quaternion/Euler (.x rot) (.y rot) (.z rot)))))

(defn rem-obj [n]
  (destroy! (@bfstate n))
;   (if (not (keyword? n)) (destroy! (@bfstate (str n "-title"))) ())
  (swap! bfstate dissoc n))

(swap! bfstate assoc :names 
    (let [obj (new GameObject "Names")
          canvas (cmpt+ obj Canvas)]
      (set! (.renderMode canvas) UnityEngine.RenderMode/ScreenSpaceOverlay)
      obj))

; time
(defn settime [t] 
  (update-state (@bfstate :timekeeper) :time #(assoc % :rate t)))

(defn time []
  ((state (@bfstate :timekeeper) :time) :time))

(add-obj
 :timekeeper
 (let [tk (new GameObject)]
   (role+ tk :time timekeeper-role)
   tk))
(rem-obj :timekeeper)

(@bfstate :timekeeper)

((state (@bfstate :timekeeper) :time) :time)

(defrole timekeeper-role
  :state {:time 0 :rate 1}
  (update 
   [obj k] 
   (let [{:keys [:time :rate]} (state obj k)] 
     (state+ obj k 
             {:time (+ time (* Time/deltaTime rate)) 
              :rate rate}))))

(defn run-on-trigger [tmod toff f]
  (let [t (time)
        offset])
  (when ))
