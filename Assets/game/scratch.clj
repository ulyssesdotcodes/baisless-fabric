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
           RectTransformUtility
           BaseAgent))

(use 'game.core :reload)
(use 'game.names :reload)

(init)
(deinit)
 
@bfstate 

(rem-obj :timekeeper)
(destroy! (GameObject/Find "Names"))

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


(init) 

(add-personality (randomname) "Mover")

(map #(add-personality % "Mover") (take 10 (repeatedly #(randomname)))) 

(map #(add-personality % "Avoider") (take 4 (repeatedly #(randomname))))

(map #(add-personality % "Attractor") (take 10 (repeatedly #(randomname))))

(doseq [[k v] @bfstate] (prn k)])

(doseq [k (keys @bfstate)] (prn k)) 

(doseq [k (keys @bfstate)] 
  (when (= (@bfstate k) :actor) (cmpt (get-in @bfstate [k :item])BaseAgent)))



(defn add-personality [name type]
  (let [actor (instantiate (Resources/Load (str "Prefabs/Personalities/" type)))]
    (add-agent-vfx-col actor)
    (add-obj name :actor actor)))

(rem-obj "Karen")

(add-obj 
 "Silas"
 (instantiate (Resources/Load "Prefabs/Personalities/Avoider")))

(add-obj
 "Spot"
 :light
 (blink-light LightType/Spot 800 (Color/red) 4)
 (v3 0 8 0)
 (v3 90 0 0))

(add-obj
 "Point0"
 :light
 (blink-light LightType/Point 3200 (Color/red) 0.5)
 (v3 10 1 10))

(add-obj
 "Point1"
 :light
 (blink-light LightType/Point 3200 (Color/blue) 0.33)
 (v3 -10 1 10))

(add-obj
 "Point2"
 :light
 (blink-light LightType/Point 3200 (Color/green) 2)
 (v3 -10 1 -10))

(add-obj
 "Point3"
 :light
 (blink-light LightType/Point 3200 (Color/yellow) 2)
 (v3 10 1 -10))

(rem-obj "Spot")

; vfx

(defn add-fire [obj]
  (let [vfxc (cmpt+ obj VisualEffect)]
    (set! (.visualEffectAsset vfxc) (Resources/Load "VFX/Fire"))))

; add timeout to trigger fall apart effect - fall apart effect triggers destroy
; pare down to no lights? Gotta have something to fill it up
; 

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

(defn add-agent-vfx-col [obj]
  (set! (.visualEffectAsset (cmpt+ obj VisualEffect)) (Resources/Load "VFX/AgentCollision"))
  (role+ obj :agent-vfx-col on-collision)
  (update-state 
   obj 
   :agent-vfx-col 
   #(assoc 
     % :fn 
     (fn [obj col] 
       (.SendEvent (cmpt obj VisualEffect) "OnPlay")))))

