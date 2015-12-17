package com.github.morikuni.locest.frequency.domain.model

import com.github.morikuni.locest.util.{Entity, Identifier, Property}

case class WordId(override val value: Int) extends Identifier[Int]

/**
  *
  * @param text 単語を表す文字列
  */
case class WordProperty(
  val text: String
) extends Property

case class Word(override val id: WordId, override val property: WordProperty) extends Entity[WordId, WordProperty]