(defproject com.nedap.staffing-solutions/one "0.1.0-alpha1"
  ;; Please keep the dependencies sorted a-z.
  :dependencies [[com.nedap.staffing-solutions/utils.modular "0.3.0"]
                 [com.nedap.staffing-solutions/utils.spec "0.7.1-alpha2"]
                 [com.stuartsierra/component "0.4.0"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/test.check "0.10.0-alpha3"]]

  :description "A safer cond"

  :url "http://github.com/nedap/one"

  :min-lein-version "2.0.0"

  :license {:name "EPL-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :signing {:gpg-key "servicedesk-PEP@nedap.com"}

  :repositories {"releases" {:url      "https://nedap.jfrog.io/nedap/staffing-solutions/"
                             :username :env/artifactory_user
                             :password :env/artifactory_pass}}

  :deploy-repositories [["releases" {:url "https://nedap.jfrog.io/nedap/staffing-solutions/"}]]

  :repository-auth {#"https://nedap.jfrog\.io/nedap/staffing-solutions/"
                    {:username :env/artifactory_user
                     :password :env/artifactory_pass}}

  :target-path "target/%s"

  :monkeypatch-clojure-test false

  :plugins [[lein-cljsbuild "1.1.7"]]

  :repl-options {:init-ns dev}

  ;; Please don't add `:hooks [leiningen.cljsbuild]`. It can silently skip running the JS suite on `lein test`.
  ;; It also interferes with Cloverage.

  :cljsbuild {:builds {"test" {:source-paths ["src" "test"]
                               :compiler     {:main          nedap.one.test-runner
                                              :output-to     "target/out/tests.js"
                                              :output-dir    "target/out"
                                              :target        :nodejs
                                              :optimizations :none}}}}

  :profiles {:dev      {:dependencies [[cider/cider-nrepl "0.16.0" #_"formatting-stack needs it"]
                                       [com.clojure-goes-fast/clj-java-decompiler "0.2.1"]
                                       [criterium "0.4.4"]
                                       [formatting-stack "0.17.0"]
                                       [lambdaisland/deep-diff "0.0-29"]
                                       [org.clojure/tools.namespace "0.3.0-alpha4"]
                                       [org.clojure/tools.reader "1.1.1" #_"formatting-stack needs it"]]
                        :plugins      [[lein-cloverage "1.0.13"]]
                        :source-paths ["dev" "test"]}

             :provided {:dependencies [[org.clojure/clojurescript "1.10.520"]]}})
