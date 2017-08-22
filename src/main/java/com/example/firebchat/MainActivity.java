package com.example.firebchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    final int SIGN_IN_REQUEST_CODE = 1;
FirebaseListAdapter<Message> firebaseListAdapter;
    ConstraintLayout constraintLayout;
    Button button;
    EditText input;
    DisposableObserver observer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         input = (EditText)findViewById(R.id.editText);
        constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);
        button = (Button)findViewById(R.id.button);



        Observable o = Observable.create(e -> {

            // e.setCancellation(() -> view.setOnClickListener(null));
            //view.setOnClickListener(v -> e.onNext(v));
            button.setOnClickListener(v -> e.onNext(v));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observer = new DisposableObserver(){

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Object o) {
                Log.d("rx","observer onNext called");
                FirebaseDatabase.getInstance().getReference().push().setValue(
                        new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),input.getText().toString())

                );
                input.setText("");
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.d("rx","observer onError called");
            }

            @Override
            public void onComplete() {
                Log.d("rx","observer onComplete called");

            }
        };
        o.subscribe(observer);



        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }else{
            displayChat();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logout){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            observer.dispose();
                            Snackbar.make(constraintLayout,"выход выполнен",Snackbar.LENGTH_SHORT).show();
                            finish();
                        }
                    }
            );
        }
        return true;
    }

    private void displayChat() {
        ListView listView = (ListView)findViewById(R.id.list);
        firebaseListAdapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item,FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView tvMail, tvText,tvDate;
                tvDate = (TextView)v.findViewById(R.id.tvDate);
                tvMail = (TextView)v.findViewById(R.id.tvMail);
                tvText = (TextView)v.findViewById(R.id.tvText);

                tvDate.setText(android.text.format.DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTime()));
                tvMail.setText(model.getUser());
                tvText.setText(model.getMessage());
            }
        };
        listView.setAdapter(firebaseListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_IN_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Snackbar.make(constraintLayout,"вход выполнен",Snackbar.LENGTH_SHORT).show();
                displayChat();
            }
            else{
                Snackbar.make(constraintLayout,"вход не выполнен",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}
