;;   Copyright (c) Leon Grapenthin.  All rights reserved.
;;   You must not remove this notice, or any other, from this software.

(ns inertic.impl.js-clock
  (:require
   [inertic.impl.protocols :as p]))

(defrecord JSClock [timers]
  p/Clock
  (now [this] (.now js/Date))
  (schedule [this t fn0 id-fn]
    (let [dela (- t (p/now this))
          h-a (atom nil)
            h (js/setTimeout (fn [_] (fn0 @h-a)) (max 0 dela))]
      (reset! h-a h)
      (when id-fn (id-fn h))
      (swap! timers conj h)
      h))
  (cancel [this sched]
    (when (@timers sched)
      (swap! timers disj sched)
      (js/clearTimeout sched)
      true))
  (stop [this]
    (run! #(p/cancel this %) @timers)
    (reset! timers #{})))

(defn timeout-clock []
  (->JSClock (atom #{})))
