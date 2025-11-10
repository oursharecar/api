package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.ID
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.BsonDocument
import org.bson.Document
import java.net.Inet4Address

class MongoRepositoryTest : FunSpec({
    lateinit var mongodExecutable: MongodExecutable
    lateinit var mongodProcess: MongodProcess
    lateinit var client: MongoClient
    lateinit var collection: MongoCollection<Document>
    lateinit var repository: MongoRepository<Document>
    var port: Int = -1

    beforeSpec {
        val starter = MongodStarter.getDefaultInstance()
        port = Network.freeServerPort(Inet4Address.getLocalHost())
        val config = MongodConfig.builder()
            .version(Version.Main.PRODUCTION)
            .net(Net("localhost", port, Network.localhostIsIPv6()))
            .build()
        mongodExecutable = starter.prepare(config)
        mongodProcess = mongodExecutable.start()
        client = MongoClient.create("mongodb://localhost:$port")
        collection = client.getDatabase("core-test").getCollection("documents")
        repository = MongoRepository(collection)
    }

    afterSpec {
        client.close()
        mongodExecutable.stop()
        mongodProcess.stop()
    }

    beforeTest {
        collection.deleteMany(BsonDocument())
    }

    test("insert returns generated id and findById fetches the stored document") {
        val insertedId = repository.insert(Document(mapOf("name" to "Fleet")))

        val stored = repository.findById(ID<Document>(insertedId.id))

        stored?.getString("name") shouldBe "Fleet"
        stored?.getObjectId("_id")?.toHexString() shouldBe insertedId.id
    }

    test("deleteById removes the matching document and reports success") {
        val id = repository.insert(Document(mapOf("name" to "Alpha")))

        val deleted = repository.deleteById(ID(id.id))

        deleted shouldBe true
        repository.findById(ID<Document>(id.id)) shouldBe null
    }

    test("findAll emits every stored document") {
        repository.insert(Document(mapOf("name" to "A")))
        repository.insert(Document(mapOf("name" to "B")))

        val names = repository.findAll().map { it.getString("name") }.toList()

        names.sorted() shouldBe listOf("A", "B")
    }
})
