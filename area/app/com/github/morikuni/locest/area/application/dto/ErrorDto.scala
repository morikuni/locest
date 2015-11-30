package com.github.morikuni.locest.area.application.dto

import play.api.libs.json.{Json, Writes}

case class ErrorDto(error: String)

object ErrorDto {
  implicit val writes: Writes[ErrorDto] = Json.writes[ErrorDto]

  val internalServerError: ErrorDto = ErrorDto("Internal server error.")
}


