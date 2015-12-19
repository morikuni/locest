package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.dto.MorphemeDto
import com.github.morikuni.locest.frequency.application.service.impl.MorphologicalAnalysisServiceImpl
import com.github.morikuni.locest.frequency.domain.model.{WordProperty, WordId, Word}
import com.github.morikuni.locest.frequency.domain.service.WordService
import com.github.morikuni.locest.frequency.test.helper.{WordServiceHelper, InjectExecutionContextProviderHelper}
import java.io.IOException
import org.specs2.mutable.Specification
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class MorphologicalAnalysisServiceSpec extends Specification {
  def create(wordServ: WordService): MorphologicalAnalysisServiceImpl = new MorphologicalAnalysisServiceImpl with InjectExecutionContextProviderHelper {
    override def wordService: WordService = wordServ
  }

  val sentence = "ああいい"
  val word = Word(WordId(1), WordProperty("ああ"))
  val word2 = Word(WordId(2), WordProperty("いい"))

  "morphologicalAnalysis" should {
    "return Seq(MorphemeDto) successfully" in {
      val wordServ = WordServiceHelper.create(morphologicalAnalysis = Future.successful(Map(word -> 1, word2 -> 2)))
      val service = create(wordServ)
      val expect = MorphemeDto.fromMap(Map(word -> 1, word2 -> 2))
      val result = service.morphologicalAnalysis(sentence)

      Await.result(result, Duration.Inf) must_=== expect
    }

    "fail with IOException if WordService.morphologicalAnalysis failed with IOException" in {
      val wordServ = WordServiceHelper.create(morphologicalAnalysis = Future.failed(new IOException))
      val service = create(wordServ)
      val result = service.morphologicalAnalysis(sentence)

      Await.result(result, Duration.Inf) must throwA[IOException]
    }
  }
}
