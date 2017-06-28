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

package uk.gov.hmrc.taxhistory.services

import org.mockito.Matchers
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsArray, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.tai.model.rti.RtiData
import uk.gov.hmrc.taxhistory.connectors.des.RtiConnector
import uk.gov.hmrc.taxhistory.connectors.nps.EmploymentsConnector
import uk.gov.hmrc.taxhistory.model.nps.NpsEmployment
import uk.gov.hmrc.taxhistory.model.taxhistory.Employment
import uk.gov.hmrc.taxhistory.model.utils.TestUtil
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.Future

/**
  * Created by shailesh on 20/06/17.
  */

class EmploymentServiceSpec extends PlaySpec with MockitoSugar with TestUtil{
  private val mockEmploymentConnector= mock[EmploymentsConnector]
  private val mockRtiDataConnector= mock[RtiConnector]

  implicit val hc = HeaderCarrier()
  val testNino = randomNino()
  object TestEmploymentService extends EmploymentHistoryService {
    override def employmentsConnector: EmploymentsConnector = mockEmploymentConnector
    override def rtiConnector: RtiConnector = mockRtiDataConnector
  }

  val npsEmploymentResponse =  Json.parse(""" [{
                             |    "nino": "AA000000",
                             |    "sequenceNumber": 6,
                             |    "worksNumber": "6044041000000",
                             |    "taxDistrictNumber": "531",
                             |    "payeNumber": "J4816",
                             |    "employerName": "Aldi"
                             |    }]
                           """.stripMargin)
  lazy val rtiEmploymentResponse = loadFile("/json/rti/response/dummyRti.json")
  lazy val rtiDuplicateEmploymentsResponse = loadFile("/json/rti/response/dummyRtiDuplicateEmployments.json")
  lazy val rtiPartialDuplicateEmploymentsResponse = loadFile("/json/rti/response/dummyRtiPartialDuplicateEmployments.json")
  lazy val rtiNonMatchingEmploymentsResponse = loadFile("/json/rti/response/dummyRtiNonMatchingEmployment.json")
  lazy val rtiNoPaymentsResponse = loadFile("/json/rti/response/dummyRtiNoPaymentsResponse.json")
  lazy val npsEmptyEmployments = loadFile("/json/nps/response/emptyEmployments.json")

  "Employment Service" should {
    "successfully get Nps Employments Data" in {
      when(mockEmploymentConnector.getEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(OK, Some(npsEmploymentResponse))))

      val eitherResponse = await(TestEmploymentService.getNpsEmployments(testNino, TaxYear(2016)))
      assert(eitherResponse.isRight)
      eitherResponse.right.get mustBe a[List[NpsEmployment]]

    }

