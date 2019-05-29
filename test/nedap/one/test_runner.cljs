(ns nedap.one.test-runner
 (:require
  [cljs.nodejs :as nodejs]
  [cljs.test :refer-macros [run-tests]]
  [unit.nedap.one.api]))

(nodejs/enable-util-print!)

(defn -main []
  (run-tests
   'unit.nedap.one.api))

(set! *main-cli-fn* -main)
