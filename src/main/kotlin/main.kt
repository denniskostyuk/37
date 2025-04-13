import com.sun.javafx.iio.ImageStorage.ImageType

fun main() {
    val like = Likes(0, false, true)

    val photo1:Attachment = PhotoAttachment("photo", Photo(1,1,"link 11", "link 12"))
    val video:Attachment = VideoAttachment("video", Video(1,1,"super vidos", 30))
    val audio:Attachment = AudioAttachment("audio", Audio(1,1,"super music", 30))
    val photo2:Attachment = PhotoAttachment("photo", Photo(2,1,"link 21", "link 22"))
    val arrayAttachments = arrayOf(photo1, video, audio, photo2)

    val post = Post(ownerId = 1, fromId = 1,text="Hello World", can_edit = true, date = 1742682123, attachments = null, likes=null)

    WallService.addPost(post)       // публикуем 1-й пост

    val post2 = Post(ownerId = 1, fromId = 1,text="Hello World Again", can_edit = true, date = 1742682123,attachments = null, likes=like)

    WallService.addPost(post2)      // публикуем 2-й пост

    WallService.addLike(1)      // даем лайк 1-му посту

    println(WallService.getPostInfo(1)) // посмотрим, как записались лайки к первому посту...

    // теперь обновим 1-й пост и посмотрим, что получилось
    WallService.update(Post(1,3,3,"This is new text", true, 1742682999, arrayAttachments, Likes(999, false,true)))
    println(WallService.getPostInfo(1))

    val comment1 = Comment(fromId = 777, text = "Das ist fantastischer Artikel")
    WallService.createComment(1, comment1)

}

data class Post(
    val id: Int = 0,            // Идентификатор записи.
    val ownerId: Int,           // Идентификатор владельца стены, на которой размещена запись.
    val fromId: Int,            // Идентификатор автора записи (от чьего имени опубликована запись).
    val text: String,           // Текст записи.
    val can_edit: Boolean,      // Информация о том, может ли текущий пользователь редактировать запись (1 — может, 0 — не может).
    val date: Int,              // Время публикации записи в формате unixtime.
    val attachments: Array<Attachment>?,
    val likes: Likes?
)

class Likes(
    val count: Int,              // число пользователей, которым понравилась запись;
    val userLikes: Boolean,      // наличие отметки «Мне нравится» от текущего пользователя
    val canLike: Boolean,        // информация о том, может ли текущий пользователь поставить отметку «Мне нравится»
)

interface Attachment {
    val type: String
}

class VideoAttachment(
    override val type: String,
    val video: Video
) :Attachment {

}

class AudioAttachment(
    override val type: String,
    val audio: Audio
) :Attachment {

}

class PhotoAttachment(
    override val type: String,
    val photo: Photo
) :Attachment {

}

class Video (
    val id: Int,
    val owner_id: Int,
    val title: String,
    val duration: Int
)

class Audio (
    val id: Int,
    val owner_id: Int,
    val title: String,
    val duration: Int
)

class Photo (
    val id: Int,
    val owner_id: Int,
    val photo_130: String,
    val photo_604: String
)

data class Comment(
    val id: Int = 0,                                     // Идентификатор записи.
    val replyToPost: Int = 0,                            // Идентификатор поста, в ответ на который оставлен текущий комментарий.
    val fromId: Int,                                     // Идентификатор автора записи (от чьего имени опубликован комментарий к посту).
    val text: String,                                    // Текст записи - комментария.
    val date: Long = System.currentTimeMillis()/1000     // Время публикации записи в формате unixtime.
)

class PostNotFoundException(message:String) : RuntimeException(message)

object WallService {

    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()

    fun addPost(post:Post): Post {
        posts += post.copy(id=getMaxPostId()+1)
        return posts.last()
    }

    fun createComment(postId: Int, comment: Comment): Comment {
        var ifPostExist = false
        for (post in posts) {
            if (post.id == postId) {
                comments += comment.copy(id=getMaxCommentId()+1, replyToPost=postId)
                ifPostExist = true
            }
        }
        if (ifPostExist==false) {
            throw PostNotFoundException ("Не найден пост с ID=${postId}")
        }
        return comments.last()
    }

    fun update(post: Post): Boolean {
        for ((index, postedPost) in posts.withIndex()) {
            if (postedPost.id == post.id) {
                posts[index] = post.copy(ownerId = post.ownerId, fromId = post.fromId, text = post.text, can_edit = post.can_edit, date = post.date, attachments = post.attachments, likes = post.likes)
                return true
            }
        }
        return false
    }

    fun addLike(id:Int) {
        for ((index, post) in posts.withIndex()) {
            if (post.id == id) {
                posts[index] = post.copy(likes=Likes((post.likes?.count ?: 0)+1, true, false))
            }
        }
    }

    fun getMaxPostId(): Int {
        var maxId=0;
        for (post in posts) {
            if (post.id > maxId) {
                maxId = post.id
            }
        }
        return maxId
    }

    fun getMaxCommentId(): Int {
        var maxId=0;
        for (comment in comments) {
            if (comment.id > maxId) {
                maxId = comment.id
            }
        }
        return maxId
    }

    fun getPostInfo(id:Int): String {
        var result = "Нет поста с таким ID"
        for (post in posts) {
            if (post.id == id) {
                result = "У поста `${post.text}` количество лайков = ${post.likes?.count ?: 0} и ${getAttachmentInfo(id)}"
            }
        }
        return result
    }

    fun getAttachmentInfo(postId:Int): String {
        var result = "нет вложений"
        for (post in posts) {
            if (post.id == postId && post.attachments != null) {
                var totalVideo:Int = 0
                var totalAudio:Int = 0;
                var totalPhoto:Int = 0;
                for (attachment in post.attachments) {

                    if (attachment is VideoAttachment) {
                        totalVideo++
                    }
                    if (attachment is AudioAttachment) {
                        totalAudio++
                    }
                    if (attachment is PhotoAttachment) {
                        totalPhoto++
                    }
                    result = "количество вложений: Видео = ${totalVideo}, Аудио = ${totalAudio}, Фото = ${totalPhoto}"
                }
            }
        }
        return result
    }

    fun clear() {
        posts = emptyArray()
    }


}