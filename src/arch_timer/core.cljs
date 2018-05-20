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
  (let [{:keys [counter runtime readytime readycounter running config timestamp] :as cur-state} (rum/react app-state)
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
        set-app-on-value-change (fn [key] #(c/set-app key (-> (.. % -target -value) js/parseInt)))
        vt-text (if running remaining readyleft)
        modal-hidden (not config)]

    ;; NON-VISUAL Changes
    (timer/start-check cur-state controls/start)
    (timer/stop-check cur-state controls/stop)

    [:div
     [:div
      (map hydrate-button buttons)]
     [:div.vt {:style color}
      [:div vt-text]]
     [:audio#beep {:controls true
                   :style {:display "none"}}
      [:source {:src "audio/beep.ogg"}]]
     [:h4 timestamp]

     ; Modal Config
     [:div#config {:hidden modal-hidden}
      [:dl
       [:dt [:label "ready time"]]
       [:dd [:input {:type "number"
                     :value readytime
                     :on-change (set-app-on-value-change :readytime)}]]

       [:dt [:label "runtime"]]
       [:dd [:input {:type "number"
                     :value runtime
                     :on-change (set-app-on-value-change :runtime)}]]]

      [:div]
      [:button {:on-click #(c/set-app :config false)} "OK"]]]
    ))

(defn mountroot! [el-id]
  (rum/hydrate (root c/app-state) (js/document.getElementById el-id)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (mountroot! "app"))

(mountroot! "app")
