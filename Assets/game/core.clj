(ns game.core
  (use arcadia.core arcadia.linear hard.core)
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

; state

(def bfstate (atom {}))


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


; Adding objects

(defn add-obj 
  ([n type obj]
   (swap! bfstate assoc n {:type type, :item obj})
   (when (== type :actor) 
     (do (set! (.name obj) n) (title-follow n obj)))
   obj)
  ([n type obj pos] 
   (add-obj n type obj) 
   (position! obj pos))
  ([n type obj pos rot] 
   (add-obj n type obj pos) 
   (rotation! obj (Quaternion/Euler (.x rot) (.y rot) (.z rot)))))

(defn rem-obj [n]
  (let [obj (@bfstate n)]
    (when (== (:type obj) :actor) (destroy! (@bfstate (str n "-title"))))
    (destroy! obj)
    (swap! bfstate dissoc n)))

(swap! bfstate assoc :names 
    (let [obj (new GameObject "Names")
          canvas (cmpt+ obj Canvas)]
      (set! (.renderMode canvas) UnityEngine.RenderMode/ScreenSpaceOverlay)
      obj))

; time

(defn settime [t] 
  (update-state (@bfstate :timekeeper) :time #(assoc % :rate t)))

(defn bftime []
  ((state (@bfstate :timekeeper) :time) :time))

(defn rate []
  ((state (@bfstate :timekeeper) :time) :rate))

(defrole timekeeper-role
  :state {:time 0 :rate 1}
  (update 
   [obj k] 
   (let [{:keys [:time :rate]} (state obj k)] 
     (state+ obj k 
             {:time (+ time (* Time/deltaTime rate)) 
              :rate rate}))))

(add-obj
 :meta
 :timekeeper
 (let [tk (new GameObject)]
   (role+ tk :time timekeeper-role)
   tk))

; Actions

(defrole trigger
  :state { :trigger 0 :trigger-fn #() }
  (update
   [obj k]
   (let [tstate (state obj k)
         trigger (tstate :trigger)
         trigger-fn (tstate :trigger-fn)]
     (Debug/Log (bftime))
     (when
      (and
       (< (- (bftime) (* (Time/deltaTime) (rate))) trigger)
       (> (bftime) trigger))
       (trigger-fn)))))

(defrole repeattrigger
  :state { :time 1 :fn #() }
  (update
   [obj k]
   (let [rstate (state obj k)
         time (rstate :time)
         rfn (rstate :fn)]
     (when (< (mod (bftime) time) (- (bftime) (* (Time/deltaTime) (rate))))))))

(defn blink-cmpt [c t]
  (let [blobj (new GameObject)]
    (role+ blobj :blink repeattrigger)
    (update-state blobj :blink #(assoc % :time t))
    (update-state 
     blobj :blink 
     #(assoc
       %
       :fn 
       (fn [obj] (set! (.enabled c) (not (.enabled c))))))))