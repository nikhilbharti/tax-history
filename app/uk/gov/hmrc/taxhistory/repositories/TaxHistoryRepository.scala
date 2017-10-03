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
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.ImplicitBSONHandlers._
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.taxhistory.model.api.PAYEForAgentDetails
import reactivemongo.api.commands.WriteConcern

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait TaxHistoryRepository {
  def insertRecord(payeModel: PAYEForAgentDetails): Future[Boolean]

  def fetchRecords(nino: String, taxYear: Int): Future[Option[PAYEForAgentDetails]]

  def removeRecords(): Future[Unit]
}

class MongoRepository()(implicit mongo: () => DefaultDB)
  extends ReactiveRepository[PAYEForAgentDetails, BSONObjectID](
    collectionName = "employment-history",
    mongo = mongo,
    domainFormat = Json.format[PAYEForAgentDetails]) with TaxHistoryRepository {

  override def indexes: Seq[Index] = {
    Seq(Index(Seq("taxYear" -> IndexType.Ascending)))
  }

  override def insertRecord(payeModel: PAYEForAgentDetails): Future[Boolean] = {
    collection.insert(payeModel) map { lastError =>
      Logger.debug(s"insertion failure :status${lastError.ok} errors: ${lastError.writeErrors}")
      lastError.ok
    }
  }

  override def fetchRecords(nino: String, taxYear: Int): Future[Option[PAYEForAgentDetails]] = {
    collection.find(Json.obj("nino" -> nino,
      "taxYears.taxYear" -> taxYear)).sort(Json.obj("_id" -> -1)).one[PAYEForAgentDetails]
  }

  override def removeRecords():Future[Unit] = {
    removeAll(WriteConcern.Acknowledged).map { _ =>}
  }
}

object TaxHistoryRepository extends MongoDbConnection {

  private lazy val repository = new MongoRepository

  def apply(): MongoRepository = repository
}

