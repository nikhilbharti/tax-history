/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.taxhistory.metrics

import com.codahale.metrics.Timer
import com.codahale.metrics.Timer.Context
import uk.gov.hmrc.play.graphite.MicroserviceMetrics
import uk.gov.hmrc.taxhistory.metrics.MetricsEnum.MetricsEnum

trait TaxHistoryMetrics {

  def startTimer(api: MetricsEnum): Timer.Context

  def incrementSuccessCounter(api: MetricsEnum): Unit

  def incrementFailedCounter(api: MetricsEnum): Unit

}


object TaxHistoryMetrics extends TaxHistoryMetrics with MicroserviceMetrics {
  val registry = metrics.defaultRegistry
  val timers = Map(
    MetricsEnum.NPS_GET_EMPLOYMENTS -> registry.timer("nps-get-employments-response-timer"),
    MetricsEnum.RTI_GET_EMPLOYMENTS -> registry.timer("rti-get-employments-response-timer"),
    MetricsEnum.NPS_GET_IABDS -> registry.timer("rti-get-iabds-response-timer")

  )
  val successCounters = Map(
    MetricsEnum.NPS_GET_EMPLOYMENTS -> registry.counter("nps-get-employments-success-counter"),
    MetricsEnum.RTI_GET_EMPLOYMENTS -> registry.counter("rti-get-employments-success-counter"),
      MetricsEnum.NPS_GET_IABDS -> registry.counter("rti-get-iabds-success-counter")

  )
  val failedCounters = Map(
    MetricsEnum.NPS_GET_EMPLOYMENTS -> registry.counter("nps-get-employments-failed-counter"),
    MetricsEnum.RTI_GET_EMPLOYMENTS -> registry.counter("rti-get-employments-failed-counter"),
      MetricsEnum.NPS_GET_IABDS -> registry.counter("rti-get-iabds-failed-counter")

  )

  override def startTimer(api: MetricsEnum): Context = timers(api).time()

  override def incrementSuccessCounter(api: MetricsEnum): Unit = successCounters(api).inc()

  override def incrementFailedCounter(api: MetricsEnum): Unit = failedCounters(api).inc()

}