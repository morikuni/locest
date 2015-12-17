package com.github.morikuni.locest.frequency.domain.model

import com.github.morikuni.locest.util.{Entity, Identifier, Property}

case class FrequencyInformationId(override val value: (WordId, AreaId)) extends Identifier[(WordId, AreaId)] {
  def wordId: WordId = value._1
  def areaId: AreaId = value._2
}

/**
  *
  * @param count 出現回数
  */
case class FrequencyInformationProperty(
  val count: Int
) extends Property

case class FrequencyInformation(override val id: FrequencyInformationId, override val property: FrequencyInformationProperty) extends Entity[FrequencyInformationId, FrequencyInformationProperty]

