(ns game.events
  (use arcadia.core arcadia.linear hard.core game.core)
  (:import [UnityEngine Debug]
    QuarkEvent 
    CljQuarkEventListener
    QuarkEvents
    QuarkEventType
    TransformEvent ResetEvent))

(def eventlistener (cmpt (object-named "Area") CljQuarkEventListener))
(def render-area (object-named "RenderedScene"))

(defn init-event-passer []
  (let [obj (new GameObject "EventPasser")]
    (role+ 
      obj
      :event-passer
      { :state { :listeners {} }
      :update (fn [obj k]
        (let [listeners ((state obj k) :listeners)
              queue (.EventQueue eventlistener)]
          (while (> (.Count queue) 0)
            (let [event (.Dequeue queue)
                  id-listeners (get listeners (.Id event) '())
                  global-listeners (get listeners -1 '())]
              (doseq [listener (concat id-listeners global-listeners)] 
                (listener event))))))
      })
  (add-obj :event-passer :meta obj)))

(defn add-event-listener 
  [id efn]
  (update-state 
    (get-obj :event-passer) 
    :event-passer 
    (fn [passer]
      (update passer :listeners 
        (fn [listeners]
          (Debug/Log id)
          (update listeners id #(if (empty? %) (set [efn]) (conj % efn)))))))
  efn)

(defn remove-event-listener
  [id efn]
  (update-state
    (get-obj :event-passer)
    :event-passer
    (fn [passer]
      (update passer :listeners 
        (fn [listeners]
          (update listeners id (fn [ls] (remove #(= efn %) ls))))))))

(defn clear-event-listeners [] (update-state (get-obj :event-passer) :event-passer
  (fn [passer] (assoc passer :listeners {}))))


(defn create-event-listener [source id fns]
  (let [obj (instantiate source)
        efns (map (fn [efn] (add-event-listener id #(efn obj %))) fns)]
    (role+ obj :event-listener
      { 
       :state { :fns efns }
       :destroy (fn [obj k] (map #(remove-event-listener id %) (:fns (state obj k))))
      })
    (parent! obj render-area)
    (add-obj (str "Puppet" id) :puppet obj)))

(defn create-creator [creator-name source obj-name fns]
  (let [obj (new GameObject (str "Creator(" obj-name ")"))
        efn (add-event-listener 
              -1
              (fn [e]
                (when 
                   (and 
                     (= (.Type e) (enum-val QuarkEventType "Create"))
                     (= (.Name e) obj-name))
                   (create-event-listener 
                     source
                     (.Id e) 
                     ((state obj :creator) :efns))
                   )))]

    (role+ 
      obj :creator 
      {
       :state { :efns fns }
       :destroy (fn [obj k] (remove-event-listener efn))
      }
    )
    (add-obj creator-name :creator obj)))


; (defn add-creator [source name obj-name]
;   (let [obj (new GameObject "Creator")]
;     (role+ obj :event-listener 
;       {:state {:event-fns {}}
;        :update (fn [obj k] 
;            (let [events ((state obj k) :events)]
;              (doseq [e events] 
;                (when 
;              (state+ obj k { :events '() })))
;       })
;     (add-listener obj -1)
;     (add-obj name :creator obj)))

; (defn add-event-obj [id source]
;   (when (not (has-obj (str id)))
;     (let [obj (instantiate source)]
;       (role+ obj :event-listener 
;         {:state 
;            { :eventfn 
;               (fn [obj k] 
;                (let [kstate (state obj k)
;                      events (kstate :events)
;                      event-fns (kstate :eventfns)]
;                  (doseq [e events]
;                    (when (contains? event-fns (.Type e))
;                      ((event-fns obj (.Type e)))))
;                  (state+ obj k { :events '() })))
;             }
;          :destroy (fn [obj k] (remove-listener obj id))
;         })
;       (add-listener obj id)
;       (parent! obj render-area)
;       (set! (.localPosition (.transform obj)) (v3 0 0 0))
;       (set! (.localScale (.transform obj)) (v3 4))
;       (set! (.rotation (.transform obj)) (euler (v3 30 180 0)))
;       (add-obj (str id) :skull obj))))


; (defn add-passer []
;   (let [ obj (new GameObject "Passer")]
;     (add-obj :passer :meta obj)
;     (role+ obj :event-passer event-passer)))

; (defn add-logger []
;   (let [obj (new GameObject "Logger")]
;     (add-listener obj -1)
;     (role+ obj :event-listener 
;       {:state {:events '()}
;        :update (fn [obj k] 
;            (let [events ((state obj k) :events)]
;              (doseq [e events] (Debug/Log (.Type e)))
;              (state+ obj :events '())))
;       })
;     (add-obj :logger :meta obj)))

; (defn add-event-fn [obj ty efn]
;   (update-state 
;     obj :event-listener
;     (fn [estate] 

;   (-> (state obj :event-listener)
;       update-state 
;   (update 
;     event-fns (enum-val QuarkEventType ty)
;     #(if (nil? %) newfn (comp newfn %))))


