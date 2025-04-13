import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class WallServiceTest {

    @Before
    fun clearBeforeAddPost() {
        WallService.clear()
    }

    @Test
    fun addPost() {

        val service = WallService
        val result = service.addPost(
            Post(
                ownerId = 1,
                fromId = 1,
                text = "Hello World Again",
                can_edit = true,
                date = 1742682123,
                attachments = null,
                likes = Likes(0, false, true)
            )
        ).id

        assertEquals(1, result)
    }

    @Test
    fun updateTrue() {

        val service = WallService
        service.addPost(
            Post(
                ownerId = 1,
                fromId = 1,
                text = "Hello World Again",
                can_edit = true,
                date = 1742682123,
                attachments = null,
                likes = Likes(0, false, true)
            )
        )
        val result = service.update(Post(1,3,3,"This is new text", true, 1742682999, attachments = null, Likes(999, false,true)))
        assertEquals(true, result)
    }

    @Test
    fun updateFalse() {

        val service = WallService
        val result = service.update(Post(10,3,3,"This is new text", true, 1742682999, attachments = null, Likes(999, false,true)))
        assertEquals(false, result)
    }

    @Test
    fun createCommentOk() {
        val service = WallService
        service.addPost(
            Post(
                ownerId = 1,
                fromId = 1,
                text = "Hello World Again",
                can_edit = true,
                date = 1742682123,
                attachments = null,
                likes = Likes(0, false, true)
            )
        )
        service.createComment(
            postId = 1,
            Comment(
                fromId = 777,
                text = "Это супер-пупер пост!!!")
        )
    }

    @Test
    fun createCommentException() {
        val service = WallService
        service.addPost(
            Post(
                ownerId = 1,
                fromId = 1,
                text = "Hello World Again",
                can_edit = true,
                date = 1742682123,
                attachments = null,
                likes = Likes(0, false, true)
            )
        )
        service.createComment(
            postId = 11,
            Comment(
                fromId = 777,
                text = "Это супер-пупер пост!!!")
        )
    }

}