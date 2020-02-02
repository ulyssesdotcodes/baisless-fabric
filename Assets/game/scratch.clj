(ns game.scratch
  (use arcadia.core arcadia.linear hard.core game.core game.names game.personalities game.cam game.events game.text game.props)
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
           CljQuarkEventListener
           PersonalityQuarksArea))

(use 'game.core :reload)
(use 'game.text :reload)
(use 'game.names :reload)
(use 'game.personalities :reload)
(use 'game.cam :reload)
(use 'game.events :reload)
(use 'game.props :reload)

(do
  (init)
  (init-event-passer))

(init)

(do (deinit))
  (rem-type :puppet)

; Inits

(do 
  (init)
  (init-event-passer)

  (create-score-text :left "0")
  (create-score-text :right "0")

  (def tele-id -1)
  (def luar-id -1)

  (def tele-score (atom 0))
  (def luar-score (atom 0))

  (def luar (object-named "Blue"))
  (def tele (object-named "Red"))

  (defn position-event [obj e]
    (when (= (.Type e) (enum-val QuarkEventType "Transform"))
      (let [pos (.Position e)]
        (set! (-> obj .transform .localPosition) pos)
        (set! (-> obj .transform .rotation) (.Rotation e))
        )))

  (defn distance-event [obj e]
    (when (= (.Type e) (enum-val QuarkEventType "Distance"))
      (let [distance (.Distance e)
            id (.Id e)
            score-tag (if (= id tele-id) :left :right)
            score (if (= id tele-id) tele-score luar-score)
            ]
        (swap! score (fn [s] (+ s (max 0 (- 1 (Mathf/Floor distance))))))
        (update-score-text score-tag (str @score)))))

  (defn live [obj e]
    (position-event obj e)
    (distance-event obj e))


  (defn agent-listener [obj fabname fab]
    (if (= fabname "Tele") 
      (def tele-id (.GetInstanceID obj)) 
      (def luar-id (.GetInstanceID obj)))
    (create-event-listener 
       (str "Prefabs/FindEachother/" fab) 
       (.GetInstanceID obj)
       (fn [created]
         (add-obj fabname :puppet created true))
       [#'live]
       ))

  (defn reset-agents []
    (let [pos (v3 (?f -32 32) 0.5 (?f -32 32))]
    (position! luar pos)
    (position! tele (v3 (* -1 (.x pos) 0.5 (.z pos))))
  )



; Act I

(create-title-text "Act I: Kisses")

(agent-listener tele "Tele" "Tele")
(agent-listener luar "Luar" "Luar")

(def tele-particles (object-named "Tele"))
(def luar-particles (object-named "Luar"))

(do
  (defn cam-far []
    (focus-cam [tele-particles luar-particles] (v3 0 20 -4) false))

  (defn cam-near []
    (focus-cam [tele-particles luar-particles] (v3 0 2 0) true))

  (defn cam-far-zoom []
    (focus-cam [tele-particles luar-particles] (v3 0 40 -4) true))

  (defn luar-cam-far []
    (focus-cam [luar-particles] (v3+ (.. luar transform localPosition) (v3 0 20 -4)) false))

  (defn luar-cam-near []
    (focus-cam [luar-particles] (v3+ (.. luar transform localPosition) (v3 0 2 0)) false))

  (defn tele-cam-far []
    (focus-cam [tele-particles] (v3+ (.. tele transform localPosition) (v3 0 20 -4)) false))

  (defn tele-cam-near []
    (focus-cam [tele-particles] (v3+ (.. tele transform localPosition) (v3 0 2 0)) true)))

(cam-far)

(defn from-the-top []
  (reset-agents)
  (reset! tele-score 0)
  (reset! luar-score 0))

(from-the-top)

(cam-far-zoom)

; Act II

(create-title-text "Act II: Obstacles")

(dotimes [n 4] (add-cubewall))
(dotimes [n 4] (add-column))
(dotimes [n 4] (add-rectwall))

(cam-far)

(tele-cam-near)

(from-the-top)

(rem-type :wall)

; Act III

(create-title-text "Act III: Challenges")

(dotimes [n 8] (add-moveable-box))
(dotimes [n 8] (add-movable-wedge))

(from-the-top)

(rem-type :movable)

; Act IV

(create-title-text "Act IV: Distractions")

(dotimes [n 32] (add-big-consumable))
(dotimes [n 32] (add-small-consumable))

(from-the-top)

(cam-far-zoom)

(rem-type :consumable)

; Act V

(create-title-text "Act V: A Kiss")

(dotimes [n 128] (add-random-prop))

(focus-cam [luar-particles tele-particles] (v3 0 40 -4) true)
(cam-far-zoom)

(from-the-top)

(do
  (rem-type :wall)
  (rem-type :movable)
  (rem-type :consumable))

(create-title-text "Fin")




; resets

(do 
  (rem-type :actor)
  (rem-type :puppet)
  (rem-type :creator)
  (rem-type :title)
  (rem-type :text)
  (rem-type :wall)
  (rem-type :movable)
  (rem-type :consumable)
  (clear-event-listeners))

(do
  (reset-agents)
  (focus-cam [tele-particles luar-particles] (v3 0 10 0) true)
  (reset! tele-score 0)
  (reset! luar-score 0)))



; Text

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

