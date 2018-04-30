package com.example.currentplacedetailsonmap.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Model.Address;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

/**
 * Bron:
 * https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
 * https://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/
 * https://www.androidhive.info/2016/10/android-working-with-firebase-realtime-database/
 */
public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    private EditText inputLastname, inputEmail, inputPassword, inputFirstname;

    private LatLng inputAdress;
    private Spinner inputGeslacht;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private String userId;
    private String geslacht;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        PlaceAutocompleteFragment paf = (PlaceAutocompleteFragment) this.getFragmentManager().findFragmentById(R.id.adress);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);

        //place data in variables
        inputLastname = (EditText) findViewById(R.id.lastname);

        inputFirstname = (EditText) findViewById(R.id.firstname);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        /*https://developer.android.com/guide/topics/ui/controls/spinner.html*/
        inputGeslacht = (Spinner) findViewById(R.id.geslacht);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGeslacht.setAdapter(adapter);
        //inputGeslacht.setOnItemClickListener((AdapterView.OnItemClickListener) this);

        paf.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {
                inputAdress = place.getLatLng();
            }

            @Override
            public void onError(Status status)
            {
                Log.i("FRAGMENT GOOGLE ERROR", "An error occurred: " + status);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final String lastName = inputLastname.getText().toString().trim();
                final String firstName = inputFirstname.getText().toString().trim();
                final String myGender = String.valueOf(inputGeslacht.getSelectedItem());
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final Address address = new Address(inputAdress.latitude, inputAdress.longitude);

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 9)
                {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 9 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful())
                                {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else
                                {
                                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                    userId = current_user.getUid();
                                    mDatabaseReference = database.getReference("User").child(userId);

                                    User user = new User(userId, lastName, firstName, myGender, email, password, address, "online");
                                    mDatabaseReference.setValue(user);
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (adapterView.getItemAtPosition(i) == adapterView.getItemAtPosition(1))
            geslacht = "Male";
        else
            geslacht = "female";
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
        Context context = getApplicationContext();
        CharSequence text = "Select gender";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


}