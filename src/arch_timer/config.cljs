(ns arch-timer.config)

(declare app-state)

(defn- on-heartbeat
  []
  (swap! app-state assoc-in [:timestamp] (.. (js/Date.) toTimeString)))

(def default-config (atom {:timer nil
                           :heartbeat (js/setInterval on-heartbeat 1000)
                           :readytime 5
                           :readycounter 0
                           :runtime 25
                           :running false
                           :counter 0
                           :config true}))


(defonce app-state (atom @default-config))

(defn reset
  "Reset the app-state to the initial state, ready for the next run."
  []
  (reset! app-state
    (merge
      @app-state
      @default-config
      (select-keys @app-state [:timer :config :runtime :readytime]))))

(defn set-app
  [key value]
  (let [ks (if (coll? key) key [key])]
    (swap! app-state assoc-in ks value)))

(defn update-app
  [key fn]
  (let [ks (if (coll? key) key [key])]
    (swap! app-state update-in ks fn)))
