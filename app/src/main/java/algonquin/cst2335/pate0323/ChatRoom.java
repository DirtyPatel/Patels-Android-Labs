package algonquin.cst2335.pate0323;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import algonquin.cst2335.pate0323.data.ChatRoomViewModel;
import algonquin.cst2335.pate0323.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.pate0323.databinding.ReceiveMessageBinding;
import algonquin.cst2335.pate0323.databinding.SentMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    protected RecyclerView.Adapter<MyRowHolder> myAdapter;
    protected RecyclerView recyclerView;
    protected EditText textInput;
    protected Button btn;

    protected Button rBtn;
    protected ArrayList<ChatMessage> messages;
    ChatRoomViewModel chatModel ;

    public static class ChatMessage {
        String message;
        String timeSent;
        boolean isSentButton;

        ChatMessage(String m, String t, boolean sent) {
            message = m;
            timeSent = t;
            isSentButton = sent;
        }

        public String getMessage() {
            return message;
        }

        public String getTimeSent() {
            return timeSent;
        }

        public boolean isSentButton() {
            return isSentButton;
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh:mm:ss a");
        return sdf.format(new Date());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        messages = chatModel.messages.getValue();

        btn = binding.sendButton;
        rBtn = binding.receiveButton;
        textInput = binding.textMessage;
        recyclerView = binding.recycleView;

        if (messages == null) {
            chatModel.messages.postValue(messages = new ArrayList<>());
        }

        btn.setOnClickListener(ck -> {
            boolean isSentButton = true;
            messages.add(new ChatMessage(textInput.getText().toString(), getCurrentTimestamp(), isSentButton));
            myAdapter.notifyDataSetChanged();
            textInput.setText("");
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        rBtn.setOnClickListener(ck -> {
            boolean isSentButton = false;
            messages.add(new ChatMessage(textInput.getText().toString(), getCurrentTimestamp(), isSentButton));
            myAdapter.notifyDataSetChanged();
            textInput.setText("");
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == 1) {
                    SentMessageBinding binding = SentMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(binding.getRoot());
                } else {
                    ReceiveMessageBinding binding = ReceiveMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(binding.getRoot());
                }
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                ChatMessage obj = messages.get(position);
                holder.theMessage.setText(obj.getMessage());
                holder.timestamp.setText(obj.getTimeSent());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

            @Override
            public int getItemViewType(int position) {
                ChatMessage message = messages.get(position);
                if (message.isSentButton()) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected static class MyRowHolder extends RecyclerView.ViewHolder {
        TextView theMessage;
        TextView timestamp;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            theMessage = itemView.findViewById(R.id.textMessageView);
            timestamp = itemView.findViewById(R.id.timestampView);
        }
    }
}
