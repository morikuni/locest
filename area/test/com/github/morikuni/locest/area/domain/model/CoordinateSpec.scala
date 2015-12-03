package com.github.morikuni.locest.area.domain.model

import org.specs2.mutable.Specification


class CoordinateSpec extends Specification {
  "create" should {
    "return Coordinate successfully" in {
      val lat = 90d
      val lng = 180d
      val coordinate = Coordinate.create(lat, lng).get

      coordinate must be equalTo Coordinate.createUnsafe(lat, lng)
    }

    "fail with IllegalArgumentException" in {

      def fail(lat: Double, lng: Double) = {
        val coordinateTry = Coordinate.create(lat ,lng)
        coordinateTry.get must throwA[IllegalArgumentException]
      }

      "when lat is too big" in {
        fail(91d, 180d)
      }

      "when lat is too small" in {
        fail(-91d, 180d)
      }

      "when lng is too big" in {
        fail(90d, 181d)
      }

      "when lng is too small" in {
        fail(90d, -181d)
      }
    }
  }

  "createUnsafe" should {
    "return Coordinate successfully" in {
      val lat = 90d
      val lng = 180d
      val coordinate = Coordinate.createUnsafe(lat, lng)

      coordinate.lat must be equalTo lat
      coordinate.lng must be equalTo lng
    }
  }
}
