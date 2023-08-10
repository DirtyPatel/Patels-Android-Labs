package algonquin.cst2335.pate0323;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.pate0323.databinding.MessageDetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {

    ChatRoom.ChatMessage thisMessage;

    public MessageDetailsFragment(ChatRoom.ChatMessage toShow){
        thisMessage = toShow;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle instance){
        super.onCreateView(inflater, parent, instance);
        MessageDetailsLayoutBinding binding = MessageDetailsLayoutBinding.inflate(inflater);
        binding.messageView.setText(thisMessage.message);
        binding.timeStamp.setText(thisMessage.timeSent);
        binding.idView.setText(Long.toString(thisMessage.id));
        return binding.getRoot();
    }
}