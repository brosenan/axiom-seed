(ns my-app.core-test
  (:require [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [my-app.core :as app]
            [reagent-query.core :as rq]
            [axiom-cljs.core :as ax]))

(deftest edit-task
  (let [record (atom {:user "alice"
                      :task "do something"
                      :ts 1000})
        ui (app/edit-task (-> @record
                              (assoc :del! :my-del!)
                              (assoc :swap! (partial swap! record))))]
    (is (= (rq/query ui :div :input:value) ["do something"]))
    (is (= (rq/query ui :div :button) ["X"]))
    (is (= (rq/query ui :div :button:on-click) [:my-del!]))
    ;; The :input box's :on-change should update the :task field
    (let [[on-change] (rq/query ui :div :input:on-change)]
      (on-change (rq/mock-change-event "do something else")))
    (is (= (:task @record) "do something else"))))

(deftest task-list
  (let [host (ax/mock-connection "alice")
        ui (app/task-list host)]
    ;; The task list contains a button with the caption "New Task"
    (is (= (rq/query ui :div :button) ["New Task"]))
    ;; Pressing this button will create a new empty task item
    (let [[callback] (rq/query ui :div :button:on-click)]
      (callback)
      (let [ui (app/task-list host)]
        ;; A new :li element should be created with the key 0 (the timestamp returned by the mock host)
        (is (= (rq/query ui :div :ul :li:key) [0]))
        ;; and an empty :value in the :input box
        (is (= (rq/query ui :div :ul :li :div :input:value) [""]))))))