    "handle any non success status response from get Nps Employments" in {
      when(mockEmploymentConnector.getEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(npsEmploymentResponse))))

      val eitherResponse = await(TestEmploymentService.getNpsEmployments(testNino, TaxYear(2016)))
      assert(eitherResponse.isLeft)
      eitherResponse.left.get mustBe a[HttpResponse]
    }

    "successfully get Rti Employments Data" in {
      when(mockRtiDataConnector.getRTIEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(OK, Some(rtiEmploymentResponse))))

      val eitherResponse = await(TestEmploymentService.getRtiEmployments(testNino, TaxYear(2016)))
      assert(eitherResponse.isRight)
      eitherResponse.right.get mustBe a[RtiData]
    }

    "handle any non success status response from get Rti Employments" in {
      when(mockRtiDataConnector.getRTIEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(rtiEmploymentResponse))))

      val eitherResponse = await(TestEmploymentService.getRtiEmployments(testNino, TaxYear(2016)))
      assert(eitherResponse.isLeft)
      eitherResponse.left.get mustBe a[HttpResponse]
    }

    "return any non success status response from get Nps Employments api" in {
      when(mockEmploymentConnector.getEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(npsEmploymentResponse))))
      val response =  await(TestEmploymentService.getEmploymentHistory(testNino.toString(),2016))
      response mustBe a[HttpResponse]
      response.status mustBe BAD_REQUEST
    }

    "return not found status response from get Nps Employments api" in {
      when(mockEmploymentConnector.getEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(OK, Some(JsArray(Seq.empty)))))
      val response =  await(TestEmploymentService.getEmploymentHistory(testNino.toString(),2016))
      response mustBe a[HttpResponse]
      response.status mustBe NOT_FOUND
    }

    "return any non success status response from get Rti Employments api" in {
      when(mockEmploymentConnector.getEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(OK, Some(npsEmploymentResponse))))
      when(mockRtiDataConnector.getRTIEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(rtiEmploymentResponse))))
      val response =  await(TestEmploymentService.getEmploymentHistory(testNino.toString(),2016))
      response mustBe a[HttpResponse]
      response.status mustBe BAD_REQUEST
    }

    "return success response from get Employments" in {
      when(mockEmploymentConnector.getEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(OK, Some(npsEmploymentResponse))))
      when(mockRtiDataConnector.getRTIEmployments(Matchers.any(), Matchers.any())(Matchers.any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(OK, Some(rtiEmploymentResponse))))
      val response =  await(TestEmploymentService.getEmploymentHistory(testNino.toString(),2016))
      response mustBe a[HttpResponse]
      response.status mustBe OK
      val employments = response.json.as[List[Employment]]
      employments.size mustBe 1
      employments.head.employerName mustBe "Aldi"
      employments.head.payeReference mustBe "J4816"
      employments.head.taxablePayTotal mustBe BigDecimal.valueOf(20000.00)
      employments.head.taxTotal mustBe BigDecimal.valueOf(1880.00)
      employments.head.taxablePayEYU mustBe None
      employments.head.taxEYU mustBe None
    }

    "successfully merge rti and nps employment data into employment list" in {
      val rtiData = rtiEmploymentResponse.as[RtiData]
      val npsEmployments = npsEmploymentResponse.as[List[NpsEmployment]]

      val employmentList =TestEmploymentService.createEmploymentList(rtiData = rtiData, npsEmployments = npsEmployments)
      employmentList.size mustBe 1
      employmentList.head.employerName mustBe "Aldi"
      employmentList.head.payeReference mustBe "J4816"
      employmentList.head.taxablePayTotal mustBe BigDecimal.valueOf(20000.00)
      employmentList.head.taxTotal mustBe BigDecimal.valueOf(1880.00)
      employmentList.head.taxablePayEYU mustBe None
      employmentList.head.taxEYU mustBe None
    }

    "return empty list if there are multiple matching rti employments for a single nps employment" in {
      val rtiData = rtiDuplicateEmploymentsResponse.as[RtiData]
      val npsEmployments = npsEmploymentResponse.as[List[NpsEmployment]]

      val employmentList =TestEmploymentService.createEmploymentList(rtiData = rtiData, npsEmployments = npsEmployments)
      employmentList.size mustBe 0
    }

    "successfully merge if there are multiple matching rti employments for a single nps employment but single match on currentPayId" in {
      val rtiData = rtiPartialDuplicateEmploymentsResponse.as[RtiData]
      val npsEmployments = npsEmploymentResponse.as[List[NpsEmployment]]

      val employmentList =TestEmploymentService.createEmploymentList(rtiData = rtiData, npsEmployments = npsEmployments)
      employmentList.size mustBe 1
    }

    "return empty list if there are zero matching rti employments for a single nps employment" in {
      val rtiData = rtiNonMatchingEmploymentsResponse.as[RtiData]
      val npsEmployments = npsEmploymentResponse.as[List[NpsEmployment]]

      val employmentList =TestEmploymentService.createEmploymentList(rtiData = rtiData, npsEmployments = npsEmployments)
      employmentList.size mustBe 0
    }

    "return empty list if there are zero matching rti payments within the matching employment" in {
      val rtiData = rtiNoPaymentsResponse.as[RtiData]
      val npsEmployments = npsEmploymentResponse.as[List[NpsEmployment]]

      val employmentList =TestEmploymentService.createEmploymentList(rtiData = rtiData, npsEmployments = npsEmployments)
      employmentList.size mustBe 0
    }
  }


}
