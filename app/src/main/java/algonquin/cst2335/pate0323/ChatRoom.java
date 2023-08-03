package algonquin.cst2335.pate0323;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.pate0323.data.ChatMessage;
import algonquin.cst2335.pate0323.data.ChatMessageDAO;
import algonquin.cst2335.pate0323.data.ChatRoomViewModel;
import algonquin.cst2335.pate0323.data.MessageDatabase;
import algonquin.cst2335.pate0323.data.MessageDetailsFragment;
import algonquin.cst2335.pate0323.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.pate0323.databinding.ReceiveMessageBinding;
import algonquin.cst2335.pate0323.databinding.SentMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ArrayList<ChatMessage> messages;
    ChatMessageDAO mDAO;
    ActivityChatRoomBinding binding;
    RecyclerView.Adapter myAdapter;
    ChatRoomViewModel chatModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        mDAO = db.cmDAO();

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = chatModel.messages;

        chatModel.selectedMessage.observe(this, (newMessageValue) -> {
            MessageDetailsFragment chatFragment = new MessageDetailsFragment(newMessageValue);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLocation, chatFragment)
                    .addToBackStack("")
                    .commit();
        });

        binding.submit.setOnClickListener(click -> {
            String typed = binding.message.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateAndTime = sdf.format(new Date());
            ChatMessage typedMessage = new ChatMessage(typed, currentDateAndTime, true);
            messages.add(typedMessage);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                typedMessage.id = (int) mDAO.insertMessage(typedMessage);
            });
            //Notify the adapter
            myAdapter.notifyItemInserted(messages.size()-1); //redraw missing row

            binding.message.setText(""); //removed what was typed
        });

        binding.receive.setOnClickListener(click -> {
            String typed = binding.message.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateAndTime = sdf.format(new Date());
            ChatMessage typedMessage = new ChatMessage(typed, currentDateAndTime, false);
            messages.add(typedMessage);


            //Notify the adapter
            myAdapter.notifyItemInserted(messages.size()-1); //redraw missing row

            binding.message.setText(""); //removed what was typed
        });

        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<RowHolder>() {

            @NonNull
            @Override
            public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //viewType will be either 0 or 1, determined by getItemViewType(int position)
                if (viewType == 0) {
                    SentMessageBinding rowBinding = SentMessageBinding.inflate(getLayoutInflater(), parent, false);

                    return new RowHolder(rowBinding.getRoot());
                } else {
                    ReceiveMessageBinding rowBinding = ReceiveMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new RowHolder(rowBinding.getRoot());
                }
            }

            @Override
            public void onBindViewHolder(@NonNull RowHolder holder, int position) {
                //position will be either 0 or 1, determined by getItemViewType()
                ChatMessage message = messages.get(position);

                //override the text in the rows
                holder.message.setText(message.getMessage());
                holder.time.setText(message.getTimeSent());
            }

            //the number of items
            @Override
            public int getItemCount() {
                return messages.size();
            }

            @Override
            public int getItemViewType(int position) {
                ChatMessage message = messages.get(position);
                if(message.isSentButton()) {
                    return 0;
                } else return 1;
            }
        });
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));


        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            messages.clear();
            messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database

            runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
        });

    }
    //This represents 1 row
    class RowHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView time;
        public RowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {
                int position = getAbsoluteAdapterPosition();
                ChatMessage selected = messages.get(position);

                chatModel.selectedMessage.postValue(selected);
                /*int position = getAbsoluteAdapterPosition();


                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setTitle("Question: ")
                        .setMessage("Do you want to delete the message: " + message.getText())
                        .setNegativeButton("No", (dialog, cl)->{ })
                        .setPositiveButton("Yes", (dialog, cl)->{

                            ChatMessage removedMessage = messages.get(position);
                            messages.remove(position);
                            myAdapter.notifyItemRemoved(position);


                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() -> {
                                mDAO.deleteMessage(removedMessage);
                            });


                            Snackbar.make(message, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk ->{
                                        messages.add(position, removedMessage);
                                        myAdapter.notifyItemInserted(position);


                                        Executor thread2 = Executors.newSingleThreadExecutor();
                                        thread2.execute(() -> {
                                             mDAO.insertMessage(removedMessage);
                                        });

                                    })
                            .show();
                })
                .create().show();*/

            });
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }
}