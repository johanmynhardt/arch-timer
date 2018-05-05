(ns arch-timer.config
  (:require [arch-timer.events :as events]))

(def default-config (atom {:timer nil
                           :heartbeat (js/setInterval events/on-heartbeat 1000)
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

(defn set-app [key value]
  (swap! app-state assoc-in [key] value))