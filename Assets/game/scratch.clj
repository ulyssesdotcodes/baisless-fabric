(ns game.scratch
  (use arcadia.core arcadia.linear hard.core game.core game.names game.personalities game.cam game.events game.text)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas LightType
            Quaternion Rigidbody Camera Shader TextAnchor]
           [UnityEngine.VFX VisualEffect VFXEventAttribute]
           [UnityEngine.Rendering Volume]
           [UnityEngine.Rendering.HighDefinition HDAdditionalLightData]
           [UnityEngine.UI Text]
           RectTransformUtility
           BaseAgent
           QuarkEvent
           QuarkEvents
           QuarkEventType
           TransformEvent ResetEvent
           CljQuarkEventListener))

(use 'game.core :reload)
(use 'game.text :reload)
(use 'game.names :reload)
(use 'game.personalities :reload)
(use 'game.cam :reload)
(use 'game.events :reload)

(do
  (init)
  (init-event-passer))

(init)

(do (deinit))

  (rem-type :puppet)

(do 
  (rem-type :actor)
  (rem-type :puppet)
  (rem-type :creator)
  (rem-type :title)
  (clear-event-listeners)
  )

(def luar (object-named "Blue"))
(def tele (object-named "Red"))

(defn live [obj e]
  (Debug/Log (.Id e))
  (when (and (= (.Type e) (enum-val QuarkEventType "Transform"))
          (== (.Id e) (.GetInstanceID tele)))
    (sets! (.transform (main-cam))
           localPosition (v3+ (.Position e) (v3 0 20 -6))))
  )

(defn position-event [obj e]
  (when (= (.Type e) (enum-val QuarkEventType "Transform"))
    (let [pos (.Position e)]
      (set! (-> obj .transform .localPosition) pos)
      (set! (-> obj .transform .rotation) (.Rotation e)))))


(defn agent-listener [n fab]
  (let [obj (object-named n)]
    (create-event-listener 
       (str "Prefabs/FindEachother/" fab) 
       (.GetInstanceID obj)
       (fn [created]
         (add-obj fab :puppet created true))
       [#'position-event
        #'live]
       )))

(clear-event-listeners)

(agent-listener "Red" "Tele")

(agent-listener "Blue" "Luar")

; Text

(create-score-text :left "11")
(create-score-text :right "16")

(update-score-text :right "14")

(create-title-text "Of hope and despair lalala")


(add-obj
 "Spot"
 :light
 (blink-light LightType/Spot 800 (Color/red) 4)
 (v3 0 8 0)
 (v3 90 0 0))

(add-obj
 "Spotblue"
 :light
 (blink-light LightType/Spot 800 (Color/blue) 4)
 (v3 0 8 0)
 (v3 90 0 0))

(add-obj
 "Point0"
 :light
 (blink-light LightType/Spot 3200 (Color/red) 0.5)
 (v3 10 1 10))

(add-obj
 "Point1"
 :light
 (blink-light LightType/Spot 3200 (Color/blue) 0.33)
 (v3 -10 1 10))

(add-obj
 "Point2"
 :light
 (blink-light LightType/Point 3200 (Color/green) 2)
 (v3 -10 1 -10))

(add-obj
 "Point3"
 :light
 (blink-light LightType/Spot 3200 (Color/yellow) 2)
 (v3 10 1 -10))

(rem-obj "Spot")

; walls


(rem-type :wall)

(do
  (dotimes [n 16] (add-cubewall))
  (dotimes [n 7] (add-rectwall))
  (dotimes [n 16] (add-column)))

(dotimes [n 16] (add-cubewall))

(defn add-cubewall []
  (let [p (v3 (?f -32 32) 1 (?f -32 32))]
  (add-obj
    :uniq
    :wall
    (instantiate-in-area (Resources/Load "Prefabs/FindEachother/Objects/CubeWall"))
    false
    p)
  (add-obj
    :uniq
    :wall
    (UnityEngine.Object/Instantiate (Resources/Load "Prefabs/FindEachother/Objects/CubeWall") (.transform render-area))
    false
    p)))

(defn add-column []
  (add-obj
    :uniq
    :wall
    (instantiate-in-area (Resources/Load "Prefabs/Column"))
    (v3 (?f -32 32) 1 (?f -32 32))))

(defn add-rectwall []
  (add-obj
    :uniq
    :wall
    (instantiate-in-area (Resources/Load "Prefabs/RectWall"))
    (v3 (?f -32 32) 1 (?f -32 32))))


(rem-type :target)
(rem-type :wall)
(rem-type :actor)

; vfx


; Lights

(defn blink-light [type lumens color speed]
  (let [light (create-light type lumens color)]
    (role+ light :blink repeattrigger)
    (update-state light :blink #(assoc % :time speed))
    (update-state light :blink #(assoc % :fn (fn [obj] (set! (.enabled (cmpt obj Light)) (not (.enabled (cmpt obj Light)))))))
    light))


(defn create-light 
    ([^LightType type lumens ^Color color] 
        (let [gobj (new GameObject)
              light (cmpt+ gobj Light)
              hdlight (cmpt+ gobj HDAdditionalLightData)]
          (set! (.type light) type)
          (set! (.color light) color)
          (set! (.intensity hdlight) lumens)
          gobj)))

; VFX

(defn set-bloom [intensity]
  (let [volumeobj (GameObject/Find "Volume")
        volume (cmpt volumeobj Volume)
        profile (.profile volume)
        components (.components profile)
        bloom (nth components 4)]
    (set! (-> bloom .intensity .value) (float intensity))))

(defn set-motionblur [intensity]
  (let [volumeobj (GameObject/Find "Volume")
        volume (cmpt volumeobj Volume)
        profile (.profile volume)
        components (.components profile)
        bloom (nth components 5)]
    (set! (-> bloom .intensity .value) (float intensity))))

(defn set-dof [intensity]
  (let [volumeobj (GameObject/Find "Volume")
        volume (cmpt volumeobj Volume)
        profile (.profile volume)
        components (.components profile)
        dof (nth components 6)]
    (set! (-> dof .active) (if (> intensity 0) true false))
    (set! (-> dof .focusDistance .value) (float intensity))))

(set-bloom 
  0.3)
(set-bloom 0.2)
(set-motionblu0.4)
(set-motionblur 1.6)
(set-dof 0)

