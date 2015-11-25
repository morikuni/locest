package com.github.morikuni.locest.area.domain.model

import scala.util.{Success, Failure, Try}

/** 緯度経度で座標を表す。
  * 南緯を負値、北緯を正値で表し、
  * 西経を負値、東経を正値で表す。
  *
  * @param lat 緯度(-90 <= lat <= 90)
  * @param lng 経度(-180 <= lng <= 180)
  */
case class Coordinate private(val lat: Double, val lng: Double)

object Coordinate {

  /** Coordinate をバリデーション付きで作成する。
    *
    * @param lat 緯度
    * @param lng 経度
    * @return Success(Coordinate) 緯度経度が正しかった時
    *         Failure(IllegalArgumentException) 緯度経度が不正な値だった場合
    */
  def create(lat: Double, lng: Double): Try[Coordinate] = {
    if(lat > 90 || lat < -90) Failure(new IllegalArgumentException("lat must be (-90 <= lat <= 90)"))
    else if(lng > 180 || lng < -180) Failure(new IllegalArgumentException("lng must be (-180 <= lng <= 180)"))
    else Success(Coordinate(lat, lng))
  }
}