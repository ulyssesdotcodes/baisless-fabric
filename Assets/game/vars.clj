(ns game.vars
  (use arcadia.core arcadia.linear)
  (:import Spawner))

(defonce CAMPAR (atom (object-named "CameraParent")))
(defonce PLAYER (atom (object-named "ybot@T-Pose")))
(defonce POOL (atom (.Pool (cmpt (object-named "Spawner") Spawner))))
(defonce SPAWNER (atom (cmpt (object-named "Spawner") Spawner)))