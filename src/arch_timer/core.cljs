(ns arch-timer.core
    (:require [rum.core :as rum]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:timer nil
                          :runtime 120
                          :counter 1}))

(defn on-timer []
  (swap! app-state update-in [:counter] inc))

(defn start []
  (let [timer (:timer @app-state)
        _ (println "timer: " timer)]
    (if-not timer
      (do
        (println "Starting timer...")
        (swap! app-state assoc-in [:timer] (js/setInterval on-timer 1000))))))

(defn stop []
  (let [timer (:timer @app-state)]
    (if timer (do
                (println "Stopping timer.")
                (js/clearInterval timer)
                (swap! app-state assoc-in [:timer] nil)))))

(defn reset []
  (swap! app-state assoc-in [:counter] 0))

(rum/defc root < rum/reactive [app-state]
  (let [{:keys [counter runtime]} (rum/react app-state)
        remaining (- runtime counter)
       
        _ (if (<= remaining 10) (stop))]
    [:div
     [:div
      [:button {:on-click start} "start"]
      [:button {:on-click stop} "stop"]
      [:button {:on-click reset} "reset"]]
     [:div.vt (str remaining)]
     [:h4 (str (js/Date.))]]
    ))

(defn mountroot! [el-id]
  (rum/hydrate (root app-state) (js/document.getElementById el-id)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (mountroot! "app"))

(mountroot! "app")
