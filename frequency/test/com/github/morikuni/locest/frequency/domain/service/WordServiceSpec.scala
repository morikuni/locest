package com.github.morikuni.locest.frequency.domain.service

import com.github.morikuni.locest.frequency.domain.model.{WordProperty, WordId, Word}
import com.github.morikuni.locest.frequency.domain.repository.{WordRepositorySession, WordRepository}
import com.github.morikuni.locest.frequency.domain.service.impl.WordServiceImpl
import com.github.morikuni.locest.frequency.test.helper.{WordRepositoryHelper, InjectExecutionContextProviderHelper, TransactionManagerHelper}
import com.github.morikuni.locest.util.{Transaction, TransactionManager}
import java.io.IOException
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class WordServiceSpec extends Specification with Mockito with InjectExecutionContextProviderHelper {
  def create(wordRepo: WordRepository): WordServiceImpl = new WordServiceImpl {
    override def wordRepositoryTransactionManager: TransactionManager[WordRepositorySession] = TransactionManagerHelper.emptyTransactionManager

    override def wordRepository: WordRepository = wordRepo
  }

  "morphologicalAnalysis" should {
    val sentence = "もももすももももも"
    val momo = Word(WordId(1), WordProperty("もも"))
    val mo = Word(WordId(2), WordProperty("も"))
    val sumomo = Word(WordId(3), WordProperty("すもも"))

    "return Map(Word -> Int) successfully" in {
      val wordRepo = mock[WordRepository]
      wordRepo.create(===(momo.property)) returns Transaction.successful(momo)
      wordRepo.create(===(mo.property)) returns Transaction.successful(mo)
      wordRepo.create(===(sumomo.property)) returns Transaction.successful(sumomo)
      val service = create(wordRepo)
      val expect = Map(momo -> 2, mo -> 2, sumomo -> 1)
      val result = service.morphologicalAnalysis(sentence)

      Await.result(result, Duration.Inf) must_=== expect
    }

    "fail with IOException if WordRepository.create failed with IOException" in {
      val wordRepo = WordRepositoryHelper.create(create = Transaction.failed(new IOException))
      val service = create(wordRepo)
      val result = service.morphologicalAnalysis(sentence)

      Await.result(result, Duration.Inf) must throwA[IOException]
    }
  }
}
