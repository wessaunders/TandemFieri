package com.gmail.dleemcewen.tandemfieri;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gmail.dleemcewen.tandemfieri.Constants.AddressConstants;
import com.gmail.dleemcewen.tandemfieri.Utility.FetchAddressIntentService;
import com.gmail.dleemcewen.tandemfieri.Utility.MapUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import static android.widget.Toast.makeText;
import static com.gmail.dleemcewen.tandemfieri.R.id.phone;

public class CreateAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    public Button nextButton, myLocation;
    public EditText firstName, lastName, address, city, zip, phoneNumber, email;

    protected Location location;

    private AddressResultReceiver mResultReceiver;
    private LatLng latLng;
    private GoogleApiClient googleApiClient;
    private String state = "";
    private Spinner states;
    private static CreateAccountActivity activityInstance;
    private boolean statusFromREST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        activityInstance = this;

        nextButton = (Button) findViewById(R.id.nextButton);
        myLocation = (Button) findViewById(R.id.location_button);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        states = (Spinner) findViewById(R.id.state);
        zip = (EditText) findViewById(R.id.zip);
        phoneNumber = (EditText) findViewById(phone);
        email = (EditText) findViewById(R.id.email);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                    if (googleApiClient.isConnected() && location != null) {
                        if (mResultReceiver == null) mResultReceiver = new AddressResultReceiver(new Handler());
                        Intent addressIntent = new Intent(getApplicationContext(), FetchAddressIntentService.class);
                        addressIntent.putExtra(AddressConstants.RECEIVER, mResultReceiver);
                        addressIntent.putExtra(AddressConstants.LOCATION_DATA_EXTRA, location);
                        startService(addressIntent);
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityStr = city.getText().toString();
                String zipStr = zip.getText().toString();
                String street = address.getText().toString();

                if (MapUtil.verifyAddress(getApplicationContext(), street, cityStr, state, zipStr)) {
                    Intent intent = new Intent(CreateAccountActivity.this, AlmostDoneActivity.class);
                    intent.putExtra("firstName", firstName.getText().toString());
                    intent.putExtra("lastName", lastName.getText().toString());
                    intent.putExtra("address", address.getText().toString());
                    intent.putExtra("city", city.getText().toString());
                    intent.putExtra("state", state);
                    intent.putExtra("zip", zip.getText().toString());
                    intent.putExtra("phoneNumber", phoneNumber.getText().toString());
                    intent.putExtra("email", email.getText().toString());
                    if (email.getText().toString().isEmpty()) {
                        makeText(getApplicationContext(), "Do not leave email blank", Toast.LENGTH_LONG).show();
                    } else if (!email.getText().toString().contains("@")) {
                        makeText(getApplicationContext(), "Email must contain an @", Toast.LENGTH_LONG).show();
                    }
                    startActivity(intent);
                } else {
                    address.setError(FormConstants.ERROR_TAG_ADDRESS);
                    city.setError(FormConstants.ERROR_TAG_CITY);
                    zip.setError(FormConstants.ERROR_TAG_ZIP);
                }
            }
        });

        states.setOnItemSelectedListener(activityInstance);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        states.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public static CreateAccountActivity getInstance() {
        return activityInstance;
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
        state = (String) parent.getItemAtPosition(position);
    }

    public class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Address addressOutput = resultData.getParcelable(AddressConstants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                Toast.makeText(getApplicationContext(), "Couldn't Receive Location.", Toast.LENGTH_SHORT).show();
            }

            String [] statesArray = getResources().getStringArray(R.array.states);
            address.setText(addressOutput.getAddressLine(0));
            for (int i = 0; i < statesArray.length; i++) {
                if (addressOutput.getAdminArea().trim().equals(statesArray[i]))
                    states.setSelection(i);
            }
            city.setText(addressOutput.getLocality());
            zip.setText(addressOutput.getPostalCode());
        }
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
        //not implemented
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this
                , android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "no permission", Toast.LENGTH_SHORT).show();
                }

                return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
