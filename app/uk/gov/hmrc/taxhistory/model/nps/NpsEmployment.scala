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

package uk.gov.hmrc.taxhistory.model.nps

import org.joda.time.LocalDate
import play.api.libs.json._
import uk.gov.hmrc.taxhistory.model.utils.JsonUtils

/**
  * Created by shailesh on 15/06/17.
  */


case class NpsEmployment(nino:String,
                      sequenceNumber:Int,
                      taxDistrictNumber:String,
                      payeNumber:String,
                      employerName:String,
                      worksNumber: Option[String]= None,
                      receivingJobSeekersAllowance: Boolean = false,
                      otherIncomeSourceIndicator: Boolean = false,
                      startDate: LocalDate,
                      endDate: Option[LocalDate] = None
                    )

object NpsEmployment {
  implicit val reader = new Reads[NpsEmployment] {
    def reads(js: JsValue): JsResult[NpsEmployment] = {
      val startDate = (js \ "startDate").as[LocalDate](JsonUtils.npsDateFormat)
      val endDate = (js \ "endDate").asOpt[LocalDate](JsonUtils.npsDateFormat)
      for {
        nino <- (js \ "nino").validate[String]
        sequenceNumber <- (js \ "sequenceNumber" ).validate[Int]
        taxDistrictNumber <- (js \ "taxDistrictNumber").validate[String]
        payeNumber <- (js \ "payeNumber").validate[String]
        employerName <- (js \ "employerName").validate[String]
        worksNumber <- (js \ "worksNumber").validateOpt[String]
        receivingJobSeekersAllowance <- (js \ "receivingJobseekersAllowance").validate[Boolean]
        otherIncomeSourceIndicator <- (js \ "otherIncomeSourceIndicator").validate[Boolean]
      } yield {
        NpsEmployment(
        nino = nino,
        sequenceNumber = sequenceNumber,
        taxDistrictNumber = taxDistrictNumber,
        payeNumber = payeNumber,
        employerName = employerName,
        worksNumber = worksNumber,
        receivingJobSeekersAllowance = receivingJobSeekersAllowance,
        otherIncomeSourceIndicator  = otherIncomeSourceIndicator,
        startDate = startDate,
        endDate = endDate
        )
      }
    }
  }
  implicit val writer = Json.writes[NpsEmployment]
}
