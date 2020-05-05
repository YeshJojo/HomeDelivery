package com.jojo.homedelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jojo.customfonts.EditText_Poppins_Regular;
import com.jojo.customfonts.MyTextView_Poppins_Bold;
import com.jojo.customfonts.MyTextView_Poppins_Regular;
import com.jojo.payments.JSONParser;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Product_detail extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    private static String MercahntKey = "ATMIMC97739829232153";
    String name, price;
    MyTextView_Poppins_Bold textView;
    MyTextView_Poppins_Regular payment;
    String PAYTM_PACKAGE_NAME = "net.one97.paytm";
    String PHONEPE_PACKAGE_NAME = "com.phonepe.app";
    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    final int UPI_PAYMENT = 0;
    String custID = "";
    String orderID = "";
    int flag = 0;
    private RadioGroup paymentMethod;
    private RadioButton radioSelect;
    EditText_Poppins_Regular upiEdittext;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if(intent.getStringExtra("name") != null && intent.getStringExtra("price") != null){
            name = intent.getStringExtra("name");
            price = intent.getStringExtra("price");
        }
        textView = findViewById(R.id.priceDisp);
        payment = findViewById(R.id.confirmPayment);
        paymentMethod = findViewById(R.id.radioGroup);
        upiEdittext = findViewById(R.id.upiID);

        textView.setText("₹ "+price + ".");

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upiId = upiEdittext.getText().toString();
                if(flag == 0){
                    sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
                    dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    custID = createRandomCustId();
                    orderID = createRandomOrderId();
                } else if(flag == 1){
                    payUsingUpi(price, upiId, name, GOOGLE_PAY_PACKAGE_NAME);
                 } else if(flag == 2){
                    payUsingUpi(price, upiId, name, PHONEPE_PACKAGE_NAME);
                } else if(flag == 3){
                    payUsingUpi(price, upiId, name, PAYTM_PACKAGE_NAME);
                }
            }
        });
    }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.paytmBtn:
                if (checked){
                    upiEdittext.setVisibility(View.GONE);
                    flag = 0;
                } else{
                    upiEdittext.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.gpayBtn:
                if (checked){
                    upiEdittext.setVisibility(View.VISIBLE);
                    flag = 1;
                } else{
                    upiEdittext.setVisibility(View.GONE);
                }
                break;
            case R.id.phonepeBtn:
                if (checked){
                    upiEdittext.setVisibility(View.VISIBLE);
                    flag = 2;
                } else{
                    upiEdittext.setVisibility(View.GONE);
                }
                break;
            case R.id.upiBtn:
                if (checked){
                    upiEdittext.setVisibility(View.VISIBLE);
                    flag = 3;
                } else{
                    upiEdittext.setVisibility(View.GONE);
                }
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(Product_detail.this);
        String url ="http://enactive-words.000webhostapp.com/paytm/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {

            JSONParser jsonParser = new JSONParser(Product_detail.this);
            String param= "MID="+ MercahntKey + "&ORDER_ID=" + orderID + "&CUST_ID="+ custID +
                    "&CHANNEL_ID=WAP&TXN_AMOUNT=" + price + "&WEBSITE=WEBSTAGING"+
                    "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();

            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("MID", MercahntKey);
            paramMap.put("ORDER_ID", orderID);
            paramMap.put("CUST_ID", custID);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", price);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            Service.startPaymentTransaction(Product_detail.this, true, true, Product_detail.this  );
        }
    }
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
    }
    @Override
    public void networkNotAvailable() {
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected String createRandomCustId() {
        String val = "DI";
        int ranChar = 65 + (new Random()).nextInt(90-65);
        char ch = (char)ranChar;
        val += ch;

        Random r = new Random();
        int numbers = 100000 + (int)(r.nextFloat() * 899900);
        val += String.valueOf(numbers);

        val += "-";
        for(int i = 0; i<6;){
            int ranAny = 48 + (new Random()).nextInt(90-65);
            if(!(57 < ranAny && ranAny<= 65)){
                char c = (char)ranAny;
                val += c;
                i++;
            }
        }
        return val;
    }
    protected String createRandomOrderId() {
        long timeSeed = System.nanoTime();
        double randSeed = Math.random() * 1000;
        long midSeed = (long) (timeSeed * randSeed);
        String s = midSeed + "";
        String subStr = s.substring(0, 9);

        return subStr;
    }

    void payUsingUpi(String amount, String upiId, String name, String paymentMethod) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", "note")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        upiPayIntent.setPackage(paymentMethod);
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(Product_detail.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String text = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + text);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(text);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(Product_detail.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(Product_detail.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d("UPI", "responseStr: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(Product_detail.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Product_detail.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Product_detail.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
