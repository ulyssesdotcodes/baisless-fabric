(ns game.spawner
  (use arcadia.core arcadia.linear hard.core)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas]
           [UnityEngine.Experimental.VFX VisualEffect]
           [UnityEngine.UI Text]
           RectTransformUtility))


(create-primitive :sphere)

(def bfstate (atom {}))

(def cljtimescale (atom 1))
(defn cljtimescalef [] @cljtimescale)
(defn time [] (* Time/time (cljtimescalef)))

(defn settime [t] (reset! cljtimescale t))

(settime 1)

(time)

(defn add-obj [n obj]
  (swap! bfstate assoc n obj)
  (title-follow n obj)
  obj)

(defn rem-obj [n]
  (destroy! (@bfstate n))
  (destroy! (@bfstate (str n "-title")))
  (swap! bfstate dissoc n))

(swap! bfstate assoc :names 
    (let [obj (new GameObject "Names")
          canvas (cmpt+ obj Canvas)]
      (set! (.renderMode canvas) UnityEngine.RenderMode/ScreenSpaceOverlay)
      obj))

(swap! bfstate dissoc :names)

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

(defn move-update [obj k]
  (let [t (.transform obj)]
    (set! (.position t) (v3 (mod (time) 4) 0.5 0))))

(add-obj "Sphere" (let [sph (create-primitive :sphere)]
                    (role+ :movement sph {:state {} :update #'move-update})
                    sph))
(rem-obj "Sphere")

(swap! bfstate assoc :hi "hi")

(deref bfstate)

(add-obj :testobj (create-primitive :sphere))
(rem-obj :testobj)

(get DATA :testobj)

(destroy! (get DATA :testobj))