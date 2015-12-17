package com.github.morikuni.locest.frequency.domain.repository

import com.github.morikuni.locest.frequency.domain.model.{Word, WordId, WordProperty}
import com.github.morikuni.locest.util.{Repository, Session, Transaction, TransactionManager}

trait WordRepositorySession extends Session

trait WordRepository extends Repository[Word] {
  /** Word を作成し、保存する。
    * 既に保存されている場合は、保存されている単語を取得する。
    *
    * @param wordProperty
    * @return Transaction(Word) 成功時
    */
  def create(wordProperty: WordProperty): Transaction[WordRepositorySession, Word]
}

trait DependWordRepository {
  def wordRepository: WordRepository
}

trait DependWordRepositoryTransactionManger {
  def wordRepositoryTransactionManager: TransactionManager[WordRepositorySession]
}