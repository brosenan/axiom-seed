(ns my-app.core-test
  (:require [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [my-app.core :as app]))

(deftest a-test
  (is (= 1 2)))

