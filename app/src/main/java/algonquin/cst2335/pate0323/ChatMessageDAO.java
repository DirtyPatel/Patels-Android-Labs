package algonquin.cst2335.pate0323;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatMessageDAO {

    @Insert
    long insertMessage(ChatRoom.ChatMessage m);

    @Query("Select * From ChatMessage")
    List<ChatRoom.ChatMessage> getAllMessages();

    @Delete
    int deleteMessage(ChatRoom.ChatMessage m);
}