package com.jojo.contact;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.jojo.homedelivery.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;
    private SpeechToText speechService;
    private TextToSpeech textToSpeech;
    private Context mContext;
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageView btnSend;
    private Button finish;
    private LinearLayout msgTextLayout;
    private boolean initialRequest;
    Handler handler;
    ArrayList<String> responses = new ArrayList<String>();
    ArrayList<String> questions = new ArrayList<String>();
    private static String TAG = "MainActivity";
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    private void createServices() {
        watsonAssistant = new Assistant("2020-06-10", new IamAuthenticator(mContext.getString(R.string.assistant_apikey)));
        watsonAssistant.setServiceUrl(mContext.getString(R.string.assistant_url));

        textToSpeech = new TextToSpeech(new IamAuthenticator((mContext.getString(R.string.TTS_apikey))));
        textToSpeech.setServiceUrl(mContext.getString(R.string.TTS_url));

        speechService = new SpeechToText(new IamAuthenticator(mContext.getString(R.string.STT_apikey)));
        speechService.setServiceUrl(mContext.getString(R.string.STT_url));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        new CommentKeyBoardFix(this);
        mContext = getApplicationContext();

        inputMessage = findViewById(R.id.message);
        btnSend = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.recycler_view);
        finish = findViewById(R.id.finishBtn);
        msgTextLayout = findViewById(R.id.msgTextLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("contact");

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });
        createServices();
        sendMessage();

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeData();
                finish();
            }
        });
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if(finish.getVisibility() == View.VISIBLE)
                    if(msgTextLayout.getVisibility() == View.VISIBLE){
                        msgTextLayout.setVisibility(View.GONE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 10);
    }
    private void storeData(){
        String currentUserID = firebaseAuth.getCurrentUser().getUid();
        HashMap<String, Object> profileMap = new HashMap<>();
        for(int i=0; i<questions.size(); i++){
            profileMap.put(questions.get(i), responses.get(i));
        }
        databaseReference.child(currentUserID).updateChildren(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(ContactActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendMessage() {
        final String inputmessage = this.inputMessage.getText().toString().trim();
        if(!inputmessage.equals(""))
            responses.add(inputmessage);
        if (!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
        }
        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (watsonAssistantSession == null) {
                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
                        watsonAssistantSession = call.execute();
                    }
                    MessageInput input = new MessageInput.Builder().text(inputmessage).build();
                    MessageOptions options = new MessageOptions.Builder()
                            .assistantId(mContext.getString(R.string.assistant_id))
                            .input(input)
                            .sessionId(watsonAssistantSession.getResult().getSessionId())
                            .build();
                    Response<MessageResponse> response = watsonAssistant.message(options).execute();

                    if (response != null && response.getResult().getOutput() != null && !response.getResult().getOutput().getGeneric().isEmpty()) {
                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();
                        for (RuntimeResponseGeneric r : responses) {
                            Message outMessage;
                            switch (r.responseType()) {
                                case "text":
                                    outMessage = new Message();
                                    outMessage.setMessage(r.text());
                                    outMessage.setId("2");
                                    Log.i(TAG, "outMessage: " + r.text());
                                    messageArrayList.add(outMessage);
                                    String reply = r.text();
                                    if(reply.contains("visit our store")){
                                        finish.setVisibility(View.VISIBLE);
                                        msgTextLayout.setVisibility(View.GONE);
                                    } else {
                                        questions.add(reply);
                                        finish.setVisibility(View.GONE);
                                        msgTextLayout.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case "option":
                                    outMessage = new Message();
                                    String title = r.title();
                                    questions.add(title);
                                    String OptionsOutput = "";
                                    for (int i = 0; i < r.options().size(); i++) {
                                        DialogNodeOutputOptionsElement option = r.options().get(i);
                                        OptionsOutput = OptionsOutput + option.getLabel() + "\n";
                                    }
                                    outMessage.setMessage(title + "\n" + OptionsOutput);
                                    outMessage.setId("2");
                                    messageArrayList.add(outMessage);
                                    break;
                                case "image":
                                    outMessage = new Message(r);
                                    messageArrayList.add(outMessage);
                                    break;
                                default:
                                    Log.e("Error", "Unhandled message type");
                            }
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
            builder.setTitle("Oops!!");
            builder.setMessage("No mobile data connection detected.\nPlease check network connection.");
            builder.setCancelable(false);
            builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS),0);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
    }
}