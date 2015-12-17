package com.github.morikuni.locest.frequency.application.service.impl

import com.github.morikuni.locest.frequency.application.InjectExecutionContextProvider
import com.github.morikuni.locest.frequency.application.dto.CountDto
import com.github.morikuni.locest.frequency.application.service.{CountService, DependCountService}
import com.github.morikuni.locest.frequency.domain.repository.impl.{InjectFrequencyInformationRepository, InjectFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.repository.{DependFrequencyInformationRepository, DependFrequencyInformationRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.support.DependExecutionContextProvider
import scala.concurrent.Future

trait CountServiceImpl extends CountService
  with DependFrequencyInformationRepository
  with DependFrequencyInformationRepositoryTransactionManager
  with DependExecutionContextProvider {
  /** 合計出現回数を取得する
    *
    * @return Future.successful(CountDto) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  override def totalNumberOfCount: Future[CountDto] = {
    frequencyInformationRepositoryTransactionManger.execute(frequencyInformationRepository.sumOfAllCounts)(executionContextProvider.repository)
      .map(l => CountDto(l))(executionContextProvider.default)
  }
}

trait InjectCountService extends DependCountService {
  override def countService: CountService =
    new CountServiceImpl
      with InjectFrequencyInformationRepository
      with InjectFrequencyInformationRepositoryTransactionManager
      with InjectExecutionContextProvider
}