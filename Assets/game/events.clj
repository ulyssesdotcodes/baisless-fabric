(ns game.events
  (use arcadia.core arcadia.linear hard.core)
  (:import [QuarkEvent])

(def listeners (atom {}))

(def eventsystem (object-named "Area"))

(defn init-events []
  ())

(defrole on-event
  :state { :listeners: () }
  (update
    [obj k]
    let 
