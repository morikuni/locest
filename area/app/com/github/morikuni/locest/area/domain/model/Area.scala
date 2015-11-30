package com.github.morikuni.locest.area.domain.model

import com.github.morikuni.locest.util.{Property, Entity, Identifier}
import play.api.libs.json.{Json, JsValue, Writes}

case class AreaId(override val value: Int) extends Identifier[Int]

case class AreaProperty(
  val name: String,
  val coordinate: Coordinate
) extends Property

case class Area(
  override val id: AreaId,
  override val property: AreaProperty
) extends Entity[AreaId, AreaProperty] {

}