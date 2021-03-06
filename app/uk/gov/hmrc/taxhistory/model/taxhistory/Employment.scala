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

package uk.gov.hmrc.taxhistory.model.taxhistory

import org.joda.time.LocalDate
import play.api.libs.json.Json

case class Employment(payeReference: String,
                      employerName: String,
                      startDate:LocalDate,
                      endDate:Option[LocalDate] = None,
                      taxablePayTotal: Option[BigDecimal] = None,
                      taxTotal: Option[BigDecimal] = None,
                      earlierYearUpdates: List[EarlierYearUpdate] = Nil,
                      companyBenefits: List[CompanyBenefit] = Nil)


case class CompanyBenefit(typeDescription: String, amount: BigDecimal, iabdMessageKey: String)


object CompanyBenefit {
  implicit val formats = Json.format[CompanyBenefit]
}


object Employment {
  implicit val formats = Json.format[Employment]
}


case class Allowance(typeDescription: String, amount: BigDecimal, iabdMessageKey: String)

object Allowance {
  implicit val formats = Json.format[Allowance]
}

case class PayAsYouEarnDetails(employments: List[Employment], allowances: List[Allowance] = Nil)

object PayAsYouEarnDetails {
  implicit val formats = Json.format[PayAsYouEarnDetails]
}
