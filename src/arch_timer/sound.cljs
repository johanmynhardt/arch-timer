(ns arch-timer.sound)

(defn- audio
  "Finds and returns the audio element."
  []
  (js/document.querySelector "audio"))

(defn init-audio
  "Preload and plays the audio at mute volume - a requirement for mobile Chrome."
  []
  (set! (.-volume (audio)) 0)
  (.. (audio) play)
  (js/setTimeout #(set! (.-volume (audio)) 1) 1000))

(defn beep!
  "Trigger the audio beep."
  []
  (.. (audio) play))

(defn beep-repeat
  "Repeat the beep the number of requested times.
  
  Note that this is called recursively as a JavaScript Timeout is used."
  [times]
  (if (> times 0)
    (do
      (beep!)
      (js/setTimeout #(beep-repeat (dec times)) 1000))))

(defn reset
  []
  (beep-repeat 1))

(defn start
  []
  (beep-repeat 2))

(defn stop
  []
  (beep-repeat 3))
