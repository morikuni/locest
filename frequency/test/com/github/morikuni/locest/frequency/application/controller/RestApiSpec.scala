package com.github.morikuni.locest.frequency.application.controller

import com.github.morikuni.locest.frequency.application.dto.{MorphemeDto, CountDto, FrequencyInformationDto}
import com.github.morikuni.locest.frequency.application.service.{FrequencyInformationSearchService, MorphologicalAnalysisService, FrequencyInformationUpdateService, CountService}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import com.github.morikuni.locest.frequency.test.helper.{FrequencyInformationSearchServiceHelper, FrequencyInformationUpdateServiceHelper, CountServiceHelper, MorphologicalAnalysisServiceHelper, InjectExecutionContextProviderHelper}
import java.io.IOException
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.{FakeRequest, PlaySpecification}
import scala.concurrent.Future

class RestApiSpec extends PlaySpecification with Mockito {
  def create(
    morphServ: MorphologicalAnalysisService = MorphologicalAnalysisServiceHelper.create(),
    countServ: CountService = CountServiceHelper.create(),
    freqUpdateServ: FrequencyInformationUpdateService = FrequencyInformationUpdateServiceHelper.create(),
    freqSearchServ: FrequencyInformationSearchService = FrequencyInformationSearchServiceHelper.create()
  ): RestApi = new RestApi with InjectExecutionContextProviderHelper {
    override def morphologicalAnalysisService: MorphologicalAnalysisService = morphServ

    override def frequencyInformationUpdateService: FrequencyInformationUpdateService = freqUpdateServ

    override def countService: CountService = countServ

    override def frequencyInformationSearchService: FrequencyInformationSearchService = freqSearchServ
  }


  "frequencies_word" should {
    val freqDto = FrequencyInformationDto(1, 2, 3)
    val freqDto2 = FrequencyInformationDto(1, 3, 6)

    "return json successfully" in {
      val freqSearchServ = FrequencyInformationSearchServiceHelper.create(searchByWordId = Future.successful(List(freqDto, freqDto2)))
      val api = create(freqSearchServ = freqSearchServ)
      val expect = Json.arr(
        Json.obj(
          "wordId" -> freqDto.wordId,
          "areaId" -> freqDto.areaId,
          "count" -> freqDto.count
        ),
        Json.obj(
          "wordId" -> freqDto2.wordId,
          "areaId" -> freqDto2.areaId,
          "count" -> freqDto2.count
        )
      )
      val result = api.frequencies_word(1).apply(FakeRequest())

      status(result) must_=== OK
      contentAsJson(result) must_=== expect
    }

    "return 'Internal Server Error' response if FrequencyInformationSearchService.searchByWordId failed with IOException" in {
      val freqSearchServ = FrequencyInformationSearchServiceHelper.create(searchByWordId = Future.failed(new IOException))
      val api = create(freqSearchServ = freqSearchServ)
      val result = api.frequencies_word(1).apply(FakeRequest())

      status(result) must_=== INTERNAL_SERVER_ERROR
      there was one(freqSearchServ).searchByWordId(===(1))
    }
  }

  "count_all" should {
    val dto = CountDto(10)

    "return json successfully" in {
      val countServ = CountServiceHelper.create(totalNumberOfCount = Future.successful(dto))
      val api = create(countServ = countServ)
      val expect = Json.obj("count" -> dto.count)
      val result = api.count_all.apply(FakeRequest())

      status(result) must_=== OK
      contentAsJson(result) must_=== expect
    }

    "return `Internal Server Error' response if CountService.totalNumberOfCount failed with IOException" in {
      val countServ = CountServiceHelper.create(totalNumberOfCount = Future.failed(new IOException))
      val api = create(countServ = countServ)
      val result = api.count_all.apply(FakeRequest())

      status(result) must_=== INTERNAL_SERVER_ERROR
      there was one(countServ).totalNumberOfCount
    }
  }

  "morphemes" should {
    val sentence = "dummy"
    val morphDto = MorphemeDto(1, "あああ", 3)
    val morphDto2 = MorphemeDto(2, "いいい", 5)

    "return json successfully" in {
      val morphServ = MorphologicalAnalysisServiceHelper.create(morphologicalAnalysis = Future.successful(List(morphDto, morphDto2)))
      val api = create(morphServ = morphServ)
      val expect = Json.arr(
        Json.obj(
          "wordId" -> morphDto.wordId,
          "text" -> morphDto.text,
          "count" -> morphDto.count
        ),
        Json.obj(
          "wordId" -> morphDto2.wordId,
          "text" -> morphDto2.text,
          "count" -> morphDto2.count
        )
      )
      val result = api.morphemes(sentence).apply(FakeRequest())

      status(result) must_=== OK
      contentAsJson(result) must_=== expect
    }

    "return json 'Internal Server Error' response if MorphologicalAnalysisService.morphologicalAnalysis failed with IOException" in {
      val morphServ = MorphologicalAnalysisServiceHelper.create(morphologicalAnalysis = Future.failed(new IOException))
      val api = create(morphServ = morphServ)
      val result = api.morphemes(sentence).apply(FakeRequest())

      status(result) must_=== INTERNAL_SERVER_ERROR
      there was one(morphServ).morphologicalAnalysis(===(sentence))
    }
  }

  "register_sentence" should {
    val sentence = "dummy"
    val lat = 34d
    val lng = 134d

    "return 'OK' response successfully" in {
      val freqUpdateServ = FrequencyInformationUpdateServiceHelper.create(registerSentence = Future.successful(()))
      val api = create(freqUpdateServ = freqUpdateServ)
      val result = api.register_sentence(sentence, lat, lng).apply(FakeRequest())

      status(result) must_=== OK
    }

    "return 'Internal Server Error' response if FrequencyInformationUpdateService.registerSentence failed with IOException" in {
      val freqUpdateServ = FrequencyInformationUpdateServiceHelper.create(registerSentence = Future.failed(new IOException))
      val api = create(freqUpdateServ = freqUpdateServ)
      val result = api.register_sentence(sentence, lat, lng).apply(FakeRequest())

      status(result) must_=== INTERNAL_SERVER_ERROR
      there was one(freqUpdateServ).registerSentence(===(sentence), ===(lat), ===(lng))
    }

    "return 'Not Found' response if FrequencyInformationUpdateService.registerSentence failed with NoSuchElementException" in {
      val freqUpdateServ = FrequencyInformationUpdateServiceHelper.create(registerSentence = Future.failed(new NoSuchElementException))
      val api = create(freqUpdateServ = freqUpdateServ)
      val result = api.register_sentence(sentence, lat, lng).apply(FakeRequest())

      status(result) must_=== NOT_FOUND
      there was one(freqUpdateServ).registerSentence(===(sentence), ===(lat), ===(lng))
    }
  }


}
