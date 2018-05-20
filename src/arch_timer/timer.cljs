(ns arch-timer.timer
  (:require [arch-timer.sound :as sound]
            [arch-timer.config :as c]))

(defn start-check
  [{:keys [running] :as cur-state} start-fn]
  #_(let [a 'a]
    (if-not running (start-fn))))

(defn stop-check
  [{:keys [runtime counter running] :as cur-state} stop-fn]
  (let [remaining (- runtime counter)]
    (when (<= remaining 0)
      (if running (sound/stop))
      (stop-fn))))

(defn on-timer
  []
  (let [{:keys [counter runtime readytime readycounter running config timestamp] :as cur-state} @c/app-state]
    (cond
      (= readycounter readytime)
      (let [{:keys [running]} @c/app-state]
        (if-not running
          (do
            (sound/start)
            (c/set-app :running true)))
        (if running (c/update-app :counter inc)))

      :else
      (c/update-app :readycounter inc))))

(defn get-style
  [remaining running]
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