package com.github.morikuni.locest.frequency.application.dto

import com.github.morikuni.locest.frequency.domain.model.Word
import play.api.libs.json.{Json, Writes}

case class MorphemeDto(wordId: Int, text: String, count: Int)

object MorphemeDto {
  implicit val writes: Writes[MorphemeDto] = Json.writes[MorphemeDto]

  def fromMap(m: Map[Word, Int]): Seq[MorphemeDto] = {
    m.toSeq.map { case (word, count) => MorphemeDto(word.id.value, word.property.text, count) }
  }
}