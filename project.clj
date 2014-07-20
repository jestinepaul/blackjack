(defproject com.jestinepaul.blackjack/blackjack "0.1.0"
  :description "An implementation of blackjack written in clojure."
  :url "https://github.com/jestinepaul/blackjack"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [jline "2.12"]]
  :main ^:skip-aot com.jestinepaul.blackjack.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
