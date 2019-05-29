(ns nedap.one.api
  (:require
   [nedap.utils.spec.api :refer [check!]]))

(defmacro one
  "Exactly like `#'cond`, but on `#'*assert*`, the following will be checked:

  * No duplicate conditions are contained
  * Exactly one condition is truthy.

  Helps avoiding sequential semantics, and related ambiguity
  (is a given `cond`? inherently or unintentionally sequential?).

  For said checking, all conditions are evaluated eagerly, so they should be side-effect free.
  Only the truthy condition's 'consequence' will be evaluated, and once."
  {:style/indent 0}
  [& clauses]
  {:pre [(check! even?                     (count clauses)
                 (partial apply distinct?) (->> clauses
                                                (partition 2)
                                                (map first)))]}
  (if-not *assert*
    `(cond ~@clauses)
    (let [pairs (partition 2 clauses)
          conditions (map first pairs)
          syms (->> conditions
                    (mapv (fn [_]
                            (gensym))))
          bindings (->> (interleave syms conditions)
                        (vec))
          consequences (map second pairs)
          check-form `(check! #{1} (->> ~syms
                                        (filter identity)
                                        (count)))
          cond-form (->> consequences
                         (interleave syms)
                         (cons `cond))]
      `(let ~bindings
         ~check-form
         ~cond-form))))
