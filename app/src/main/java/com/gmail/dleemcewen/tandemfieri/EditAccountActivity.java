package com.gmail.dleemcewen.tandemfieri;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gmail.dleemcewen.tandemfieri.Entities.User;
import com.gmail.dleemcewen.tandemfieri.Formatters.StringFormatter;
import com.gmail.dleemcewen.tandemfieri.Logging.LogWriter;
import com.gmail.dleemcewen.tandemfieri.Repositories.Users;
import com.gmail.dleemcewen.tandemfieri.Utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.logging.Level;

import static com.gmail.dleemcewen.tandemfieri.Validator.Validator.isValid;

public class EditAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private User currentUser;
    private User changedUser;
    private String type = "";
    private String uid = "";

    private boolean emailIsDuplicated, userIsValid;


    private DatabaseReference mDatabase;
    private FirebaseUser fireuser;

    private EditText firstName, lastName, address, city, zip, phoneNumber, email;
    private Button saveButton, cancelButton;

    private String state = "";
    private Spinner states;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        currentUser = new User();
        Bundle bundle = this.getIntent().getExtras();
        currentUser = (User) bundle.getSerializable("User");
        type = this.getIntent().getStringExtra("UserType");


        /*For the code I currently have, the key stored in the User object is not the same as the id in the database.
        Therefore I must use "uid = FirebaseAuth.getInstance().getCurrentUser().getUid()" to access the correct
        user in the database.*/
        fireuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fireuser != null) {
            // User is signed in
            uid = fireuser.getUid();
        } else {
            // No user is signed in
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();
        }


        //reference in database to the current user
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(type).child(uid);


        if(currentUser != null) {
            LogWriter.log(getApplicationContext(), Level.INFO, "The user is " + currentUser.getEmail());
        }
        else{
            LogWriter.log(getApplicationContext(), Level.WARNING, "The user is null");
            finish();
        }
        LogWriter.log(getApplicationContext(), Level.INFO, "The user key is " + currentUser.getKey());
        LogWriter.log(getApplicationContext(), Level.INFO, "The user id is " + uid);

        //get handles to view
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        states = (Spinner) findViewById(R.id.state);
        zip = (EditText) findViewById(R.id.zip);
        phoneNumber = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        saveButton = (Button) findViewById(R.id.save_Button);
        cancelButton = (Button) findViewById(R.id.cancel_Button);


        states.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        states.setAdapter(adapter);

        // Find the user's state in the array of states
        String[] arrayOfStates = getResources().getStringArray(R.array.states);
        int positionOfUserState = Arrays.asList(arrayOfStates).indexOf(StringFormatter.toProperCase(currentUser.getState()));


        //set text in fields using user's current information
        firstName.setText(currentUser.getFirstName());
        lastName.setText(currentUser.getLastName());
        address.setText(currentUser.getAddress());
        city.setText(currentUser.getCity());
        states.setSelection(positionOfUserState);
        zip.setText(currentUser.getZip());
        phoneNumber.setText(currentUser.getPhoneNumber());
        email.setText(currentUser.getEmail());

        //program button listeners
        //cancels the page and returns to previous page
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //saves information to the database once validated
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //collect information
                getChangedUserInformation();
                userIsValid = validInput();
                //emailIsDuplicated = false;
                //Is email changed?
                if(!currentUser.getEmail().equals(changedUser.getEmail())){
                    //Toast.makeText(getApplicationContext(),"new Email has been entered" , Toast.LENGTH_LONG).show();
                    //yes - is email valid?

                        //yes - check for duplicate email
                        //Toast.makeText(getApplicationContext(),"new email is valid." , Toast.LENGTH_LONG).show();
                        DatabaseReference rodb = FirebaseDatabase.getInstance().getReference().child("User");

                        VEListener listener = new VEListener();
                        rodb.addListenerForSingleValueEvent(listener);
                    }


            }
        });
    }//end onCreate

    public void getChangedUserInformation(){
        changedUser = new User();
        changedUser.setFirstName(firstName.getText().toString());
        changedUser.setLastName(lastName.getText().toString());
        changedUser.setAddress(address.getText().toString());
        changedUser.setCity(city.getText().toString());
        changedUser.setState(state);
        changedUser.setZip(zip.getText().toString());
        changedUser.setPhoneNumber(phoneNumber.getText().toString());
        changedUser.setEmail(email.getText().toString());
        changedUser.setAuthUserID(currentUser.getAuthUserID());
    }

    public boolean validInput(){
        boolean result = false; //return value

        boolean firstNameValid = isValid(firstName, FormConstants.REG_EX_FIRSTNAME, FormConstants.ERROR_TAG_FIRSTNAME);
        boolean lastNameValid = isValid(lastName, FormConstants.REG_EX_LASTNAME, FormConstants.ERROR_TAG_LASTNAME);
        boolean addressValid = isValid(address, FormConstants.REG_EX_ADDRESS, FormConstants.ERROR_TAG_ADDRESS);
        boolean cityValid = isValid(city, FormConstants.REG_EX_CITY, FormConstants.ERROR_TAG_CITY);

        //boolean stateValid = isValid(state, FormConstants.REG_EX_STATE, FormConstants.ERROR_TAG_STATE);

        boolean emailValid = isValid(email, FormConstants.REG_EX_EMAIL, FormConstants.ERROR_TAG_EMAIL);
        boolean phoneNumberValid = isValid(phoneNumber, FormConstants.REG_EX_PHONE, FormConstants.ERROR_TAG_PHONE);
        boolean zipValid = isValid(zip, FormConstants.REG_EX_ZIP, FormConstants.ERROR_TAG_ZIP);

        String cityStr = city.getText().toString();
        String zipStr = zip.getText().toString();
        String street = address.getText().toString();
        if (!MapUtil.verifyAddress(getApplicationContext(), street, cityStr, state, zipStr)) {
            address.setError(FormConstants.ERROR_TAG_ADDRESS);
            city.setError(FormConstants.ERROR_TAG_CITY);
            zip.setError(FormConstants.ERROR_TAG_ZIP);
            return result;
        }

        if (    firstNameValid      &&
                lastNameValid       &&
                addressValid        &&
                cityValid           &&

                //stateValid          &&

                zipValid            &&
                phoneNumberValid    &&
                emailValid) {

            result = true;
        }
        return result;
    }


    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        state = (String)parent.getItemAtPosition(position);
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class VEListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            emailIsDuplicated = false;
            String msg = "Email has been changed.";
            DataSnapshot ro = (DataSnapshot) dataSnapshot.child("Restaurant");
            for (DataSnapshot ps : ro.getChildren()) {
                User u = ps.getValue(User.class);
                //Toast.makeText(getApplicationContext(), u.getEmail(), Toast.LENGTH_SHORT).show();
                if (u.getEmail().equals(changedUser.getEmail())) {
                    Toast.makeText(getApplicationContext(), "In u.getEmail == changeduser email", Toast.LENGTH_SHORT).show();
                    emailIsDuplicated = true;
                    msg = "Invalid email.";
                }
            }
            DataSnapshot diner = (DataSnapshot) dataSnapshot.child("Diner");
            for (DataSnapshot ps : diner.getChildren()) {
                User u = ps.getValue(User.class);
                //Toast.makeText(getApplicationContext(), u.getEmail(), Toast.LENGTH_SHORT).show();
                if (u.getEmail().equals(changedUser.getEmail())) {
                    Toast.makeText(getApplicationContext(), "In u.getEmail == changeduser email", Toast.LENGTH_SHORT).show();
                    emailIsDuplicated = true;
                    msg = "Invalid email.";
                }
            }
            DataSnapshot driver = (DataSnapshot) dataSnapshot.child("Driver");
            for (DataSnapshot ps : driver.getChildren()) {
                User u = ps.getValue(User.class);
                Toast.makeText(getApplicationContext(), u.getEmail(), Toast.LENGTH_SHORT).show();
                if (u.getEmail().equals(changedUser.getEmail())) {
                    //Toast.makeText(getApplicationContext(), "In u.getEmail == changeduser email", Toast.LENGTH_SHORT).show();
                    emailIsDuplicated = true;
                    msg = "Invalid email.";
                }
            }
            //is all info valid?
            if(userIsValid && !emailIsDuplicated){
                //Toast.makeText(getApplicationContext(),"About to save" , Toast.LENGTH_LONG).show();
                saveUserToDatabase();
                Toast.makeText(getApplicationContext(),msg , Toast.LENGTH_LONG).show();
                //send new user info to the proper menu activity
                Bundle bundle1 = new Bundle();
                Intent intent = null;

                if (type.equals("Restaurant"))
                    intent = new Intent(EditAccountActivity.this, RestaurantMainMenu.class);
                else if (type.equals("Diner"))
                    intent = new Intent(EditAccountActivity.this, DinerMainMenu.class);
                else if (type.equals("Driver"))
                    intent = new Intent(EditAccountActivity.this, DriverMainMenu.class);

                bundle1.putSerializable("User", changedUser);
                intent.putExtras(bundle1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else{
                //something is not valid
                if(emailIsDuplicated)
                    Toast.makeText(getApplicationContext(),msg , Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onCancelled(DatabaseError firebaseError) {
            //System.out.println("The read failed: " + firebaseError.getMessage());
        }

    }

    //edits the current user's info in the database with the new User information
    public void saveUserToDatabase(){
        mDatabase.child("firstName").setValue(changedUser.getFirstName());
        mDatabase.child("lastName").setValue(changedUser.getLastName());
        mDatabase.child("address").setValue(changedUser.getAddress());
        mDatabase.child("city").setValue(changedUser.getCity());
        mDatabase.child("state").setValue(changedUser.getState());
        mDatabase.child("zip").setValue(changedUser.getZip());
        mDatabase.child("phoneNumber").setValue(changedUser.getPhoneNumber());
        mDatabase.child("email").setValue(changedUser.getEmail());

        //set the new email in the authentication table
        fireuser.updateEmail(changedUser.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            LogWriter.log(getApplicationContext(), Level.INFO, "User email address updated.");
                        }
                    }
                });
    }

}//end activity
