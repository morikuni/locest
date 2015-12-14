package com.github.morikuni.locest.frequency.application.dto

import com.github.morikuni.locest.frequency.domain.model.FrequencyInformation
import play.api.libs.json.{Json, Writes}

case class FrequencyInformationDto(val wordId: Int, val areaId: Int, count: Int)

object FrequencyInformationDto {
  implicit val writes: Writes[FrequencyInformationDto] = Json.writes[FrequencyInformationDto]

  def from(fi: FrequencyInformation): FrequencyInformationDto = FrequencyInformationDto(fi.id.wordId.value, fi.id.areaId.value, fi.property.count)
}
