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
  (init-event-passer)
  (def tele-id -1)
  (def luar-id -1)

  (def tele-score (atom 1))
  (def luar-score (atom 1))

  (def tele-reset-count (atom 0))
  (def luar-reset-count (atom 0))

  (def luar (object-named "Blue"))
  (def luar-vfx (object-named "Luar - VFX"))
  (def tele (object-named "Red"))
  (def tele-vfx (object-named "Tele - VFX"))
  )

; ; Inits

(do 
  (create-score-text :left "0")
  (create-score-text :right "0")


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
        (swap! score (fn [s] (+ s (* 0.125 (max 0 (- 2 (Mathf/Floor distance)))))))
        )))

  (defn consumable-event [obj e]
    (when (= (.Type e) (enum-val QuarkEventType "Consumable"))
      (let [reward (* 0.015625 (.Reward e))
            id (.Id e)
            score-tag (if (= id tele-id) :left :right)
            score (if (= id tele-id) tele-score luar-score)
            ]
        (swap! score (fn [s] (+ s reward))))))

  (defn live [obj e]
    (position-event obj e)
    (distance-event obj e)
    (consumable-event obj e))

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

  (defn from-the-top []
    (let [xrand (?f -15 15)
          xpos (+ xrand (* (Mathf/Sign xrand) 15))
          zrand (?f -15 15)
          zpos (+ zrand (* (Mathf/Sign zrand) 15))
          pos (v3 xpos 0.5 zpos)]
    (position! luar pos)
    (position! tele (v3 (* -1 xpos) 0.5 (* -1 zpos)))
    (when (> @tele-score 1) (swap! tele-reset-count (fn [c] (+ c 1))))
    (when (< @tele-score 0) (swap! tele-reset-count (fn [c] (+ c -1))))
    (when (> @luar-score 1) (swap! luar-reset-count (fn [c] (+ c 1))))
    (when (< @luar-score 0) (swap! luar-reset-count (fn [c] (+ c -1))))
    (update-score-text :left (str @tele-reset-count))
    (update-score-text :right (str @luar-reset-count))
    (reset! tele-score 1)
    (reset! luar-score 1)
    ))

  (defn update-score-alpha [obj k]
    (let [score-atom ((state obj k) :score-atom)
          score @score-atom
          light ((state obj k) :score-light)
          vfx ((state obj k) :score-vfx)
          lightlevel (* score 1000.0)
          ]
      (.SetFloat vfx "Alpha" score)
      ; (.SetFloat 
      ;   vfx 
      ;   "Intensity" 
      ;   (MasterInput/CalculateRMS (enum-val FilterType "LowPass")))
      ; (.SetFloat 
      ;   vfx 
      ;   "Roughness" 
      ;   (MasterInput/CalculateRMS (enum-val FilterType "Bypass")))
      (set! (.intensity light) (float lightlevel))
      (swap! score-atom (fn [s] (- s 0.0016)))))

  (defrole score-alpha 
    :state { :score-atom tele-score
             :score-light nil
             :score-vfx nil
           }
    :update #'update-score-alpha)
  )

(from-the-top)

; ; Act I

(create-title-text "Act I: Connection")

(do 
  (agent-listener tele "Tele" "Tele")
  (agent-listener luar "Luar" "Luar")

  (def tele-particles (object-named "Tele"))
  (def luar-particles (object-named "Luar"))
  
  (def tele-light (object-named "Tele-Light"))
  (def luar-light (object-named "Luar-Light")))

(do
  (role+ tele-particles :alpha-score score-alpha)
  (state+ tele-particles :alpha-score { 
                                 :score-atom tele-score 
                                 :score-light (cmpt tele-light HDAdditionalLightData)
                                 :score-vfx (cmpt tele-vfx VisualEffect)
                                 })
  (Debug/Log @tele-score))

(do
  (role+ luar-particles :alpha-score score-alpha)
  (state+ luar-particles :alpha-score { 
                                 :score-atom luar-score 
                                 :score-light (cmpt luar-light HDAdditionalLightData)
                                 :score-vfx (cmpt luar-vfx VisualEffect)
                                 })
  (Debug/Log @luar-score))

(do
  (defn cam-far []
    (focus-cam [tele-particles luar-particles] (v3 0 20 -4) false))

  (defn cam-near []
    (focus-cam [tele-particles luar-particles] (v3 0 4 0) false))

  (defn cam-far-zoom []
    (focus-cam [tele-particles luar-particles] (v3 0 40 -4) true))

  (defn luar-cam-far []
    (focus-cam [luar-particles] (v3+ (.. luar transform localPosition) (v3 0 20 -4)) false))

  (defn luar-cam-near []
    (focus-cam [luar-particles] (v3+ (.. luar transform localPosition) (v3 0 2 0)) false))

  (defn tele-cam-far []
    (focus-cam [tele-particles] (v3+ (.. tele transform localPosition) (v3 0 20 -4)) false))

  (defn tele-cam-near []
    (focus-cam [tele-particles] (v3+ (.. tele transform localPosition) (v3 0 4 -4)) true)))

(focus-cam [tele-particles luar-particles] (v3 0 4 60) true)

