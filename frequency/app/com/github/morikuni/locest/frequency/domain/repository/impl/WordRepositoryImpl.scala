package com.github.morikuni.locest.frequency.domain.repository.impl

import com.github.morikuni.locest.frequency.domain.model.{WordId, Word, WordProperty}
import com.github.morikuni.locest.frequency.domain.repository.{DependWordRepositoryTransactionManger, DependWordRepository, WordRepositorySession, WordRepository}
import com.github.morikuni.locest.util.{TransactionManager, Transaction}
import scala.concurrent.{Future, ExecutionContext}
import scalikejdbc.scalikejdbcSQLInterpolationImplicitDef

class WordRepositoryImpl extends WordRepository {
  /** Word を作成し、保存する。
    * 既に保存されている場合は、保存されている単語を取得する。
    *
    * @param wordProperty
    * @return Transaction(Word) 成功時
    */
  override def create(wordProperty: WordProperty): Transaction[WordRepositorySession, Word] = PostgreSQLTransactionManager.ask.map { implicit session =>
    sql"SELECT word_id FROM Word WHERE word_text= ?".bind(wordProperty.text)
      .map(r => r.int(1))
      .single.apply
      .orElse {
        sql"INSERT INTO Word (word_text) VALUES (?) RETURNING word_id".bind(wordProperty.text)
          .map(r => r.int(1))
          .single.apply
      }
      .map(id => Word(WordId(id), wordProperty))
      .get
  }
}

trait InjectWordRepository extends DependWordRepository {
  override def wordRepository: WordRepository = new WordRepositoryImpl
}

trait InjectWordRepositoryTransactionManager extends DependWordRepositoryTransactionManger {
  override def wordRepositoryTransactionManager: TransactionManager[WordRepositorySession] = new TransactionManager[WordRepositorySession] {
    override def execute[A](transaction: Transaction[WordRepositorySession, A])(ctx: ExecutionContext): Future[A] = PostgreSQLTransactionManager.execute(transaction)(ctx)
  }
}