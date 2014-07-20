(ns com.jestinepaul.blackjack.card
  (:import [jline.console ConsoleReader]))


(def ^{:private true} shoe (ref '()))

(def pause-duration 1000)

(defn take-card
  "Removes a card from the dealing shoe. If the shoe is empty, reloads it with a new deck of cards."
  []
  (dosync
    (if (empty? @shoe)
      (do (println "Reloading shoe with a new deck of cards......")
          (Thread/sleep pause-duration)
          (ref-set shoe (into '() (shuffle
                                    (for [suite ["Club", "Diamond", "Heart", "Spade"]
                                          rank ["A", "2", "3", "4", "5", "6", "7", "8", "9",
                                                "10", "J", "Q", "K"]]
                                      {:rank rank
                                       :suite suite}))))))
    (let [first-card (first @shoe)]
      (alter shoe rest)
      first-card)))


(defn calculate-score
  "Calculate the total score of a hand. If the score is less than 11, then an A is counted as 11"
  [hand]
  (let [score (reduce (fn [sum card]
                        (let [rank (:rank card)]
                          (+ sum (cond
                                   (#{"J" "Q" "K"} rank) 10
                                   (= "A" rank) 1
                                   :else (Integer/parseInt rank)))))
                      0 hand)]
    (if (and (< score 12)
             (some #{"A"} (map :rank hand)))
      (+ score 10)
      score)))

(defn- print-card
  "Output one card"
  [card]
  (let [suite (case (:suite card)
                "Club"    "\u2667"
                "Diamond" "\u2666"
                "Spade"   "\u2664"
                "Heart"   "\u2665"
                " ")]
    (println (str "*---*"))
    (println (str "| " (:rank card) " |"))
    (println (str "| " suite " |"))
    (println (str "*---*"))))

(defn print-hands-with-dealer-hidden
  "Output both the hands with one of the dealer card turned down"
  [player-hand dealer-hand]
  (.clearScreen (ConsoleReader.))
  (println "Player")
  (doseq [card player-hand]
    (print-card card))
  (println)
  (println "Dealer")
  (print-card (first dealer-hand))
  (print-card {:rank " "})
  (println)
  (Thread/sleep pause-duration))

(defn print-hands
  "Output both the hands"
  [player-hand dealer-hand]
  (.clearScreen (ConsoleReader.))
  (println "Player")
  (doseq [card player-hand]
    (print-card card))
  (println)
  (println "Dealer")
  (doseq [card dealer-hand]
    (print-card card))
  (println)
  (Thread/sleep pause-duration))