(focus-cam [tele-particles] (v3 0 20 -3) false)

(from-the-top)

(tele-cam-far)

; Act II

(create-title-text "Act II: Obstacles")

(do 
  (dotimes [n 16] (add-cubewall))
  (dotimes [n 16] (add-column))
  (dotimes [n 16] (add-rectwall)))

(cam-far-zoom)

(tele-cam-far)

(from-the-top)

(focus-cam [tele-particles] (v3 0 10 -6) false)

(rem-type :wall)

; Act III

(create-title-text "Act III: Challenges")

(dotimes [n 32] (add-moveable-box))
(dotimes [n 32] (add-movable-wedge))

(from-the-top)

(rem-type :movable)

; Act IV

(create-title-text "Act IV: Distractions")

(dotimes [n 64] (add-big-consumable))
(dotimes [n 64] (add-small-consumable))

(from-the-top)

(tele-cam-near)

(focus-cam 
  [tele-particles ] 
  (v3+ (.. tele transform localPosition) (v3 0 4 -4)) true)

(rem-type :consumable)

; Act V

(create-title-text "Act V: A Kiss")

(dotimes [n 128] (add-random-prop))
(dotimes [n 16] (add-big-consumable))
(dotimes [n 16] (add-small-consumable))
(dotimes [n 32] (add-moveable-box))
(dotimes [n 32] (add-movable-wedge))
(dotimes [n 16] (add-cubewall))
(dotimes [n 16] (add-column))
(dotimes [n 16] (add-rectwall))

(cam-far-zoom)

(tele-cam-near)

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
  (clear-event-listeners)
  (focus-cam [] (v3 0 40 0) false))

; (do
;   (reset-agents)
;   (focus-cam [tele-particles luar-particles] (v3 0 10 0) true)
;   (reset! tele-score 0)
;   (reset! luar-score 0)))



; ; Text

; (add-obj
;  "Spot"
;  :light
;  (blink-light LightType/Spot 800 (Color/red) 4)
;  (v3 0 8 0)
;  (v3 90 0 0))

; (add-obj
;  "Spotblue"
;  :light
;  (blink-light LightType/Spot 800 (Color/blue) 4)
;  (v3 0 8 0)
;  (v3 90 0 0))

; (add-obj
;  "Point0"
;  :light
;  (blink-light LightType/Spot 3200 (Color/red) 0.5)
;  (v3 10 1 10))

; (add-obj
;  "Point1"
;  :light
;  (blink-light LightType/Spot 3200 (Color/blue) 0.33)
;  (v3 -10 1 10))

; (add-obj
;  "Point2"
;  :light
;  (blink-light LightType/Point 3200 (Color/green) 2)
;  (v3 -10 1 -10))

; (add-obj
;  "Point3"
;  :light
;  (blink-light LightType/Spot 3200 (Color/yellow) 2)
;  (v3 10 1 -10))

; (rem-obj "Spot")

; ; walls


; (rem-type :wall)

; (do
;   (dotimes [n 16] (add-cubewall))
;   (dotimes [n 7] (add-rectwall))
;   (dotimes [n 16] (add-column)))

; (dotimes [n 16] (add-cubewall))



; (rem-type :target)
; (rem-type :wall)
; (rem-type :actor)

; ; vfx


; ; Lights

; (defn blink-light [type lumens color speed]
;   (let [light (create-light type lumens color)]
;     (role+ light :blink repeattrigger)
;     (update-state light :blink #(assoc % :time speed))
;     (update-state light :blink #(assoc % :fn (fn [obj] (set! (.enabled (cmpt obj Light)) (not (.enabled (cmpt obj Light)))))))
;     light))


; (defn create-light 
;     ([^LightType type lumens ^Color color] 
;         (let [gobj (new GameObject)
;               light (cmpt+ gobj Light)
;               hdlight (cmpt+ gobj HDAdditionalLightData)]
;           (set! (.type light) type)
;           (set! (.color light) color)
;           (set! (.intensity hdlight) lumens)
;           gobj)))

; ; VFX

; (defn set-bloom [intensity]
;   (let [volumeobj (GameObject/Find "Volume")
;         volume (cmpt volumeobj Volume)
;         profile (.profile volume)
;         components (.components profile)
;         bloom (nth components 4)]
;     (set! (-> bloom .intensity .value) (float intensity))))

; (defn set-motionblur [intensity]
;   (let [volumeobj (GameObject/Find "Volume")
;         volume (cmpt volumeobj Volume)
;         profile (.profile volume)
;         components (.components profile)
;         bloom (nth components 5)]
;     (set! (-> bloom .intensity .value) (float intensity))))

; (defn set-dof [intensity]
;   (let [volumeobj (GameObject/Find "Volume")
;         volume (cmpt volumeobj Volume)
;         profile (.profile volume)
;         components (.components profile)
;         dof (nth components 6)]
;     (set! (-> dof .active) (if (> intensity 0) true false))
;     (set! (-> dof .focusDistance .value) (float intensity))))

; (set-bloom 
;   0.3)
; (set-bloom 0.2)
; (set-motionblu0.4)
; (set-motionblur 1.6)
; (set-dof 0)

