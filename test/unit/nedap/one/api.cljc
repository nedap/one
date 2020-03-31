(ns unit.nedap.one.api
  (:require
   #?(:clj [clojure.test :refer [deftest testing are is use-fixtures]] :cljs [cljs.test :refer-macros [deftest testing is are] :refer [use-fixtures]])
   [nedap.one.api :as sut]))

#?(:cljs (goog-define ^boolean ASSERT true))

(deftest basic
  (are [input expected] (testing input
                          (= expected
                             input))
    (sut/one
      1 1)     1

    (sut/one
      false 1
      true  2) 2

    (sut/one
      false 1
      true  2
      nil   3) 2

    (sut/one
      false 1
      nil   2
      true  3) 3))

(when #?(:clj  *assert*
         :cljs ASSERT)
  (deftest condition-side-effects
    (testing "Side-effects in the conditions are evaluated exactly once, and for all conditions"
      (are [input expected] (let [proof (atom [])]
                              (input
                               (do
                                 (swap! proof conj :first)
                                 false) 1
                               (do
                                 (swap! proof conj :second)
                                 true)  2
                               (do
                                 (swap! proof conj :third)
                                 false) 3)

                              (= expected @proof))
        sut/one [:first :second :third]
        cond    [:first :second]))))

(when #?(:clj  *assert*
         :cljs ASSERT)
  (deftest consequence-side-effects
    (testing "Only the 'chosen' consequence is executed, and it is exactly once"
      (let [proof (atom [])]
        (sut/one
          false (swap! proof conj :first)
          true  (swap! proof conj :second)
          nil   (swap! proof conj :third))

        (is (= [:second]
               @proof))))))
