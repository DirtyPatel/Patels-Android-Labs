package algonquin.cst2335.pate0323;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ChatRoom.ChatMessage.class}, version=1)
public abstract class MessageDatabase extends RoomDatabase {
    public abstract ChatMessageDAO cmDAO();
}