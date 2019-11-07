(ns nedap.one.test-runner
 (:require
  [cljs.nodejs :as nodejs]
  [nedap.utils.test.api :refer-macros [run-tests]]
  [unit.nedap.one.api]))

(nodejs/enable-util-print!)

(defn -main []
  (run-tests
   'unit.nedap.one.api))

(set! *main-cli-fn* -main)
