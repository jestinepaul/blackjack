(ns com.jestinepaul.blackjack.main
  (:require [com.jestinepaul.blackjack.core :refer [start-round]]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:gen-class))


(defn print-blackjack []
  (println (str
             "    _______   __                      __                                    __\n"
             "   /       \\ /  |                    /  |                                  /  |\n"
             "   $$$$$$$  |$$ |  ______    _______ $$ |   __      __   ______    _______ $$ |   __\n"
             "   $$ |__$$ |$$ | /      \\  /       |$$ |  /  |    /  | /      \\  /       |$$ |  /  |\n"
             "   $$    $$< $$ | $$$$$$  |/$$$$$$$/ $$ |_/$$/     $$/  $$$$$$  |/$$$$$$$/ $$ |_/$$/\n"
             "   $$$$$$$  |$$ | /    $$ |$$ |      $$   $$<      /  | /    $$ |$$ |      $$   $$<\n"
             "   $$ |__$$ |$$ |/$$$$$$$ |$$ \\_____ $$$$$$  \\     $$ |/$$$$$$$ |$$ \\_____ $$$$$$  \\\n"
             "   $$    $$/ $$ |$$    $$ |$$       |$$ | $$  |    $$ |$$    $$ |$$       |$$ | $$  |\n"
             "   $$$$$$$/  $$/  $$$$$$$/  $$$$$$$/ $$/   $$/__   $$ | $$$$$$$/  $$$$$$$/ $$/   $$/\n"
             "                                             /  \\__$$ |\n"
             "                                             $$    $$/\n"
             "                                              $$$$$$/\n"
             "       *---*  *---*\n"
             "       | A |  | J |\n"
             "       | ♥ |  | ♦ |\n"
             "       *---*  *---*\n"
             )))

(def cli-options
  [["-c" "--chips CHIPS" "Number of chips for the player to start the game"
    :default 100
    :parse-fn #(Integer/parseInt %)
    :validate [#(<= 100 % 1000) "Must be a number between 100 and 1000"]]
   ["-h" "--help"]])

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn usage [options-summary]
  (->> ["Welcome to blackjack!!"
        ""
        "Usage: java -jar blackjack.jar [options]"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn -main
  [& args]
  (print-blackjack)

  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 0) (exit 1 (usage summary))
      errors (exit 1 (error-msg errors)))
    (start-round (:chips options))))