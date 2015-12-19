package com.github.morikuni.locest.frequency.domain.service

import com.github.morikuni.locest.frequency.domain.model.{WordProperty, Word, FrequencyInformation, FrequencyInformationProperty, AreaId, WordId, FrequencyInformationId}
import com.github.morikuni.locest.frequency.domain.repository.{AreaRepository, AreaRepositorySession, FrequencyInformationRepository, FrequencyInformationRepositorySession}
import com.github.morikuni.locest.frequency.domain.service.impl.FrequencyInformationServiceImpl
import com.github.morikuni.locest.frequency.test.helper.{AreaRepositoryHelper, WordServiceHelper, InjectExecutionContextProviderHelper, FrequencyInformationRepositoryHelper, TransactionManagerHelper}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import java.io.IOException
import org.specs2.matcher.Matchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

class FrequencyInformationServiceSpec extends Specification
  with Mockito
  with InjectExecutionContextProviderHelper {

  def create(
    areaRepo: AreaRepository = AreaRepositoryHelper.create(),
    freqRepo: FrequencyInformationRepository = FrequencyInformationRepositoryHelper.create(),
    wordServ: WordService = WordServiceHelper.create()
  ): FrequencyInformationServiceImpl = new FrequencyInformationServiceImpl {
    override def frequencyInformationRepository: FrequencyInformationRepository = freqRepo

    override def areaRepository: AreaRepository = areaRepo

    override def wordService: WordService = wordServ

    override def frequencyInformationRepositoryTransactionManger: TransactionManager[FrequencyInformationRepositorySession] = TransactionManagerHelper.emptyTransactionManager

    override def areaRepositoryTransactionManager: TransactionManager[AreaRepositorySession] = TransactionManagerHelper.emptyTransactionManager
  }

  val wordId = WordId(1)
  val word = Word(wordId, WordProperty("テスト"))
  val areaId = AreaId(1)
  val freqId = FrequencyInformationId(wordId, areaId)
  val freqProp = FrequencyInformationProperty(10)
  val freq = FrequencyInformation(freqId, freqProp)

  "allFrequenciesOfWord" should {
    "return List(FrequencyInformation) successfully" in {
      val freqRepo = FrequencyInformationRepositoryHelper.create(findByWordId = Transaction.successful(List(freq)))
      val service = create(freqRepo = freqRepo)
      val expect = List(freq)
      val result = service.allFrequenciesOfWord(wordId)

      Await.result(result, Duration.Inf) must_=== expect
    }

    "fail with IOException if FrequencyInformationRepository.findByWordId failed with IOException" in {
      val freqRepo = FrequencyInformationRepositoryHelper.create(findByWordId = Transaction.failed(new IOException))
      val service = create(freqRepo = freqRepo)
      val result = service.allFrequenciesOfWord(wordId)

      Await.result(result, Duration.Inf) must throwA[IOException]
    }
  }

  "registerSentence" should {
    "return () successfully" in {
      val areaRepo = AreaRepositoryHelper.create(findByCoordinate = Transaction.successful(Some(areaId)))
      val wordServ = WordServiceHelper.create(morphologicalAnalysis = Future.successful(Map(word -> 10)))
      val freqRepo = FrequencyInformationRepositoryHelper.create(add = Transaction.successful(()))
      val service = create(wordServ = wordServ, areaRepo = areaRepo, freqRepo = freqRepo)
      val result = service.registerSentence("テスト", 34, 134)

      Await.result(result, Duration.Inf) must_=== (())
      there was one(areaRepo).findByCoordinate(34, 134)
      there was one(wordServ).morphologicalAnalysis(===("テスト"))(any)
      there was one(freqRepo).add(===(freq))
    }

    "fail with IOExcpetion if AreaReposutory.findByCoordinate failed with IOException" in {
      val areaRepo = AreaRepositoryHelper.create(findByCoordinate = Transaction.failed(new IOException))
      val wordServ = WordServiceHelper.create()
      val freqRepo = FrequencyInformationRepositoryHelper.create()
      val service = create(wordServ = wordServ, areaRepo = areaRepo, freqRepo = freqRepo)
      val result = service.registerSentence("テスト", 34, 134)

      Await.result(result, Duration.Inf) must throwA[IOException]
      there was no(freqRepo).add(===(freq))
    }

    "fail with IOExcpetion if WordService.morphologicalAnalysis failed with IOException" in {
      val areaRepo = AreaRepositoryHelper.create(findByCoordinate = Transaction.successful(Some(areaId)))
      val wordServ = WordServiceHelper.create(morphologicalAnalysis = Future.failed(new IOException))
      val freqRepo = FrequencyInformationRepositoryHelper.create()
      val service = create(wordServ = wordServ, areaRepo = areaRepo, freqRepo = freqRepo)
      val result = service.registerSentence("テスト", 34, 134)

      Await.result(result, Duration.Inf) must throwA[IOException]
      there was no(freqRepo).add(===(freq))
    }

    "fail with IOException if FrequencyInformationRepository.add failed with IOException" in {
      val areaRepo = AreaRepositoryHelper.create(findByCoordinate = Transaction.successful(Some(areaId)))
      val wordServ = WordServiceHelper.create(morphologicalAnalysis = Future.successful(Map(word -> 10)))
      val freqRepo = FrequencyInformationRepositoryHelper.create(add = Transaction.failed(new IOException))
      val service = create(wordServ = wordServ, areaRepo = areaRepo, freqRepo = freqRepo)
      val result = service.registerSentence("テスト", 34, 134)

      Await.result(result, Duration.Inf) must throwA[IOException]
      there was one(freqRepo).add(===(freq))
    }
  }
}
