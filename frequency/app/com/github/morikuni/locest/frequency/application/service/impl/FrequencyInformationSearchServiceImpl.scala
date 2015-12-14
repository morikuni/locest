package com.github.morikuni.locest.frequency.application.service.impl

import com.github.morikuni.locest.frequency.application.InjectExecutionContextProvider
import com.github.morikuni.locest.frequency.application.dto.FrequencyInformationDto
import com.github.morikuni.locest.frequency.application.service.{DependFrequencyInformationSearchService, FrequencyInformationSearchService}
import com.github.morikuni.locest.frequency.domain.model.WordId
import com.github.morikuni.locest.frequency.domain.repository.impl.{InjectFrequencyInformationRepositoryTransactionManager, InjectFrequencyInformationRepository}
import com.github.morikuni.locest.frequency.domain.repository.{DependFrequencyInformationRepository, DependFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.support.DependExecutionContextProvider
import scala.concurrent.Future

trait FrequencyInformationSearchServiceImpl extends FrequencyInformationSearchService
  with DependFrequencyInformationRepository
  with DependFrequencyInformationRepositoryTransactionManager
  with DependExecutionContextProvider {
  /** 指定された単語についての全ての頻度情報を取得する
    *
    * @param wordId 単語ID
    * @return Future.successful(List(FrequencyInformationDto)) 成功時
    *         Future.failed(NoSuchElementException) 指定された単語が存在しない場合
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def searchByWordId(wordId: Int): Future[List[FrequencyInformationDto]] = {
    frequencyInformationRepositoryTransactionManger.execute(frequencyInformationRepository.findByWordId(WordId(wordId)))(executionContextProvider.repository)
      .map(_.map(FrequencyInformationDto.from))(executionContextProvider.default)
      .flatMap { l =>
        if (l.isEmpty) Future.failed(new NoSuchElementException("No word has such ID"))
        else Future.successful(l)
      }(executionContextProvider.default)
  }
}

trait InjectFrequencyInformationSearchService extends DependFrequencyInformationSearchService {
  override val frequencyInformationSearchService: FrequencyInformationSearchService =
    new FrequencyInformationSearchServiceImpl
      with InjectFrequencyInformationRepository
      with InjectFrequencyInformationRepositoryTransactionManager
      with InjectExecutionContextProvider
}