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

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.taxhistory.model.rti.TestUtil

/**
  * Created by Shailesh on 13/06/17.
  */
class NpsEmploymentSpec extends TestUtil with UnitSpec {

  lazy val employmentsResponse = loadFile("/json/nps/response/employments.json")


  val employmentResponse = """ {
                             |    "nino": "AA0000000",
                             |    "sequenceNumber": 6,
                             |    "worksNumber": "00191048716",
                             |    "taxDistrictNumber": "846",
                             |    "payeNumber": "T2PP",
                             |    "employerName": "Aldi"
                             |    }
                             """.stripMargin



  "NpsEmployment" should {
    "transform Nps Employment Response Json correctly to Employment Model " in {
      val employment = Json.parse(employmentResponse).as[NpsEmployment]
      employment shouldBe a[NpsEmployment]
      println(employment)
    }
  }
}
