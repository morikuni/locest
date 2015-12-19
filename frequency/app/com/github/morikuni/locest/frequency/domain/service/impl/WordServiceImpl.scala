package com.github.morikuni.locest.frequency.domain.service.impl

import com.github.morikuni.locest.frequency.domain.model.{Word, WordProperty}
import com.github.morikuni.locest.frequency.domain.repository.impl.{InjectWordRepository, InjectWordRepositoryTransactionManager}
import com.github.morikuni.locest.frequency.domain.repository.{DependWordRepository, DependWordRepositoryTransactionManger}
import com.github.morikuni.locest.frequency.domain.service.{DependWordService, WordService}
import com.github.morikuni.locest.frequency.domain.support.ExecutionContextProvider
import com.github.morikuni.locest.util.Transaction
import org.atilika.kuromoji.Tokenizer
import scala.concurrent.Future

trait WordServiceImpl extends WordService
  with DependWordRepository
  with DependWordRepositoryTransactionManger {

  val tokenizer = Tokenizer.builder().build()

  /** 形態素解析する
    *
    * @param sentence 形態素解析する文書
    * @return Future.successful(Map(Word -> Int)) (単語, 出現回数)のMap
    */
  override def morphologicalAnalysis(sentence: String)(implicit ecp: ExecutionContextProvider): Future[Map[Word, Int]] = {
    import scala.collection.JavaConversions.asScalaBuffer
    import ecp.default

    val textToSize = asScalaBuffer(tokenizer.tokenize(sentence))
      .map(_.getSurfaceForm)
      .groupBy(identity)
      .mapValues(b => b.size)

    val tx = Transaction.sequence(textToSize.keySet.map(text => wordRepository.create(WordProperty(text))))

    wordRepositoryTransactionManager.execute(tx)(ecp.repository)
      .map { seq =>
        seq.map { word =>
          word -> textToSize(word.property.text)
        }.toMap
      }
  }
}

trait InjectWordService extends DependWordService {
  override val wordService: WordService =
    new WordServiceImpl
      with InjectWordRepository
      with InjectWordRepositoryTransactionManager
}