package com.github.oursharecar.repository.mongodb

import com.github.oursharecar.models.ID
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.BsonDocument
import org.bson.Document
import org.testcontainers.mongodb.MongoDBContainer
import org.testcontainers.utility.DockerImageName

class MongoRepositoryTest : FunSpec({
    lateinit var mongoContainer: MongoDBContainer
    lateinit var client: MongoClient
    lateinit var collection: MongoCollection<Document>
    lateinit var repository: MongoRepository<Document>

    beforeSpec {
        val image = DockerImageName.parse("mongo:7.0.14")
        mongoContainer = MongoDBContainer(image).apply { start() }
        client = MongoClient.create(mongoContainer.connectionString)
        collection = client.getDatabase("core-test").getCollection("documents")
        repository = MongoRepository(collection)
    }

    afterSpec {
        client.close()
        mongoContainer.stop()
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
