package haidang.com.myappff;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditInfActivity extends AppCompatActivity implements View.OnClickListener {

    String JsonURL = "https://apptimnhau.000webhostapp.com/getfriend.php";
    String urlUpdateInf = "https://apptimnhau.000webhostapp.com/updateInfUser.php";
    public static String IdUser;
    String urlavatar;
    EditText edtSDT;
    EditText edtNgaySinh;
    EditText edtEmail;
    EditText edtNameHT;
    EditText edtAuth;

    TextView txtNS;
    TextView txtSDT;
    TextView txtEmail;

    ImageView coverpic;
    CircleImageView avatarphoto;
    TextView txtNameHT;

    Button btnLuuTT;
    Button btnHuy;
    Button btnSend;
    Button btnAuth;
    Button btnResend;
    //
    private static final String TAG = "EditInfActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private boolean STATE_VERIFIED = false;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inf);
        Bundle bdLF = getIntent().getExtras();
        if (bdLF != null) {
            IdUser = bdLF.getString("Userid");

        }
        Anhxa();
        // Bắt sự kiện lưu cập nhật thông tin

        btnLuuTT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UpdateInfUser(urlUpdateInf);

            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          startActivity(new Intent(EditInfActivity.this, InfUserActivity.class));
                                          finish();

                                      }
                                  }
        );
        edtSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSend.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnSend.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnSend.setEnabled(true);

            }
        });
        //getting the progressbar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarUserEdit);

        //making the progressbar visible
        progressBar.setVisibility(View.VISIBLE);
        txtNS.setVisibility(View.GONE);
     //   txtSDT.setVisibility(View.GONE);
        txtEmail.setVisibility(View.GONE);
        txtNameHT.setVisibility(View.GONE);

        edtNameHT.setVisibility(View.GONE);
        edtEmail.setVisibility(View.GONE);
        edtNgaySinh.setVisibility(View.GONE);
        edtSDT.setVisibility(View.GONE);
        edtAuth.setVisibility(View.GONE);

        btnHuy.setVisibility(View.GONE);
        btnLuuTT.setVisibility(View.GONE);
        btnAuth.setVisibility(View.GONE);
        btnResend.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);
        btnLuuTT.setEnabled(false);

        RequestQueue requestQueue = Volley.newRequestQueue(EditInfActivity.this);
        StringRequest obreq = new StringRequest(Request.Method.POST, JsonURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.INVISIBLE);
                txtNS.setVisibility(View.VISIBLE);
         //       txtSDT.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                txtNameHT.setVisibility(View.VISIBLE);

                btnHuy.setVisibility(View.VISIBLE);
                btnLuuTT.setVisibility(View.VISIBLE);
                btnAuth.setVisibility(View.VISIBLE);
                btnResend.setVisibility(View.VISIBLE);
                btnSend.setVisibility(View.VISIBLE);
                //enable resend vs auth
                btnSend.setEnabled(true);
                btnResend.setEnabled(false);
                btnAuth.setEnabled(false);

                edtNameHT.setVisibility(View.VISIBLE);
                edtEmail.setVisibility(View.VISIBLE);
                edtNgaySinh.setVisibility(View.VISIBLE);
                edtSDT.setVisibility(View.VISIBLE);
                edtAuth.setVisibility(View.VISIBLE);

                try {

                    JSONObject jsonobject = new JSONObject(response);
                    JSONArray jsonarray = jsonobject.getJSONArray("user");
                    JSONObject object = jsonarray.getJSONObject(0);
                    edtNameHT.setText(object.getString("Name"));
                    edtNgaySinh.setText(object.getString("BirthDay"));
                    edtEmail.setText(object.getString("Email"));
                    edtSDT.setText(object.getString("Phone"));
                    Picasso.with(EditInfActivity.this)
                            .load(object.getString("Photo"))
                            .placeholder(R.drawable.loading)
                            .into(coverpic);

                    urlavatar = "https://graph.facebook.com/" + IdUser + "/picture?type=large";
                    Picasso.with(EditInfActivity.this)
                            .load(urlavatar)
                            .placeholder(R.drawable.loading)
                            .into(avatarphoto);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditInfActivity.this, "Không tìm thấy", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid", IdUser);
                return params;
            }
        };
        requestQueue.add(obreq);

        //
        // Assign click listeners
        btnSend.setOnClickListener(this);
        btnAuth.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                STATE_VERIFIED =true;
                // [END_EXCLUDE]
//                signInWithPhoneAuthCredential(credential);
                btnLuuTT.setEnabled(true);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    edtSDT.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        if(STATE_VERIFIED){
            disableViews(edtSDT,edtAuth,btnSend,btnAuth,btnResend);
        }
        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            String phoneNumber=edtSDT.getText().toString().trim();
            String phone= phoneNumber.replaceFirst("0","+84");
            startPhoneNumberVerification(phone);

