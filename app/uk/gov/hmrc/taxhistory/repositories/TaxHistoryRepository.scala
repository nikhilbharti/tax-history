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

import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DefaultDB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}
import uk.gov.hmrc.taxhistory.model.taxhistory.PayAsYouEarnDetails

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TaxHistoryRepository extends Repository[PayAsYouEarnDetails, BSONObjectID] {
  def insertRecord(payeModel: PayAsYouEarnDetails):Future[Boolean]
}

class MongoRepository() (implicit mongo: () => DefaultDB)
  extends ReactiveRepository[PayAsYouEarnDetails, BSONObjectID](
    collectionName = "employment-history",
    mongo = mongo,
    domainFormat = Json.format[PayAsYouEarnDetails]) with TaxHistoryRepository {

  def insertRecord(payeModel: PayAsYouEarnDetails): Future[Boolean] = {
    collection.insert(payeModel) map { lastError =>
      Logger.debug(s"insertion failure :status${lastError.ok} errors: ${lastError.writeErrors}")
      lastError.ok
    }
  }
}

object TaxHistoryRepository extends MongoDbConnection {

  private lazy val repository = new MongoRepository

  def apply(): MongoRepository = repository
}

