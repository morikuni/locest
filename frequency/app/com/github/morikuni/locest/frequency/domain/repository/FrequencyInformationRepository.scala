package com.github.morikuni.locest.frequency.domain.repository

import com.github.morikuni.locest.frequency.domain.model.{WordId, FrequencyInformation, FrequencyInformationId}
import com.github.morikuni.locest.util.{Repository, Session, Transaction, TransactionManager}

trait FrequencyInformationRepositorySession extends Session

trait FrequencyInformationRepository extends Repository[FrequencyInformation] {
  /** 指定された単語についての全ての頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Transaction(List(FrequencyInformation)) 頻度情報が保存されている場合
    *         Transaction(Nil) 頻度情報が保存されていない場合
    */
  def findByWordId(wordId: WordId): Transaction[FrequencyInformationRepositorySession, List[FrequencyInformation]]
}

trait DependFrequencyInformationRepository {
  def frequencyInformationRepository: FrequencyInformationRepository
}

trait DependFrequencyInformationRepositoryTransactionManager {
  def frequencyInformationRepositoryTransactionManger: TransactionManager[FrequencyInformationRepositorySession]
}