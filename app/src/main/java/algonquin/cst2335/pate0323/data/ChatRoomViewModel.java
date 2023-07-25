package algonquin.cst2335.pate0323.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.pate0323.ChatRoom;

public class ChatRoomViewModel extends ViewModel{
    public MutableLiveData<ArrayList<ChatRoom.ChatMessage>> messages = new MutableLiveData<>();
}