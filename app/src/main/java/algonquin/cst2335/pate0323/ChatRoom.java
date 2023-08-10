package algonquin.cst2335.pate0323;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.pate0323.data.ChatRoomViewModel;
import algonquin.cst2335.pate0323.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.pate0323.databinding.ReceiveMessageBinding;
import algonquin.cst2335.pate0323.databinding.SentMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    protected RecyclerView.Adapter myAdapter;
    protected RecyclerView recyclerView;
    protected EditText textInput;
    protected Button btn;
    protected Button rBtn;
    ArrayList<ChatMessage> messages;
    MessageDatabase db;
    ChatMessageDAO mDAO;
    ChatRoomViewModel chatModel;

    @Entity
    public static class ChatMessage {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        public long id;
        @ColumnInfo(name = "message")
        String message;
        @ColumnInfo(name = "timeSet")
        String timeSent;
        @ColumnInfo(name = "isSentButton")
        boolean isSentButton;

        public ChatMessage() {
        }

        public ChatMessage(String m, String t, boolean sent) {
            message = m;
            timeSent = t;
            isSentButton = sent;
        }

        public void setMessage(String m) {
            this.message = m;
        }

        public void setTimeSent(String t) {
            this.timeSent = t;
        }

        public void isSentButton(boolean sent) {
            this.isSentButton = sent;
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
        db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        mDAO = db.cmDAO();
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        chatModel.selectedMessage.observe(this, newMessage ->{

            if(newMessage != null) {
                MessageDetailsFragment detailsFragment = new MessageDetailsFragment(newMessage);
                FragmentManager fgmt = getSupportFragmentManager();
                FragmentTransaction tx = fgmt.beginTransaction();
                tx.addToBackStack("");
                tx.replace(R.id.frameView, detailsFragment);
                tx.commit();
            }
        });
        messages = chatModel.messages.getValue();
        btn = binding.sendButton;
        rBtn = binding.receiveButton;
        textInput = binding.textMessage;
        recyclerView = binding.recycleView;

        if (messages == null) {
            chatModel.messages.setValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll(mDAO.getAllMessages()); //Once you get the data from database

                runOnUiThread(() -> binding.recycleView.setAdapter(myAdapter)); //You can then load the RecyclerView
            });
        }


        btn.setOnClickListener(ck -> {
            boolean isSentButton = true;
            ChatMessage messageAdd = new ChatMessage(textInput.getText().toString(), getCurrentTimestamp(), isSentButton);

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> messageAdd.id = mDAO.insertMessage(messageAdd));
            messages.add(messageAdd);
            myAdapter.notifyDataSetChanged();
            textInput.setText("");
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        rBtn.setOnClickListener(ck -> {
            boolean isSentButton = false;
            ChatMessage messageAdd = new ChatMessage(textInput.getText().toString(), getCurrentTimestamp(), isSentButton);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> messageAdd.id = mDAO.insertMessage(messageAdd));
            messages.add(messageAdd);
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

            public int getItemViewType(int position) {
                ChatMessage message = messages.get(position);
                if (message.isSentButton()) {
                    return 1;
                } else {
                    return 2;
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

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.item_1) {
            deleteMessagePrompt();
            return true;
        } else if (itemId == R.id.item_2) {
            showVersionInfo();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void deleteMessagePrompt() {
        int selectedPosition = recyclerView.getChildLayoutPosition(recyclerView.getFocusedChild());

        if (selectedPosition == -1 || selectedPosition >= messages.size()) {
            Toast.makeText(this, "Please select a valid message to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Question:")
                .setMessage("Do you want to delete the message: " + messages.get(selectedPosition).getMessage())
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialog, which) -> deleteMessage(selectedPosition))
                .show();
    }

    private void deleteMessage(int position) {
        ChatMessage toDelete = messages.get(position);
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            mDAO.deleteMessage(toDelete);
            messages.remove(position);
            runOnUiThread(() -> {
                myAdapter.notifyDataSetChanged();
                showUndoSnackbar(position, toDelete);
            });
        });
    }

    private void showUndoSnackbar(int position, ChatMessage message) {
        Snackbar.make(textInput, "Deleted message #" + position, Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> restoreMessage(position, message))
                .show();
    }

    private void restoreMessage(int position, ChatMessage message) {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            mDAO.insertMessage(message);
            messages.add(position, message);
            runOnUiThread(() -> myAdapter.notifyDataSetChanged());
        });
    }

    private void showVersionInfo() {
        String words = "Version 1.0, created by Dharti Patel";
        Toast.makeText(this, words, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView theMessage;
        TextView timestamp;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(clk -> {

                int index = getAbsoluteAdapterPosition();
                chatModel.selectedMessage.postValue(messages.get(index));

                /*AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoom.this);
                builder.setMessage("Do you want to delete the message: " + theMessage.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No",((dialog, cl) -> {}))
                        .setPositiveButton("Yes", (dialog, which) -> {
                            int position = getAbsoluteAdapterPosition();
                            ChatMessage toDelete = messages.get(position);
                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() -> {
                                mDAO.deleteMessage(toDelete);
                                messages.remove(position);
                                runOnUiThread(()-> myAdapter.notifyDataSetChanged());
                                Snackbar.make(textInput, "Deleted message #" + position, Snackbar.LENGTH_LONG)
                                        .setAction("Undo", cl->{
                                            thread.execute(() -> {
                                                mDAO.insertMessage(toDelete);
                                                messages.add(position, toDelete);
                                            });
                                            runOnUiThread(() ->myAdapter.notifyDataSetChanged());
                                        })
                                        .show();
                            });
                        })
                        .create().show();*/
            });
            theMessage = itemView.findViewById(R.id.textMessageView);
            timestamp = itemView.findViewById(R.id.timestampView);
        }
    }
}