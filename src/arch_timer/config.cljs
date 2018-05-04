(ns arch-timer.config)

(def default-config (atom {:timer nil
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
  (reset! app-state (merge @app-state @default-config {:timer (:timer @app-state)})))

(defn set [key value]
  (swap! default-config assoc-in [key] value))
