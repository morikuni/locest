package com.github.morikuni.locest.area.application.dto

import com.github.morikuni.locest.area.domain.model.Area
import play.api.libs.json.{Json, Writes}

case class AreaDto(val id: Int, val name: String, lattitude: Double, longitude: Double)

object AreaDto {
  implicit val writes: Writes[AreaDto] = Json.writes[AreaDto]

  def from(area: Area): AreaDto = AreaDto(area.id.value, area.property.name, area.property.coordinate.lat, area.property.coordinate.lng)
}
