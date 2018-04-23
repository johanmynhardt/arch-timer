(ns arch-timer.core
    (:require [rum.core :as rum]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce config (atom {:timer nil
                       :readytime 5
                       :readycounter 0
                       :runtime 25
                       :running false
                       :counter 0}
                      :config true))

(defonce app-state (atom @config))

(defn on-timer []
  ;(println (str "app-state: " @app-state))

  (cond
   (= (:readycounter @app-state) (:readytime @app-state))
   (do
     (let [{:keys [running]} @app-state]
       (if-not running
         (swap! app-state assoc-in [:running] true))
       (if running
         (swap! app-state update-in [:counter] inc))))

   :else (swap! app-state update-in [:readycounter] inc)))

(defn- audio []
  (let [audio (js/document.querySelector "audio")]
    audio))

(defn- init-audio []
  (set! (.-volume (audio)) 0)
  (.. (audio) play)
  (js/setTimeout #(set! (.-volume (audio)) 1) 1000))


(defn beep! []
  (let []
    (.. (audio) play)))

(defn start []
  (init-audio)
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
  (reset! app-state (merge @app-state @config {:timer (:timer @app-state)})))

(defn set-config [key value]
  (swap! config assoc-in [key] value))

(defn configure! []
  (stop)
  (set-config :config true)
  (reset))

(defn get-style [remaining running]
  (cond
   (not running) {:background-color "red" :color "white"}

   :else 
   (let [{:keys [runtime readytime]} @app-state
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
        _ (if (<= remaining 0) (do (beep!) (stop)))]
    [:div
     [:div
      [:button {:on-click start} "start"]
      [:button {:on-click stop} "stop"]
      [:button {:on-click reset} "reset"]
      [:button {:on-click configure!} "configure"]
      [:button {:on-click beep!} "beep!"]]
     [:div.vt {:style color}
      [:div {:hidden (not running)} (str remaining)]
      [:div {:hidden running} (str readyleft)]]
     [:audio#beep {:controls true
                   :style {:display "none"}}
      [:source {:src "audio/beep.ogg"}]]
     [:h4 (str (js/Date.))]
     [:div#config {:hidden (not config)}
      [:dl
       [:dt [:label "ready time"]]
       [:dd [:input {:type "number"
                      :value readytime
                      :on-change #(set-config
                                   :readytime
                                   (->
                                    (.. % -target -value)
                                    js/parseInt))}]]

       [:dt [:label "runtime"]]
       [:dd [:input {:type "number"
                      :value runtime
                      :on-change #(set-config
                                   :runtime
                                   (->
                                    (.. % -target -value)
                                    js/parseInt))}]]]

      
      [:div]
      [:button {:on-click (fn [_]
                            (set-config :config false)
                            (reset))}
       "OK"]]]
    ))

(defn mountroot! [el-id]
  (rum/hydrate (root app-state) (js/document.getElementById el-id)))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  (mountroot! "app"))

(mountroot! "app")
