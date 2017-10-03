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

import org.joda.time.LocalDate
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import uk.gov.hmrc.mongo.MongoSpecSupport
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.taxhistory.model.taxhistory._

class MongoRepositorySpec extends UnitSpec with MockitoSugar with MongoSpecSupport with OneServerPerSuite {

  val companyBenefit = CompanyBenefit("Medical Insurance",22.0, "VanBenefit")
  val allowance = Allowance("FRE",22.0, "FlatRateJobExpenses")
  val startDate = new LocalDate("2016-02-21")

  val employment = Employment("employername","dddd", startDate, None, Some(22.00),Some(222.33),List(EarlierYearUpdate(BigDecimal(20.0), 10.0, LocalDate.now)))

  val payAsYouEarnDetails = PayAsYouEarnDetails(List(employment))
  "MongoRepository" must {
    "insert model successfully" in {

      await(TaxHistoryRepository().insertRecord(payAsYouEarnDetails)) should be(true)
    }
  }

}
