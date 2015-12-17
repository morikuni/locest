package com.github.morikuni.locest.frequency.domain.service.impl

import com.github.morikuni.locest.frequency.domain.model.{FrequencyInformation, WordId}
import com.github.morikuni.locest.frequency.domain.repository.impl.{InjectFrequencyInformationRepository, InjectFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.repository.{DependFrequencyInformationRepository, DependFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.service.{DependFrequencyInformationService, FrequencyInformationService}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import scala.concurrent.Future

trait FrequencyInformationServiceImpl extends FrequencyInformationService
  with DependFrequencyInformationRepository
  with DependFrequencyInformationRepositoryTransactionManager {
  /** 指定された単語についての全頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Future.successful(List(FrequencyInformation)) 成功時
    *         Future.failed(NoSuchElementException) 指定された単語IDが存在しない場合
    *         Future.failed(IOException) 入出力に失敗した場合
    */
  override def allFrequenciesOfWord(wordId: WordId)(implicit ecp: ExecutionContextProvider): Future[List[FrequencyInformation]] = {
    frequencyInformationRepositoryTransactionManger.execute(frequencyInformationRepository.findByWordId(wordId))(ecp.repository)
      .flatMap { l =>
        if (l.isEmpty) Future.failed(new NoSuchElementException("No word has such ID"))
        else Future.successful(l)
      }(ecp.default)
  }

}

trait InjectFrequencyInformationService extends DependFrequencyInformationService {
  override def frequencyInformationService: FrequencyInformationService =
    new FrequencyInformationServiceImpl
      with InjectFrequencyInformationRepository
      with InjectFrequencyInformationRepositoryTransactionManager
}