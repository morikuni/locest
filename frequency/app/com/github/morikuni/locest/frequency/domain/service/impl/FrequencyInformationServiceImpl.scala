package com.github.morikuni.locest.frequency.domain.service.impl

import com.github.morikuni.locest.frequency.domain.model.{FrequencyInformationProperty, FrequencyInformationId, FrequencyInformation, WordId}
import com.github.morikuni.locest.frequency.domain.repository.impl.{InjectAreaRepositoryTransactionManager, InjectAreaRepository, InjectFrequencyInformationRepository, InjectFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.repository.{FrequencyInformationRepositorySession, DependAreaRepositoryTransactionManager, DependAreaRepository, DependFrequencyInformationRepository, DependFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.service.{DependWordService, DependFrequencyInformationService, FrequencyInformationService}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import com.github.morikuni.locest.util.Transaction
import scala.concurrent.{ExecutionContext, Future}

trait FrequencyInformationServiceImpl extends FrequencyInformationService
  with DependFrequencyInformationRepository
  with DependFrequencyInformationRepositoryTransactionManager
  with DependAreaRepository
  with DependAreaRepositoryTransactionManager
  with DependWordService {
  /** 指定された単語についての全頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Future.successful(List(FrequencyInformation)) 成功時
    *         Future.failed(NoSuchElementException) 指定された単語IDが存在しない場合
    *         Future.failed(IOException) 入出力に失敗した場合
    */
  override def allFrequenciesOfWord(wordId: WordId)(implicit ecp: ExecutionContextProvider): Future[List[FrequencyInformation]] = {
    import ecp.default
    frequencyInformationRepositoryTransactionManger.execute(frequencyInformationRepository.findByWordId(wordId))(ecp.repository)
      .flatMap { l =>
        if (l.isEmpty) Future.failed(new NoSuchElementException("No word has such ID"))
        else Future.successful(l)
      }
  }

  /** 指定された文章を指定された座標のエリアに登録する
    *
    * @param sentence 登録する文章
    * @param lat 緯度
    * @param lng 経度
    * @return Future.successful(()) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def registerSentence(sentence: String, lat: Double, lng: Double)(implicit ecp: ExecutionContextProvider): Future[Unit] = {
    import ecp.default

    val areaIdFuture =
      areaRepositoryTransactionManager.execute(areaRepository.findByCoordinate(lat, lng))(ecp.repository)
        .flatMap {
          case Some(aid) => Future.successful(aid)
          case None => Future.failed(new NoSuchElementException("No area has such coordinate"))
        }

    val countsFuture = wordService.morphologicalAnalysis(sentence)

    val txSeqFuture =
      for {
        areaId <- areaIdFuture
        counts <- countsFuture
      } yield {
        counts.map { case (word, count) => FrequencyInformation(FrequencyInformationId((word.id, areaId)), FrequencyInformationProperty(count))}
          .map(fi => frequencyInformationRepository.add(fi))
      }

    val tx: Transaction[FrequencyInformationRepositorySession, Iterable[Unit]] = for {
      txSeq <- Transaction.fromFuture(txSeqFuture)
      tx <- Transaction.sequence(txSeq)
    } yield tx

    frequencyInformationRepositoryTransactionManger.execute(tx)(ecp.repository)
      .map(_ => ())
  }
}

trait InjectFrequencyInformationService extends DependFrequencyInformationService {
  override def frequencyInformationService: FrequencyInformationService =
    new FrequencyInformationServiceImpl
      with InjectFrequencyInformationRepository
      with InjectFrequencyInformationRepositoryTransactionManager
      with InjectAreaRepository
      with InjectAreaRepositoryTransactionManager
      with InjectWordService
}