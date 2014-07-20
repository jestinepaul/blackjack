(ns com.jestinepaul.blackjack.core
  (:require [com.jestinepaul.blackjack.card :refer :all])
  (:import [jline.console ConsoleReader] ))

(defn calculate-payout
  "Payout calculation if the player/dealer didn't go bust."
  [player-score dealer-score bet & {:keys [blackjack]}]
  (let [payout (cond
                 (> player-score dealer-score)
                 (if blackjack
                   (let [result (int (* 1.5 bet))]
                     (println (str "Blackjack!!! Won " result " chips.\n") )
                     result)
                   (do (println (str "Won " bet " chips!! :)\n"))
                       bet))
                 (= player-score dealer-score)
                 (do (println (str "Push " bet " chips.\n") )
                     0)
                 :else
                 (do (println (str "Lost " bet " chips :(\n") )
                     (- bet)))]
    (Thread/sleep pause-duration)
    payout))

(defn dealer-round [player-hand dealer-hand bet]
  (let [dealer-score (calculate-score dealer-hand)]
    (if (< dealer-score 17)
      (let [new-dealer-hand (conj dealer-hand (take-card))
            new-dealer-score (calculate-score new-dealer-hand)]
        (print-hands player-hand new-dealer-hand)
        (if (> new-dealer-score 21)
          (do (println (str "Dealer Bust!! Won " bet " chips :)\n") )
              (Thread/sleep pause-duration)
              bet)
          (dealer-round player-hand new-dealer-hand bet)))
      (calculate-payout (calculate-score player-hand)
                        dealer-score
                        bet))))

(defn get-player-option
  "Ask user to select hit or stand."
  []
  (print "Hit(H) or Stand(S) ? ")
  (flush)
  (let [cr (ConsoleReader.)]
    (loop []
      (let [input (char (.readCharacter cr))]
        (println)
        (case input
          \H :hit
          \h :hit
          \S :stand
          \s :stand
          (do (println "Please enter H or S !!")
              (recur)))))))

(defn player-round [player-hand dealer-hand bet]
  (case (get-player-option)
    :hit
    (let [new-player-hand (conj player-hand (take-card))
          new-player-score (calculate-score new-player-hand)]
      (print-hands-with-dealer-hidden new-player-hand dealer-hand)
      (cond (> new-player-score 21) (do
                                      (println (str "Bust!! Lost " bet " chips :(\n"))
                                      (- bet))
            (= new-player-score 21) (dealer-round new-player-hand dealer-hand bet)
            :else (player-round new-player-hand dealer-hand bet)))
    :stand
    (do (print-hands player-hand dealer-hand)
        (dealer-round player-hand dealer-hand bet))))

(defn get-bet-amount
  "Gets the bet amount from the user."
  [chips-available]
  (println "Place Bet Amount (0 to quit)?")
  (flush)
  (let [cr (ConsoleReader.)]
    (loop []
      (let [input (re-find #"\d+" (.readLine cr))]
        (if-not (clojure.string/blank? input)
          (let [bet (Integer/parseInt input)]
            (if (<= bet chips-available)
              bet
              (do (println "Insufficient chips!! Enter Again")
                  (recur))))
          (do (println "Please enter a number!!")
              (recur)))))))

(defn play-round [bet]
  (let [blackjack-score 21
        player-hand [(take-card) (take-card)]
        dealer-hand [(take-card) (take-card)]
        player-score (calculate-score player-hand)
        dealer-score (calculate-score dealer-hand)]
    (if (or (= player-score blackjack-score)
            (= dealer-score blackjack-score))
      (do (print-hands player-hand dealer-hand)
          (calculate-payout player-score dealer-score bet :blackjack true))
      (do (print-hands-with-dealer-hidden player-hand dealer-hand)
          (player-round player-hand dealer-hand bet)))))

(defn start-round [chips-available]
  (println (str "Chips Available: " chips-available \newline))
  (if (<= chips-available 0)
    (println "Game Over")
    (let [bet (get-bet-amount chips-available)]
      (if (= bet 0)
        (println "\nExiting....")
        (let [chips-from-play (play-round bet)]
          (Thread/sleep pause-duration)
          (start-round (+ chips-available chips-from-play)))))))

