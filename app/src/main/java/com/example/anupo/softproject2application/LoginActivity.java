package com.example.anupo.softproject2application;
/*
 * Purpose: This page for Login
 * Author:  Anupom Roy
 * Date: Feburary 20, 2019
 * Version: 1.7
 * */
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private static final String CUSTOMER_USERNAME_PREFS = "customer_username_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText =(EditText) findViewById(R.id.usernameTxt);
        passwordEditText = (EditText)findViewById(R.id.passwordTxt);
    }
    //validate credentials
    public boolean validate(String username, String password) {
        if(!username.isEmpty())
        {
            return username.equals(password);
        }
        return false;
    }


    public void loginBtn_OnClick(View view) {
        String _username = usernameEditText.getText().toString();
        String _password = passwordEditText.getText().toString();
        
        
        //Modified By Shila on 4 Apr 2019
         //for login calling api (sample username:anu@anu.com, pwd: Cen@123) is a valid user for testing
        String res=new HTTPAsyncTask().execute("http://bookapi-dev.us-east-1.elasticbeanstalk.com/api/UserWithRoles/login");
        //now check for valid user this is the reponse, for validating the user, check the result for success or message for 'profile found'
        /*{
  "statusCode": 200,
  "result": "success",
  "message": "profile found",
  "profile": {
    "uId": "63456bed-b6f7-43f8-9cc4-a5c4ba3b7e77",
    "uEmail": "anu@anu.com",
    "uPhoneNumber": "12345",
    "role": ""
  }
}*/
        
        

        if(validate(_username,_password))
        {
            SharedPreferences.Editor editor =
                    getSharedPreferences(CUSTOMER_USERNAME_PREFS, MODE_PRIVATE).edit();
            editor.putString("username_key",_username);
            editor.apply();

            //view customer activity
            Toast.makeText(this, "valid", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,BooksActivity.class);
            intent.putExtra("username",_username);
            startActivity(intent);

            // for SharedPreferences response
            SharedPreferences myPreference =
                    getSharedPreferences("MyUser", 0);
            //prepare it for edit by creating and Edit object
            SharedPreferences.Editor prefEditor = myPreference.edit();
            //store a string in memory
            prefEditor.putString("UserName", _username);
            //commit the transaction
            prefEditor.commit();
        }
        else
        {
            //throw error
            Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show();
            passwordEditText.setFocusable(true);
            passwordEditText.setError("Password and/or username is wrong...");
        }

    }
    public void cancelBtn_OnClick(View view) {
        usernameEditText.setText("");
        passwordEditText.setText("");

    }
    // If not rregister, go to registration activity to register
    public void NotRegistered_OnClick(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home:
                Intent homeIntent=new Intent(this,MainActivity.class);
                startActivity(homeIntent);
                //Toast.makeText(this, "You selected start!", Toast.LENGTH_LONG).show();
                break;
            case R.id.login:

                Toast.makeText(this, "You selected login!", Toast.LENGTH_LONG).show();
                break;
            case R.id.book:
                Intent books=new Intent(this,BooksActivity.class);
                startActivity(books);
                //Toast.makeText(this, "You selected book!", Toast.LENGTH_LONG).show();
                break;
            case R.id.bookList:
                Intent booksListIntent=new Intent(this,BooksListActivity.class);
                startActivity(booksListIntent);

                break;
            case R.id.exit:
                Toast.makeText(this, "You selected exit!", Toast.LENGTH_LONG).show();
                System.exit(1);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //added by shila on 4 apr 2019
    //new class for handling post request from api
    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected String onPostExecute(String result) {
           // res.setText(result);
            return result;
        }
        private String HttpPost(String myUrl) throws IOException, JSONException {
            String result = "";
            String JsonResult = null;
            StringBuffer sb = new StringBuffer();
            InputStream is = null;
            URL url = new URL(myUrl);

            // 1. create HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // 2. build JSON object
            JSONObject jsonObject = buidJsonObject();

            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject);

            // 4. make POST request to the given URL
            //conn.connect();
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            JsonResult = sb.toString();

            // 5. return response message
            return JsonResult;//conn.getResponseMessage()+"";

        }
        private JSONObject buidJsonObject() throws JSONException {

            JSONObject jsonObject = new JSONObject();
         
            //for login--begin
            jsonObject.accumulate("email", usernameEditText.getText()); //test with this value "anu@anu.com"
            jsonObject.accumulate("password", passwordEditText.getText()); //test with this value "Cen@123"
            //for login--end
          
    

            return jsonObject;
        }
        private void setPostRequestContent(HttpURLConnection conn,
                                           JSONObject jsonObject) throws IOException {

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            Log.i(MainActivity.class.toString(), jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
        }
    }
}
