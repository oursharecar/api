import com.github.oursharecar.repository.mongodb.documents.AuditableDocument
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

data class UserDocument(
    @BsonId
    @BsonProperty("_id")
    val id: ObjectId? = null,
    val publicId: String,
    val auth0Id: String,
    val email: String,
    val displayName: String,
    val pictureUrl: String? = null,
    val audit: AuditableDocument
)