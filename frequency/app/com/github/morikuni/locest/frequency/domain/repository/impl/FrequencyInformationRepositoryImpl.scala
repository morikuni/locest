package com.github.morikuni.locest.frequency.domain.repository.impl

import com.github.morikuni.locest.frequency.domain.model.{FrequencyInformationProperty, AreaId, FrequencyInformationId, WordId, FrequencyInformation}
import com.github.morikuni.locest.frequency.domain.repository.{DependFrequencyInformationRepositoryTransactionManager, DependFrequencyInformationRepository, FrequencyInformationRepositorySession, FrequencyInformationRepository}
import com.github.morikuni.locest.util.{TransactionManager, Transaction}
import scala.concurrent.{Future, ExecutionContext}
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
      }.list().apply()
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