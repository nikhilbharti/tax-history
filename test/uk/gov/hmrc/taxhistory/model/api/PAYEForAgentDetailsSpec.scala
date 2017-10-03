package uk.gov.hmrc.taxhistory.model.api

import java.time.LocalDate
import java.util.UUID

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import uk.gov.hmrc.taxhistory.model.utils.TestUtil

class PAYEForAgentDetailsSpec extends PlaySpec with TestUtil {

  "PAYEForAgentDetails" must {

    lazy val employment1 =  Employment(
      employmentId = UUID.fromString("01318d7c-bcd9-47e2-8c38-551e7ccdfae3"),
      payeReference = "paye-1",
      employerName = "employer-1",
      startDate = LocalDate.parse("2016-01-21"),
      endDate = Some(LocalDate.parse("2017-01-01")))

    val taxYears = List(TaxYear(2010, List(employment1)))

    lazy val payeForAgentDetails = loadFile("/json/model/api/PAYEForAgentsDetails.json")

    "serialize the model successfully" in {
      Json.toJson(PAYEForAgentDetails("A111111B", taxYears)) must be(payeForAgentDetails)

    }

    "de-serialise the model successfully" in {
      Json.fromJson[PAYEForAgentDetails](payeForAgentDetails) must be(PAYEForAgentDetails("A111111B", taxYears))
    }
  }

}
