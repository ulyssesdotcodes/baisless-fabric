(ns game.scratch
  (use arcadia.core arcadia.linear hard.core game.core game.names game.personalities game.cam)
  (:import [UnityEngine Physics
            GameObject Input
            Vector2 Mathf Resources Transform
            PrimitiveType Collider Light Renderer
            Color Application Debug Time Canvas LightType
            Quaternion Rigidbody Camera]
           [UnityEngine.Experimental.VFX VisualEffect VFXEventAttribute]
           [UnityEngine.Rendering Volume]
           [UnityEngine.Experimental.Rendering.HDPipeline HDAdditionalLightData]
           [UnityEngine.UI Text]
           RectTransformUtility
           BaseAgent))

(use 'game.core :reload)
(use 'game.names :reload)
(use 'game.personalities :reload)
(use 'game.cam :reload)

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
(deinit)
(do
  (add-personality "Ursula" "FindEachother/HelperBlue")
  (add-personality "Jack" "FindEachother/HelperRed"))

(add-personality "Test" "DanceFloor/Mover")

(map #(add-personality % "DanceFloor/Mover") (take 3 (repeatedly #(randomname)))) 
(map #(add-personality % "DanceFloor/Speedster") (take 3 (repeatedly #(randomname)))) 
(map #(add-personality % "DanceFloor/Spacer") (take 3 (repeatedly #(randomname)))) 
(map #(add-personality % "DanceFloor/Attractor") (take 3 (repeatedly #(randomname)))) 
(map #(add-personality % "DanceFloor/Avoider") (take 3 (repeatedly #(randomname)))) 

(map #(add-personality % "graffiti/Blue") (take 2 repeatedly #(randomname)))) 

(map #(add-personality % "graffiti/Red") (take 2 (repeatedly #(randomname)))) 

(map #(add-personality % "graffiti/Blue") (take 2 (repeatedly #(randomname)))) 


(do
  (add-personality (randomname) "FindeachotherBlue")
  (add-personality (randomname) "FindeachotherRed"))

(map #(add-personality % "GraffitiBlue") (take 10 (repeatedly #(randomname)))) 
(map #(add-personality % "GraffitiRed") (take 10 (repeatedly #(randomname)))) 

(map #(add-personality % "Avoider") (take 20 (repeatedly #(randomname))))

(init)
(map #(add-personality % "Attractor") (take 20 (repeatedly #(randomname))))

(doseq [[k v] @bfstate] (prn k)])

(doseq [k (keys @bfstate)] (prn k)) 

(doseq [k (keys @bfstate)] 
  (when (= (@bfstate k) :actor) (cmpt (get-in @bfstate [k :item])BaseAgent)))



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

; target
(map #(add-personality % "Mover") (take 20 (repeatedly #(randomname)))) 

(dotimes [n 32] (add-target))

(defn add-target []
  (add-obj
    :uniq
    :target
    (let [obj (instantiate-in-area (Resources/Load "Prefabs/Target"))]
      (if (< (?f) 0.5) (set! (.tag obj) "bluetarget") (set! (.tag obj) "redtarget"))
       obj)
    (v3 (?f -32 32) 1 (?f -32 32))))

(defn add-wall []
  (add-obj
    :uniq
    :target
    (let [obj (instantiate-in-area (Resources/Load "Prefabs/Target"))]
      (if (< (?f) 0.5) (set! (.tag obj) "bluetarget") (set! (.tag obj) "redtarget"))
       obj)
    (v3 (?f -32 32) 1 (?f -32 32))))

(dotimes [n 16] (add-cubewall))
(dotimes [n 7] (add-rectwall))

(dotimes [n 60] (add-column))


(defn add-cubewall []
  (add-obj
    :uniq
    :wall
    (instantiate-in-area (Resources/Load "Prefabs/CubeWall"))
    (v3 (?f -32 32) 1 (?f -32 32))))

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

(add-fire 

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

(init)
(map #(add-personality % "Attractor") (take 10 (repeatedly #(randomname))))

(main-cam)

(get-obj "Aura")

(follow-cam "Diamond")

(unfollow-cam)

(rem-type :acctor)

(child+ (get-obj "Aura") (main-cam))
(child- (get-obj "Aura") (main-cam))

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

