(ns arch-timer.controls
  (:require [arch-timer.config :as c]
            [arch-timer.sound :as sound]
            [arch-timer.events :as events]))

(defn reset
  "Reset the app-state to the initial state, ready for the next run."
  []
  (c/reset))

(defn start
  "Start the timer."
  []
  (reset)
  (sound/beep-repeat 2)
  (let [timer (:timer @c/app-state)
        _ (println "timer: " timer)]
    (if-not timer
      (do
        (println (str "Starting timer with config: " @c/app-state))
        (swap! c/app-state assoc-in [:timer] (js/setInterval events/on-timer 1000))))))

(defn stop
  "Stop the timer and reset states."
  []
  (let [timer (:timer @c/app-state)]
    (if timer (do
                (println "Stopping timer.")
                (js/clearInterval timer)
                (swap! c/app-state assoc-in [:timer] nil)
                (swap! c/app-state assoc-in [:running] false)))))
