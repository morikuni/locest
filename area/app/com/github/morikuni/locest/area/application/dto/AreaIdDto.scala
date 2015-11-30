package com.github.morikuni.locest.area.application.dto

import com.github.morikuni.locest.area.domain.model.AreaId
import play.api.libs.json.{Json, Writes}

case class AreaIdDto(val id: Int)

object AreaIdDto {
  implicit val writes: Writes[AreaIdDto] = Json.writes[AreaIdDto]

  def from(id: AreaId): AreaIdDto = AreaIdDto(id.value)
}
