package drucc.sittichok.heyheybread;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    //Explicit
    private ManageTABLE objManageTABLE;
    private EditText userEditText , passwordEditText;
    private String userString  , passwordString;
    public String TAG = "hey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Widget
        bindWidget();


        //Connected Database ทำให้สามารถเรียกใช้ เมดตอด ที่ อยู่ ใน ManageTABLE ได้
        objManageTABLE = new ManageTABLE(this);



        //test Add Value
        //testAddValue();

        //Delete All SQLite
        deleteAllSQLite();

        //Synchronize JSON to SQLite
        synJSONtoSQLite();


    }   // OnCreate

    private void bindWidget() {
        userEditText = (EditText) findViewById(R.id.editText);
        passwordEditText = (EditText) findViewById(R.id.editText2);
    }

    public void clickLogin(View view) {

        // Check Space เช็คว่า ถ้า ช่องที่กรอกข้อมูล อันใด อันหนึ่งว่าง ให้ โชว์ ข้อความ  "มีช่องว่าง","กรุณากรอกให้ครบ" ที่หน้า MainActivity.this
        userString = userEditText.getText().toString().trim(); // รับค่าเป็น text แปลงเป็น String ,trim ตัดช่องว่าง
        passwordString = passwordEditText.getText().toString().trim();

        if (userString.equals("") || passwordString.equals("")) {  //อีคั่ว
            //Have Space
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(MainActivity.this,"Please check space","กรุณากรอกข้อมูลให้ครบ");

        } else {
            //No Space
            checkUser();

        }


    }   // clickLogin

    private void checkUser() {

        try {

            String[] resultStrings = objManageTABLE.searchUser(userString);  //userString คือ ค่าที่รับมาจากลูกค้ากรอก
            if (passwordString.equals(resultStrings[2])) {
                // equals คือ = เปรียบเทียบ PasswordString ที่ลูกค้ากรอกมา ถ้า ตรงกับ Pass ที่อยู่ในฐานข้อมูล

                Intent objIntent = new Intent(MainActivity.this, HubActivity.class);
                objIntent.putExtra("ID", resultStrings[0]);
                startActivity(objIntent);

            } else {
                MyAlertDialog objMyAlertDialog = new MyAlertDialog();
                objMyAlertDialog.errorDialog(MainActivity.this,"Password false","กรุณากรอกรหัสใหม่");


            }

        } catch (Exception e) {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(MainActivity.this,"User false","ไม่มี "+ userString + " ในฐานข้อมูล");
        }


    }   // checkUser


    private void synJSONtoSQLite() {

        StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(myPolicy);   //เปิดโปรโตรคอลให้แอพเชื่อมต่ออินเตอร์เน็ตได้ ใช้ได้ทั้งหมด โดยใช้คำสั่ง permitAll

        int intTimes = 1;
        while (intTimes <= 3) {

            InputStream objInputStream = null;
            String strJSON = null;
            String strURLuser = "http://swiftcodingthai.com/mos/php_get_user_mos.php";
            String strURLbread = "http://swiftcodingthai.com/mos/php_get_bread_mos.php";
            String strURLorder = "http://swiftcodingthai.com/mos/php_get_order_mos.php";

            HttpPost objHttpPost = null;

            //1. Create InputStream
            try {

                HttpClient objHttpClient = new DefaultHttpClient();


                switch (intTimes) {
                    case 1:
                        objHttpPost = new HttpPost(strURLuser);

                        break;
                    case 2:
                        objHttpPost = new HttpPost(strURLbread);

                        break;
                    case 3:
                        objHttpPost = new HttpPost(strURLorder);
                        break;

                }   // switch

                HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
                HttpEntity objHttpEntity = objHttpResponse.getEntity();
                objInputStream = objHttpEntity.getContent();


            } catch (Exception e) {
                Log.d(TAG, "InputStream ==> " + e.toString());
            }



            //2. Create JSON String
            try {

                BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream,"UTF-8"));
                StringBuilder objStringBuilder = new StringBuilder();
                String strLine = null;

                while ((strLine = objBufferedReader.readLine()) != null) {
                    objStringBuilder.append(strLine);


                }   //while
                objInputStream.close();
                strJSON = objStringBuilder.toString();


            } catch (Exception e) {
                Log.d(TAG, "strJSON ==> " + e.toString());

            }

            //3. Update JSON String to SQLite
            try {

                JSONArray objJsonArray = new JSONArray(strJSON);

                for (int i=0; i<objJsonArray.length();i++) {

                    JSONObject object = objJsonArray.getJSONObject(i);

                    switch (intTimes) {

                        case 1: // userTABLE

                            String strUser = object.getString(ManageTABLE.COLUMN_User);
                            String strPassword = object.getString(ManageTABLE.COLUMN_Password);
                            String strName = object.getString(ManageTABLE.COLUMN_Name);
                            String strSurname = object.getString(ManageTABLE.COLUMN_Surname);
                            String strAddress = object.getString(ManageTABLE.COLUMN_Address);
                            String strPhone = object.getString(ManageTABLE.COLUMN_Phone);
                            String strComplacency = object.getString(ManageTABLE.COLUMN_Complacency);

                            objManageTABLE.addNewUser(strUser, strPassword, strName, strSurname,
                                    strAddress, strPhone, strComplacency);

                            break;
                        case 2: // breadTABLE
                            String strBread = object.getString(ManageTABLE.COLUMN_Bread);
                            String strPrice = object.getString(ManageTABLE.COLUMN_Price);
                            String strAmount = object.getString(ManageTABLE.COLUMN_Amount);
                            String strImage = object.getString(ManageTABLE.COLUMN_Image);

                            objManageTABLE.addNewBread(strBread, strPrice, strAmount, strImage);

                            break;
                        case 3: // orderTABLE

                            String stridReceive = object.getString(ManageTABLE.COLUMN_idReceive);
                            String strName1 = object.getString(ManageTABLE.COLUMN_Name);
                            String strDate = object.getString(ManageTABLE.COLUMN_Date);
                            String strSurname1 = object.getString(ManageTABLE.COLUMN_Surname);
                            String strAddress1 = object.getString(ManageTABLE.COLUMN_Address);
                            String strPhone1 = object.getString(ManageTABLE.COLUMN_Phone);
                            String strBread1 = object.getString(ManageTABLE.COLUMN_Bread);
                            String strPrice1 = object.getString(ManageTABLE.COLUMN_Price);
                            String strItem1 = object.getString(ManageTABLE.COLUMN_Item);

                            objManageTABLE.addNewOrderFinish(stridReceive, strName1, strDate, strSurname1,
                                   strAddress1, strPhone1, strBread1, strPrice1, strItem1);



                            break;


                    }   //switch


                }


            } catch (Exception e) {
                Log.d(TAG, "Update ==> " + e.toString());
            }


            intTimes += 1;
        }   //while

    }   // synJSONtoSQLite

    public void clickNewRegister(View view) {
        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
    }


    private void deleteAllSQLite() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null); // MODE_PRIVATE คือ ลบข้อมูลในตาราง แต่ไม่ลบตารางออก
        objSqLiteDatabase.delete(ManageTABLE.TABLE_USER, null, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_BREAD, null, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER, null, null);
        objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER_FINISH, null, null);
    }

    private void testAddValue() {
        objManageTABLE.addNewUser("testUser", "testPass", "testName",
                "testSurname", "testAddress", "testPhone", "testCom");
        objManageTABLE.addNewBread("testBread", "testPrice", "testAmount", "testImage");
        objManageTABLE.addNewOrder("testName","testDate", "testSurname", "testAddress",
                "testPhone", "testBread", "testPrice", "testItem");
    }
}   // Main class
