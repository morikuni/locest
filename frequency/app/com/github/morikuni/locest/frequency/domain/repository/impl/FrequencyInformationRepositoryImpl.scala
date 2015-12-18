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
    * @return Transaction(List(FrequencyInformation)) 成功時
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
    sql"""SELECT
          CASE
            WHEN sum(word_count) IS NULL THEN 0
            WHEN sum(word_count) IS NOT NULL THEN sum(word_count)
          END
        FROM Frequency"""
      .map(r => r.long(1))
      .single.apply.getOrElse(0)
  }

  /** 現在の出現回数に指定された頻度情報の出現か回数を足し合わせる
    *
    * @param frequencyInformation 足し合わせる頻度情報
    * @return Transaction(())
    */
  override def add(frequencyInformation: FrequencyInformation): Transaction[FrequencyInformationRepositorySession, Unit] = PostgreSQLTransactionManager.ask.map { implicit session =>
    val c = frequencyInformation.property.count
    val wid = frequencyInformation.id.wordId.value
    val aid = frequencyInformation.id.areaId.value
    sql"SELECT 1 FROM Frequency WHERE word_id = ? AND area_id = ?"
      .bind(wid, aid)
      .map(_ => ())
      .single.apply match {
        case None =>
          sql"INSERT INTO Frequency (word_id, area_id, word_count) VALUES (?, ?, ?)"
            .bind(wid, aid, c)
            .update.apply
        case Some(_) =>
          sql"UPDATE Frequency SET word_count = word_count + ? WHERE word_id = ? AND area_id = ?"
            .bind(c, wid, aid)
            .update.apply
      }
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