(ns game.events
  (use arcadia.core arcadia.linear hard.core)
  (:import [QuarkEvent System.Collections 
            CljQuarkEventListener])

(def eventlistener (cmpt (object-named "Area") CljQuarkEventListener))

(defrole event-passer
  :state { :listeners {} }
  (update [obj k]
    (let [listeners ((state obj k) :listeners)
          queue (.EventQueue eventlistener)]
      (when (> (.Count queue) 0)
        (let [event (queue/Dequeue)
              id-listeners (get (.Id event) :listeners '())
              global-listeners (get -1 :listeners '())]
          (doseq [listener listeners] 
            (conj (state listener :events) e))
          (doseq [listener global-listeners] 
            (conj (state listener :events) e)))))))

(add-listener 
  [passer obj id]
  (update-state 
    passer :listeners 
    (fn [listeners] (update listeners id (-> conj obj)))))
