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

package uk.gov.hmrc.taxhistory.repositories

import java.time.LocalDate
import java.util.UUID

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import uk.gov.hmrc.mongo.MongoSpecSupport
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.taxhistory.model.api.{Employment, PAYEForAgentDetails, TaxYear}

class TaxHistoryRepositorySpec extends UnitSpec with MockitoSugar with MongoSpecSupport with OneServerPerSuite {

  val taxYear = 2010
  lazy val employment1 =  Employment(
    employmentId = UUID.fromString("01318d7c-bcd9-47e2-8c38-551e7ccdfae3"),
    payeReference = "paye-1",
    employerName = "employer-1",
    startDate = LocalDate.parse("2016-01-21"),
    endDate = Some(LocalDate.parse("2017-01-11")))

  val taxYears = List(TaxYear(taxYear, List(employment1)))

  "MongoRepository" must {
    "insert record successfully" in {
      await(TaxHistoryRepository().insertRecord(PAYEForAgentDetails("A111111B", taxYears))) should be(true)
    }


    "fetch records successfully for the input nino and tax year" in {
      await(TaxHistoryRepository().fetchRecords("A111111B", taxYear)) should be(
        Some(PAYEForAgentDetails("A111111B", List(TaxYear(taxYear, List(Employment(UUID.fromString("01318d7c-bcd9-47e2-8c38-551e7ccdfae3"),
          LocalDate.parse("2016-01-21"),
          Some(LocalDate.parse("2017-01-11")), "paye-1", "employer-1"))))))

      )
    }

    "remove all records successfully" in {
      await(TaxHistoryRepository().removeRecords()) should be()

    }
  }

}