//            btnSend.setEnabled(false);
//            btnResend.setEnabled(true);
//            btnAuth.setEnabled(true);
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
//        edtSDT.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_black_24dp,0);
        STATE_VERIFIED =true;
        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            btnLuuTT.setEnabled(true);
                            edtAuth.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_black_24dp,0);
                            disableViews(edtSDT,edtAuth,btnSend,btnAuth,btnResend);

                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                edtAuth.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(btnSend, edtSDT);
                disableViews(btnAuth, btnResend, edtAuth);
              //  mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(btnAuth, btnResend, edtSDT, edtAuth);
                disableViews(btnSend);
          //      mDetailText.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(btnSend, btnAuth, btnResend, edtSDT,
                        edtAuth);
           //     mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(btnSend, btnAuth, btnResend, edtSDT,
                        edtAuth);
                edtSDT.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_black_24dp,0);

                //  mDetailText.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        edtAuth.setText(cred.getSmsCode());
                    } else {
                        edtAuth.setText("Đã xác thực");
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
              //  mDetailText.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGNIN_SUCCESS:

                break;
        }

//        if (user == null) {
//            // Signed out
//         //   mPhoneNumberViews.setVisibility(View.VISIBLE);
//        //    mSignedInViews.setVisibility(View.GONE);
////        edtSDT.setText("");
//        //    mStatusText.setText(R.string.signed_out);
//        } else {
//            // Signed in
////            mPhoneNumberViews.setVisibility(View.GONE);
////            mSignedInViews.setVisibility(View.VISIBLE);
//
//            disableViews(edtSDT, edtAuth,btnResend,btnSend,btnAuth);
//            edtSDT.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_black_24dp,0);
//
////            edtSDT.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_black_24dp,0);
////            mPhoneNumberField.setText(null);
////            mVerificationField.setText(null);
////
////            mStatusText.setText(R.string.signed_in);
////            mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = edtSDT.getText().toString().trim();
        String phone= phoneNumber.replaceFirst("0","+84");
        if (TextUtils.isEmpty(phone)) {
            edtAuth.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }



    private void Anhxa() {
        edtNgaySinh =(EditText) findViewById(R.id.edtNgaysinhUserEdit);
        edtEmail =(EditText) findViewById(R.id.edtEmailUserEdit);
        edtSDT =(EditText) findViewById(R.id.field_phone_number);
        edtNameHT =(EditText) findViewById(R.id.edtNameUserEdit);
        avatarphoto = (CircleImageView)findViewById(R.id.imgAvatarUserEdit);
        coverpic = (ImageView)findViewById(R.id.imgCoverUserEdit);
        edtAuth= (EditText) findViewById(R.id.field_verification_code);

        btnAuth= findViewById(R.id.button_verify_phone);
        btnSend= findViewById(R.id.button_send_verification);
        btnResend= findViewById(R.id.button_resend);
        txtNS = (TextView)findViewById(R.id.tvNSUserEdit);
      //  txtSDT = (TextView)findViewById(R.id.tvSDTUserEdit);
        txtEmail = (TextView)findViewById(R.id.tvEmailUserEdit);
        txtNameHT = (TextView)findViewById(R.id.tvNameUserEdit);
        btnLuuTT = (Button) findViewById(R.id.btnLuuEdit);
        btnHuy = (Button) findViewById(R.id.btnHuyEdit);

    }

    //Hàm cập nhật thông tin
    private void UpdateInfUser(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("success")) {
                            Toast.makeText(EditInfActivity.this, "Cập nhật thành công...", Toast.LENGTH_SHORT).show();
                            Intent mhuser = new Intent(EditInfActivity.this, InfUserActivity.class);
                            startActivity(mhuser);
                            finish();
                        } else {
                            //Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, "Error!!!", Toast.LENGTH_LONG).show();
                        Log.d("AAA", "Error:\n" + error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Userid", IdUser);
                params.put("Name", edtNameHT.getText().toString().trim());
                params.put("Birthday", edtNgaySinh.getText().toString().trim());
                params.put("Phone", edtSDT.getText().toString().trim());
                params.put("Email", edtEmail.getText().toString().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_send_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                String phoneNumber=edtSDT.getText().toString().trim();
                String phone= phoneNumber.replaceFirst("0","+84");

                startPhoneNumberVerification(phone);
                break;
            case R.id.button_verify_phone:
                String code = edtAuth.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    edtAuth.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(edtAuth.getText().toString(), mResendToken);
                break;

        }
    }
    }

