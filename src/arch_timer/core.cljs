(ns arch-timer.core
  (:require [rum.core :as rum]
            [arch-timer.config :as c]
            [arch-timer.sound :as sound]
            [arch-timer.controls :as controls]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload


(defn configure! []
  (controls/stop)
  (c/set :config true)
  (controls/reset))

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


(rum/defc root < rum/reactive [app-state]
  (let [{:keys [counter runtime readytime readycounter running config]} (rum/react app-state)
        remaining (- runtime counter)
        readyleft (- readytime readycounter)
        color (get-style remaining running)
        _ (if (<= remaining 0) (do (if running (sound/beep-repeat 3)) (controls/stop)))
        ]
    [:div
     [:div
      [:button {:on-click controls/start} "start"]
      [:button {:on-click controls/stop} "stop"]
      [:button {:on-click controls/reset} "reset"]
      [:button {:on-click configure!} "configure"]
      [:button {:on-click sound/beep!} "beep!"]]
     [:div.vt {:style color}
      [:div {:hidden (not running)} (str remaining)]
      [:div {:hidden running} (str readyleft)]]
     [:audio#beep {:controls true
                   :style {:display "none"}}
      [:source {:src "audio/beep.ogg"}]]
     [:h4 (.. (js/Date.) toTimeString)]

     ; Modal Config
     [:div#config {:hidden (not config)}
      [:dl
       [:dt [:label "ready time"]]
       [:dd [:input {:type "number"
                     :value readytime
                     :on-change #(c/set
                                   :readytime
                                   (->
                                     (.. % -target -value)
                                     js/parseInt))}]]

       [:dt [:label "runtime"]]
       [:dd [:input {:type "number"
                     :value runtime
                     :on-change #(c/set
                                   :runtime
                                   (->
                                     (.. % -target -value)
                                     js/parseInt))}]]]


      [:div]
      [:button {:on-click (fn [_]
                            (c/set :config false)
                            (controls/reset))}
       "OK"]]]
    ))

(defn mountroot! [el-id]
  (rum/hydrate (root c/app-state) (js/document.getElementById el-id)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (mountroot! "app"))

(mountroot! "app")
