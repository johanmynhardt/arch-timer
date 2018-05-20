(ns arch-timer.core
  (:require [rum.core :as rum]
            [arch-timer.config :as c]
            [arch-timer.sound :as sound]
            [arch-timer.controls :as controls]
            [arch-timer.timer :as timer]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload


(defn configure! []
  (controls/stop)
  (c/set-app :config true)
  (controls/reset))

(rum/defc root < rum/reactive [app-state]
  (let [{:keys [counter runtime readytime readycounter running config timestamp]} (rum/react app-state)
        remaining (- runtime counter)
        readyleft (- readytime readycounter)
        color (timer/get-style remaining running)
        buttons [{:value "Start" :fn controls/start}
                 {:value "Stop" :fn controls/stop}
                 {:value "Reset" :fn controls/reset}
                 {:value "Configure" :fn configure!}
                 {:value "Beep!" :fn sound/beep!}]
        hydrate-button (fn [{:keys [fn value] :as btn}]
                         [:button {:on-click fn :key value} value])
        ]
    (if (<= remaining 0) (do (if running (sound/beep-repeat 3)) (controls/stop)))

    [:div
     [:div
      (map hydrate-button buttons)]
     [:div.vt {:style color}
      [:div (if running remaining readyleft)]]
     [:audio#beep {:controls true
                   :style {:display "none"}}
      [:source {:src "audio/beep.ogg"}]]
     [:h4 timestamp]

     ; Modal Config
     [:div#config {:hidden (not config)}
      [:dl
       [:dt [:label "ready time"]]
       [:dd [:input {:type "number"
                     :value readytime
                     :on-change #(c/set-app
                                   :readytime
                                   (->
                                     (.. % -target -value)
                                     js/parseInt))}]]

       [:dt [:label "runtime"]]
       [:dd [:input {:type "number"
                     :value runtime
                     :on-change #(c/set-app
                                   :runtime
                                   (->
                                     (.. % -target -value)
                                     js/parseInt))}]]]


      [:div]
      [:button {:on-click (fn [_]
                            (c/set-app :config false)
                            ;(controls/reset)
                            )}
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
