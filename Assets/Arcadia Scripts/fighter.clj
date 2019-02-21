(ns fighter-tutorial.core
  (:use arcadia.core arcadia.linear)
  (:import [UnityEngine Collider2D Physics
            GameObject Input Rigidbody2D
            Vector2 Mathf Resources Transform
            Collision2D Physics2D]
           Spawner
           ArcadiaState)
  )

(defn add-obj [n]
  (.. (cmpt (object-named "Spawner") Spawner) SpawnTriggers
   (Add (Resources/Load (str "ScriptableObjects/SpawnTriggers/" n)))))

(defn clear []
  (.. (cmpt (object-named "Spawner") Spawner) SpawnTriggers
   (Clear)))

(ns fighter-tutorial.core)
(cmpt (object-named "Spawner") Spawner)

(ns fighter-tutorial.core)
(object-named "Spawner")

(object-named "Spawner")

(ns fighter-tutorial.core)

(add-obj "InvertRepeating")

(clear)




; (defonce CLONED (atom []))

; (defn resource [s] (UnityEngine.Resources/Load s))

; (defn clear-cloned! []
;   (dorun (map retire @CLONED))
;   (reset! CLONED []))

; (defmacro clone! [kw]
;   (if (keyword? kw)
;     (let [source (clojure.string/replace (subs (str kw) 1) #"[:]" "/")]
;       `(let [^UnityEngine.GameObject source# (~'UnityEngine.Resources/Load ~source)
;              ^UnityEngine.GameObject gob# (~'UnityEngine.GameObject/Instantiate source#)]
;          (~'set! (.name gob#) (.name source#))
;          (swap! ~'CLONED #(cons gob# %))
;          gob#))
;     `(-clone! ~kw)))

; (defn ^UnityEngine.GameObject -clone!
;   ([ref] (-clone! ref nil))
;   ([ref pos]
;      (when-let [^UnityEngine.GameObject source
;                 (cond  (string? ref)  (resource ref)
;                        (keyword? ref) (resource (clojure.string/replace (subs (str ref) 1) #"[:]" "/"))
;                        :else nil)]

;        (let [pos   (or pos (.position (.transform source)))
;              quat  (.rotation (.transform source))
;              ^UnityEngine.GameObject gob   (arcadia.core/instantiate source pos quat)]
;          (set! (.name gob) (.name source))
;          (swap! CLONED #(cons gob %)) gob))))





;; (defn rm-obj []
;;   (.. (cmpt (object-named "Spawner") Spawner) SpawnTriggers
;;       (Remove (Resources/Load "ScriptableObjects/SpawnTriggers/Cube")))

; (defn start-game [])

; (start-game)
