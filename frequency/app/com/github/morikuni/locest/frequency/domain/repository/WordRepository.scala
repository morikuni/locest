package com.github.morikuni.locest.frequency.domain.repository

import com.github.morikuni.locest.frequency.domain.model.{Word, WordId, WordProperty}
import com.github.morikuni.locest.util.{Repository, Session, Transaction, TransactionManager}

trait WordRepositorySession extends Session

trait WordRepository extends Repository[Word] {
  /** 単語がすでに保存されているか調べる
    *
    * @param wordProperty
    * @return Transaction(Some(WordId)) 単語が保存されている場合
    *         Transaction(None) 単語が登録されていない場合
    */
  def find(wordProperty: WordProperty): Transaction[WordRepositorySession, Option[WordId]]

  /** 単語を保存する
    *
    * @param wordProperty
    * @return Transaction()
    */
  def store(wordProperty: WordProperty): Transaction[WordRepositorySession, Unit]
}

trait DependWordRepository {
  def wordRepository: WordRepository
}

trait DependWordRepositoryTransactionManger {
  def wordRepositoryTransactionManager: TransactionManager[WordRepositorySession]
}