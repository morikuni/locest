package com.github.morikuni.locest.frequency.application.service

import com.github.morikuni.locest.frequency.application.dto.CountDto
import scala.concurrent.Future

trait CountService {
  /** 合計出現回数を取得する
    *
    * @return Future.successful(CountDto) 成功時
    *         Future.failed(IOException) 入出力に失敗したとき
    */
  def totalNumberOfCount: Future[CountDto]
}

trait DependCountService {
  def countService: CountService
}
