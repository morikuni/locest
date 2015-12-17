package com.github.morikuni.locest.frequency.domain.repository.impl

import com.github.morikuni.locest.frequency.domain.model.{AreaId, FrequencyInformation, FrequencyInformationId, FrequencyInformationProperty, WordId}
import com.github.morikuni.locest.frequency.domain.repository.{DependFrequencyInformationRepository, DependFrequencyInformationRepositoryTransactionManager, FrequencyInformationRepository, FrequencyInformationRepositorySession}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import scala.concurrent.{ExecutionContext, Future}
import scalikejdbc.scalikejdbcSQLInterpolationImplicitDef

class FrequencyInformationRepositoryImpl extends FrequencyInformationRepository {
  /** 指定された単語についての全ての頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Transaction(List(FrequencyInformation)) 頻度情報が保存されている場合
    *         Transaction(Nil) 頻度情報が保存されていない場合
    */
  override def findByWordId(wordId: WordId): Transaction[FrequencyInformationRepositorySession, List[FrequencyInformation]] = PostgreSQLTransactionManager.ask.map { implicit session =>
    sql"SELECT word_id, area_id, word_count FROM Frequency WHERE word_id = ?".bind(wordId.value)
      .map { r =>
        val fId = FrequencyInformationId((WordId(r.int(1)), AreaId(r.int(2))))
        val prop = FrequencyInformationProperty(r.int(3))
        FrequencyInformation(fId, prop)
      }.list.apply
  }

  /** 全単語の出現回数の合計を求める
    *
    * @return Transaction(Long) 成功時
    */
  override def sumOfAllCounts: Transaction[FrequencyInformationRepositorySession, Long] = PostgreSQLTransactionManager.ask.map { implicit session =>
    sql"SELECT sum(word_count) FROM Frequency"
      .map(r => r.long(1))
      .single.apply.getOrElse(0)
  }

  /** 現在の出現回数に指定された頻度情報の出現か回数を足し合わせる
    *
    * @param frequencyInformation 足し合わせる頻度情報
    * @return Transaction(())
    */
  override def add(frequencyInformation: FrequencyInformation): Transaction[FrequencyInformationRepositorySession, Unit] = PostgreSQLTransactionManager.ask.map { implicit session =>
    sql"UPDATE Frequency SET word_count = word_count + ? WHERE word_id = ?".bind(frequencyInformation.property.count, frequencyInformation.id.value)
      .update.apply
  }
}

trait InjectFrequencyInformationRepository extends DependFrequencyInformationRepository {
  override def frequencyInformationRepository: FrequencyInformationRepository = new FrequencyInformationRepositoryImpl
}

trait InjectFrequencyInformationRepositoryTransactionManager extends DependFrequencyInformationRepositoryTransactionManager {
  override def frequencyInformationRepositoryTransactionManger: TransactionManager[FrequencyInformationRepositorySession] = new TransactionManager[FrequencyInformationRepositorySession] {
    override def execute[A](transaction: Transaction[FrequencyInformationRepositorySession, A])(ctx: ExecutionContext): Future[A] = PostgreSQLTransactionManager.execute(transaction)(ctx)
  }
}