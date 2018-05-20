(ns arch-timer.controls
  (:require [arch-timer.config :as c]
            [arch-timer.sound :as sound]
            [arch-timer.timer :as timer]))

(defn reset
  "Reset the app-state to the initial state, ready for the next run."
  []
  (c/reset))

(defn start
  "Start the timer."
  []
  (reset)
  (sound/start)
  (let [timer (:timer @c/app-state)
        _ (println "timer: " timer)]
    (if-not timer
      (do
        (println (str "Starting timer with config: " @c/app-state))
        (c/set-app :timer (js/setInterval timer/on-timer 1000))))))

(defn stop
  "Stop the timer and reset states."
  []
  (let [timer (:timer @c/app-state)]
    (if timer (do
                (println "Stopping timer.")
                (js/clearInterval timer)
                (c/set-app :timer nil)
                (c/set-app :running false)))))
