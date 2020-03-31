# one

`nedap.one.api/one` is a  `clojure.core/cond` replacement, using its same exact syntax. It avoids problems with `cond`, `case`, `if` et al.

## Synopsis

As said, `one` has the same exact structure and semantics than `cond`. In fact when `clojure.core/*assert*` is false, it will macroexpand to a plain, unadorned `cond`.

> Consequently, `one` has zero cost in production.

What's the difference?

* All conditions are evaluated, and it is asserted that exactly one is true
  * If zero matched, we would incur into _implicit nil defaulting_.
  * If 2+ matched, we would incur into _sequential semantics_.

* Duplicate conditions are forbidden
  * e.g. `(cond 1 2, 1 2)` has the `1` condition duplicated. `clojure.core/cond` would happily compile that. 

## Rationale: analysis of the traditional Clojure alternatives

#### `cond` (and `condp`)

* Inherently sequential, which is an objective source of complexity.
* Often it is unclear if the developer actually intended the `cond` to have sequential semantics
  * i.e., can I reorder clauses freely? If I am to add a new one, should I do it in a specific position (to prevent bugs)?
* There's an implicit default clause returning `nil`.
  * This is undesirable: the intent of your logic should be exhaustively, explicitly listed. 

#### `case`

* Non-sequential: internally it is backed by a hashmap.
* Its dispatch mechanism is only intended to work with compile-time constants.
If attempting to use variables as the conditions, they will be interpreted as literal symbols, leading to opaque bugs:

```clojure
(let [a 2] (case 2 a :win)) ;; Error - no matching clause 
```

* There's no implicit default clause.

#### `if`

* Sequential when composed (nested `if`s).
* It doesn't scale gracefully: nested `if`s can get intrincate, while a `cond` or `case` are flat.
Flat code tends to be easier to understand/maintain.
* Code using it tends to assume there are exactly two cases, but tomorrow a third one might arise, creating a bug. Example:

```clojure
(if (= thing :blue)
  "Your eyes are blue"
  ;; Programmer thinking: it's not :blue, so it has to be :brown
  "Your eyes are brown")
;; let's say the programmer accounted for :blue and :brown cases, which are in fact
;; the two only possible cases _today_ in a given domain.
;; Tomorrow this code might break as requirements change (new colors are introduced), because it used `if`.
```

#### Hashmaps, multimethods

They are flat, non-sequential and have arbitrary dispatch (unlike `case`'s). However, they tend to be verbose/overkill for self-contained tasks: a single 4LOC defn should't become 1 defn plus _n_ multimethods.

Similarly, dispatching conditions->consequences via a hashmap is not a very usual pattern, nor a concise one.

#### Comparison

|                  | Sequential semantics? | Implicitly defaults to nil?  | Acceptable dispatch values | Extensible?             | Simplified dispatch cost | Simplified invocation cost |
|------------------|-----------------------|------------------------------|----------------------------|-------------------------|--------------------------|----------------------------|
| **cond**         | yes                   | yes                          | Expressions                | no                      | O(n)                     | Low                        | 
| **case**         | no                    | no                           | Compile-time constants     | no                      | O(1)                     | High                       | 
| **if**           | (yes, if nested)      | (yes, if no `else`)          | Expressions                | no                      | O(n)                     | Low                        | 
| **hashmaps**     | no                    | yes                          | Expressions                | yes (they can be merged)| O(1)                     | High                       | 
| **multimethods** | no                    | no                           | Expressions                | yes                     | O(1)                     | High                       | 
| **one**          | no                    | no                           | Expressions                | no                      | O(n)                     | Low                        | 

There's no clear winner (but there are clear losers, in terms of complexity: `cond`, `if`, `case`).

I would recommend `one` when no need for extensibility is foreseen, and keeping the code inline + straightforward would be a win in maintainability. 

## Installation

```clojure
[com.nedap.staffing-solutions/one "1.0.0"]
```

## ns organisation

Only `nedap.one.api` is meant for external consumption.

## Documentation

Please browse the public namespace, which is documented, speced and tested.

## FAQ

#### What's so wrong about sequential semantics?

Some quotes from _Simple Made Easy_:

> Syntax, interestingly, complects meaning and order often in a very unidirectional way. 

> Fold is a little bit more subtle because it seems like this nice, somebody else is taking care of it. But it does have this implication about the order of things, this left to right bit.

As one can observe, Rich repeatedly denounces sequentiality as a source of complexity.

#### Can't the conditions become more verbose/repetitive?

i.e., how is:

```clojure
(one
  x       1
  (not x) 1)
```

...better than...

```clojure
(if x
  1
  2)
```

...?

* Sometimes `one`'s repetition can make code clearer
  * particularly for 3+ clauses
  * or when there are delicate boolean variations (`not`, `or`, `and`) between clauses
  * The possible conditions are summarized to humans flatly, so they can be understood in a glance, similar to how tables tend to make info easier to grok. 
  * Merge conflicts become less likely
* If the duplication cost is too high, surround `one` with a `let`, extracting the repetitive parts. 

## IDE support

#### Emacs

```lisp
(put-clojure-indent 'one 0)
(add-to-list 'clojure-align-cond-forms "one")
```

## License

Copyright © Nedap

This program and the accompanying materials are made available under the terms of the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0).
