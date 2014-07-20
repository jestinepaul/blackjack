(ns com.jestinepaul.blackjack.core-test
  (:require [clojure.test :refer :all]
            [com.jestinepaul.blackjack.core :as core]
            [com.jestinepaul.blackjack.card :as card]))

(def bet 10)

(alter-var-root #'card/pause-duration (fn [_] 0))

(deftest player-blackjack
  (testing "Player gets a blackjack"
    (with-redefs [card/shoe (ref '({:rank "A"} {:rank "J"}
                                   {:rank "4"} {:rank "5"}))]
                 (is (= (core/play-round bet)
                        (int (* 1.5 bet)))))))

(deftest dealer-blackjack
  (testing "Dealer gets a blackjack"
    (with-redefs [card/shoe (ref '({:rank "7"} {:rank "J"}
                                   {:rank "A"} {:rank "K"}))]
                 (is (= (core/play-round bet)
                        (- bet))))))

(deftest blackjack-push
  (testing "Player and Dealer both get a blackjack"
    (with-redefs [card/shoe (ref '({:rank "10"} {:rank "A"}
                                   {:rank "A"} {:rank "K"}))]
                 (is (= (core/play-round bet)
                        0)))))

(deftest player-bust
  (testing "Player gets bust after hitting score 22"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}  ; 6
                                   {:rank "9"} {:rank "K"}
                                   {:rank "3"} {:rank "7"} {:rank "6"})) ; 6 + 16 = 22
                  core/get-player-option (let [inputs (atom [:hit :hit :hit])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        (- bet))))))

(deftest player-hits-21-won-without-dealer-round
  (testing "Player hits 21 and wins without dealer playing"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}  ; 6
                                   {:rank "9"} {:rank "K"}
                                   {:rank "3"} {:rank "7"} {:rank "5"})) ; 6 + 15 = 21
                  core/get-player-option (let [inputs (atom [:hit :hit :hit])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        bet)))))

(deftest player-hits-21-won-after-dealer-round
  (testing "Player hits 21 and wins after dealer playing"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}
                                   {:rank "9"} {:rank "7"}
                                   {:rank "3"} {:rank "7"} {:rank "5"} ; 21
                                   {:rank "4"}))            ; 20
                  core/get-player-option (let [inputs (atom [:hit :hit :hit])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        bet)))))

(deftest player-hits-21-push
  (testing "Player hits 21 and dealer also hits 21"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}
                                   {:rank "9"} {:rank "7"}
                                   {:rank "3"} {:rank "7"} {:rank "5"}
                                   {:rank "5"}))
                  core/get-player-option (let [inputs (atom [:hit :hit :hit])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        0)))))

(deftest player-hits-stands-win
  (testing "Player hits 19 and stands, dealer hits 17"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}
                                   {:rank "9"} {:rank "7"}
                                   {:rank "3"} {:rank "7"} {:rank "3"}
                                   {:rank "A"}))
                  core/get-player-option (let [inputs (atom [:hit :hit :hit :stand])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        bet)))))

(deftest player-hits-stands-push
  (testing "Player hits 19 and stands, dealer also hits 19"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}
                                   {:rank "9"} {:rank "7"}
                                   {:rank "3"} {:rank "7"} {:rank "3"}
                                   {:rank "3"}))
                  core/get-player-option (let [inputs (atom [:hit :hit :hit :stand])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        0)))))

(deftest player-hits-stands-lose
  (testing "Player hits 19 and stands, dealer hits 20"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}
                                   {:rank "9"} {:rank "7"}
                                   {:rank "3"} {:rank "7"} {:rank "3"}
                                   {:rank "4"}))
                  core/get-player-option (let [inputs (atom [:hit :hit :hit :stand])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        (- bet))))))

(deftest player-hits-stands-dealer-bust
  (testing "Player hits 19 and stands, dealer hits 20"
    (with-redefs [card/shoe (ref '({:rank "4"} {:rank "2"}
                                   {:rank "9"} {:rank "7"}
                                   {:rank "3"} {:rank "7"} {:rank "3"}
                                   {:rank "Q"}))
                  core/get-player-option (let [inputs (atom [:hit :hit :hit :stand])]
                                           (fn [] (let [input (first @inputs)]
                                                    (swap! inputs rest)
                                                    input)))]
                 (is (= (core/play-round bet)
                        bet)))))

(deftest payout-calcuation
  (testing "Payout Calculation"
    (is
      (=
        (core/calculate-payout 18 20 bet)
        (- bet)))
    (is
      (=
        (core/calculate-payout 21 19 bet)
        bet))
    (is
      (=
        (core/calculate-payout 19 19 bet)
        0))
    (is
      (=
        (core/calculate-payout 21 19 bet :blackjack true)
        (int (* 1.5 bet))))))


(deftest dealer-round-1
  (testing "Dealer Round Winning Case"
    (with-redefs [card/shoe (ref '({:rank "A"} {:rank "4"}))]
                 (is (=  (core/dealer-round [{:rank "J"} {:rank "A"}]
                                       [{:rank "8"} {:rank "7"}]
                                       bet)
                         bet)))))

(deftest dealer-round-2
  (testing "Dealer Round Push Case"
    (with-redefs [card/shoe (ref '({:rank "3"}))]
                 (is (=  (core/dealer-round [{:rank "J"} {:rank "8"}]
                                       [{:rank "8"} {:rank "7"}]
                                       bet)
                         0)))))


(deftest dealer-round-3
  (testing "Dealer Round Losing Case"
    (with-redefs [card/shoe (ref '({:rank "4"}))]
                 (is (=  (core/dealer-round [{:rank "J"} {:rank "8"}]
                                       [{:rank "8"} {:rank "7"}]
                                       bet)
                         (- bet))))))
