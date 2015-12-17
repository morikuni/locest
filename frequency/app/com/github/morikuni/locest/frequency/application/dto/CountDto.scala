package com.github.morikuni.locest.frequency.application.dto

import play.api.libs.json.{Json, Writes}

case class CountDto(val count: Long)

object CountDto {
  implicit val writes: Writes[CountDto] = Json.writes[CountDto]
}
