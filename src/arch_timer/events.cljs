(ns arch-timer.events
  (:require [arch-timer.sound :as sound]
            [arch-timer.config :as c]))

(defn on-timer
  "Actions to execute when the timer fires."
  []
  (cond
    (= (:readycounter @c/app-state) (:readytime @c/app-state))
    (let [{:keys [running]} @c/app-state]
      (if-not running
        (do
          (sound/beep-repeat 1)
          (c/set-app :running true)))
      (if running
        (swap! c/app-state update-in [:counter] inc)))

    :else (swap! c/app-state update-in [:readycounter] inc)))

(defn on-heartbeat
  []
  (c/set-app :timestamp (.. (js/Date.) toTimeString)))