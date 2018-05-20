(ns arch-timer.timer
  (:require [arch-timer.sound :as sound]
            [arch-timer.config :as c]))

(defn stop-check []
  (if (<= remaining 0) (do (if running (sound/beep-repeat 3)) (nil #_controls/stop))))

(defn on-timer []
  (let [{:keys [readycounter readytime]} @c/app-state]
    (cond
      (= readycounter readytime)
      (let [{:keys [running]} @c/app-state]
        (if-not running
          (do
            (sound/beep-repeat 1)
            (c/set-app :running true)))
        (if running (c/update-app :counter inc)))

      :else
      (c/update-app :readycounter inc))))

(defn get-style [remaining running]
  (cond
    (not running) {:background-color "red" :color "white"}

    :else
    (let [{:keys [runtime readytime]} @c/app-state
          colors [{:v runtime :background-color "green"}
                  {:v 20 :background-color "yellow" :color "black"}
                  {:v 15 :background-color "orange"}
                  {:v 0 :background-color "red"}]

          selected (last (filter #(<= remaining (:v %)) colors))
          {:keys [background-color color]
           :or {background-color "pink" color "white"}} selected]
      {:background-color background-color
       :color color})))