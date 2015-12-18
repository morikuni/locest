package com.github.morikuni.locest.frequency.domain.repository

import com.github.morikuni.locest.frequency.domain.model.{FrequencyInformation, WordId}
import com.github.morikuni.locest.util.{Repository, Session, Transaction, TransactionManager}

trait FrequencyInformationRepositorySession extends Session

trait FrequencyInformationRepository extends Repository[FrequencyInformation] {
  /** 指定された単語についての全ての頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Transaction(List(FrequencyInformation)) 成功時
    */
  def findByWordId(wordId: WordId): Transaction[FrequencyInformationRepositorySession, List[FrequencyInformation]]

  /** 全単語の出現回数の合計を求める
    *
    * @return Transaction(Long) 成功時
    */
  def sumOfAllCounts: Transaction[FrequencyInformationRepositorySession, Long]

  /** 現在の出現回数に指定された頻度情報の出現か回数を足し合わせる
    *
    * @param frequencyInformation 足し合わせる頻度情報
    * @return Transaction(())
    */
  def add(frequencyInformation: FrequencyInformation): Transaction[FrequencyInformationRepositorySession, Unit]
}

trait DependFrequencyInformationRepository {
  def frequencyInformationRepository: FrequencyInformationRepository
}

trait DependFrequencyInformationRepositoryTransactionManager {
  def frequencyInformationRepositoryTransactionManger: TransactionManager[FrequencyInformationRepositorySession]
}
