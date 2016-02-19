package drucc.sittichok.heyheybread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class ConfirmOrderActivity extends AppCompatActivity {

    // Explicit
    private TextView dateTextView, nameTextView,addressTextView,
            phoneTextView,totalTextView, idReceiveTextView;
    private String dateString,nameString,surnameString, addressString,
            phoneString,totalString;
    private ListView orderListView;
    private int totalAnInt = 0;
    private String strCurrentIDReceive;
    private Button moreButton, finishButton;
    private boolean visibleStatus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        // Bind Widget  กำหนตตำแหน่งในรายการสั่งซื้อ
        bindWidget();

        //Check Visible Button
        checkVisible();

        // Read All Data  นำค่าที่ลูกค้าสั่งมาแสดง และ ส่งค่า ชื่อ นามสกุล ที่ อยู่ เบอร์ โทร ของ ลูกค้า และรายการที่สั่ง
        readAllData();

        // Find ID receive
        findIDreceive();

        //Show View   โชว์ ชื่อ นามสกุล ที่ อยู่ เบอร์โทร ราคารวม
        showView();

    }   // Main Method

    private void checkVisible() {

        try {

            boolean myStatus = getIntent().getBooleanExtra("Status", false);

            if (myStatus) {

                moreButton.setVisibility(View.INVISIBLE);
                finishButton.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace(); // ปริ้นบน ลอคแคท
        }

    }   // checkVisible

    private void findIDreceive() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER_FINISH, null);
        objCursor.moveToFirst();
        objCursor.moveToLast();

        String strIDreceive = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_idReceive));
        Log.d("Receive", "Receive Last = " + strIDreceive);

        String[] idReceiveStrings = strIDreceive.split("#");
        int inttext = (Integer.parseInt(idReceiveStrings[1]) + 1);

        Log.d("Receive", "text = " + inttext);

        strCurrentIDReceive = idReceiveStrings[0] + "#" + (Integer.toString(inttext));
        idReceiveTextView.setText(strCurrentIDReceive);


        objCursor.close();



    }   // findIDreceive

    public void clickFinish(View view) {

        //Read All orderTABLE
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME, // เปิดฐานข้อมูล
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);  // เลือกOrder ทั้งหมด
        objCursor.moveToFirst();  // ให้เลือกตำแหน่ง ของข้อมูล Order อยู่บนสุด

        //**********************************************************************************************************************
        // Update Stock
        //**********************************************************************************************************************

        for (int i =0; i<objCursor.getCount();i++) {    // นำOrder มานับแถว ถ้ามีข้อมูล ให้ทำ

            String strDate = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Date));  // รับค่า เวลา
            String strName = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Name)); // รับค่า ชื่อ
            String strSurname = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Surname)); // รับค่านามสกุล
            String strAddress = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Address)); // รับค่าที่อยู๋
            String strPhone = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Phone)); // รับค่าเบอร์โทร
            String strBread = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread)); // รับค่าชื่อขนมปัง
            String strPrice = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price)); // รับค่าราคา
            String strItem = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item)); // รับค่าไอเทม

            // Update to mySQL
            StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy
                    .Builder().permitAll().build();
            StrictMode.setThreadPolicy(myPolicy);   // อนุญาตืให้ myPolicy เชื่อมต่อ โปรโตคอล ได้

            //Update breadTABLE
            updateBreadStock(strBread, strItem);




            // Update orderTABLE_mos
            try {

                ArrayList<NameValuePair> objNameValuePairs = new ArrayList<NameValuePair>();
                objNameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
                objNameValuePairs.add(new BasicNameValuePair("idReceive", strCurrentIDReceive));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Date, strDate));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Name, strName));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Surname, strSurname));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Address, strAddress));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Phone, strPhone));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Bread, strBread));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Price, strPrice));
                objNameValuePairs.add(new BasicNameValuePair(ManageTABLE.COLUMN_Item, strItem));

                HttpClient objHttpClient = new DefaultHttpClient(); // เปิดโปรโตคอล
                HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/mos/php_add_order_master.php"); // ลิ้งไปที่ไฟล์นี้
                objHttpPost.setEntity(new UrlEncodedFormEntity(objNameValuePairs, "UTF-8")); // ให้ลองรับ ภาษาไทย
                objHttpClient.execute(objHttpPost);

                if (i == (objCursor.getCount() - 1) ) {

                    Toast.makeText(ConfirmOrderActivity.this,"Confirm success", // โชว์ข้อความการยืนยัน 3.5 วินาที
                            Toast.LENGTH_SHORT).show();
                }   // if


            } catch (Exception e) {
                Log.d("hey", "Error Cannot Update to mySQL ==> " + e.toString());
            }   // end of TryCase 1


            try {

                //Find Id Bread
                ManageTABLE objManageTABLE = new ManageTABLE(this);
                String[] resultStrings = objManageTABLE.SearchBread(strBread);
                Log.d("16Feb", "id bread " + strBread + " " + resultStrings[0]);


            } catch (Exception e) {
                Log.d("16Feb", "Cannot Delete Stock");

            }// end of TryCase 2



            objCursor.moveToNext(); // ทำต่อ

        }   // for
        objCursor.close(); // คืนหน่วยความจำ

        // Intent HubActivity
        Intent objIntent = new Intent(ConfirmOrderActivity.this, HubActivity.class);
                                // ทำเสร็จแล้ว ให้ กลับไปหน้า HubActivity.class
        String strID = getIntent().getStringExtra("idUser");
        objIntent.putExtra("ID", strID); //แล้วส่งค่า ID คืนไปที่หน้า HubActivity.class ด้วย

        Log.d("19Feb", "ID ที่ได้ ==> " + strID);

        startActivity(objIntent);

        //Delete OrderTABLE
        objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,null,null);


    }   // clickFinish

    private void updateBreadStock(String strBread, String strItem) {

        // หา ID ของ Bread
        try {

            ManageTABLE objmanageTABLE = new ManageTABLE(this);
            String[] resultBread = objmanageTABLE.searchBreadStock(strBread);

            Log.d("19Feb", "ID bread ==> " + resultBread[0]);



        } catch (Exception e) {

            e.printStackTrace();
        }

    }   // updateBreadStock


    public void clickMore(View view) {

        finish(); // ปิดหน้าต่าง แสดงรายการลง แล้ว จะไป โชว์ ที่หน้า สั่งซื้อสินค้า

    } // clickMore

    private void showView() {
        dateTextView.setText("Date : " + dateString);
        nameTextView.setText("Name : " + nameString + " " + surnameString);
        addressTextView.setText("Address : " + addressString);
        phoneTextView.setText("Phone : " + phoneString );
        totalTextView.setText(Integer.toString(totalAnInt) + "      Baht");
    }   // showView

    private void readAllData() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null); // เปิดฐานข้อมูล Heyhey.db
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM orderTABLE", null); //ดึง Order ที่สั่งทั้งหมดจากฐานข้อมูล
        objCursor.moveToFirst();  // แล้วให้ไปอยู่ที่ Order แรก
        dateString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Date)); // รับค่า เวลา
        nameString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Name)); // รับค่า ชื่อ
        surnameString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Surname)); // รับค่า นามสกุล
        addressString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Address)); // รับค่า ที่อยู่
        phoneString = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Phone)); // รับค่าเบอร์โทร

        String[] nameOrderStrings = new String[objCursor.getCount()]; // นับจำนวนของ ชื่อสินค้า
        String[] priceStrings = new String[objCursor.getCount()]; // นับจำนวนของ ราคาสินค้า
        String[] itemStrings = new String[objCursor.getCount()]; // นับจำนวนของ จำนวนสินค้า
        String[] noStrings = new String[objCursor.getCount()]; // นับจำนวนของ ลำดับไอเทม
        String[] amountStrings = new String[objCursor.getCount()]; // นับจำนวน ราคารวม คือ item * price ได้ sum ผลรวม



        for (int i=0; i<objCursor.getCount();i++) {

            nameOrderStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread)); // รับค่าชื่อขนมปัง
            priceStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Price)); // รับค่าราคา
            itemStrings[i] = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item)); // รับค่าจำนวน
            noStrings[i] = Integer.toString(i + 1); // +1 เพราะ ลำดับ เริ่มที่ 1 แล้ว บวก ไปเลื่อยๆ จนหมด เช่น 1 2 3 4
            amountStrings[i] = Integer.toString( Integer.parseInt(itemStrings[i])* Integer.parseInt(priceStrings[i]) );
            // Sum ผลรวมของ item*price

            objCursor.moveToNext(); // เช่น ทำลำดับที่ 1 เสร็จ แล้ว ทำลำดับที่ 2 ต่อ Next จนกว่าจะหมด

            totalAnInt = totalAnInt + Integer.parseInt(amountStrings[i]);
            // ค่าผมรวมทั้งหมด total เอา amountStrings[i] ทั้งหมด มา+กัน

        }   // for


        objCursor.close();

        // Create Listview
        MyOrderAdapter objMyOrderAdapter = new MyOrderAdapter(ConfirmOrderActivity.this,
                noStrings, nameOrderStrings, itemStrings, priceStrings, amountStrings);
        orderListView.setAdapter(objMyOrderAdapter);

        // Delete Order
        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                myDeleteOrder(i);

            } // event
        });

    }   // readAllData

    private void myDeleteOrder(int position) {

        final SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);
        objCursor.moveToFirst();
        objCursor.moveToPosition(position);
        String strBread = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Bread));
        String strItem = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_Item));
        final String strID = objCursor.getString(objCursor.getColumnIndex(ManageTABLE.COLUMN_id));
        Log.d("Hay", "ID ==> " + strID);


        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.icon_myaccount);
        objBuilder.setTitle("Are you sure ? ");
        objBuilder.setMessage("Delete order " + strBread +" " +strItem + "ชิ้น");
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int intID = Integer.parseInt(strID);
                objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,
                        ManageTABLE.COLUMN_id + "=" + intID, null );
                totalAnInt = 0;
                readAllData();
                totalTextView.setText(Integer.toString(totalAnInt));

                dialogInterface.dismiss();
            }
        });
        objBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();

        objCursor.close();

    }   // myDeleteOrder

    private void bindWidget() {

        dateTextView = (TextView) findViewById(R.id.textView19);  // ตำแหน่ง เวลา
        nameTextView = (TextView) findViewById(R.id.textView20);  // ตำแหน่ง ชื่อ
        addressTextView = (TextView) findViewById(R.id.textView21); // ตำแหน่งที่อยู่
        phoneTextView = (TextView) findViewById(R.id.textView22); // ตำแหน่งเบอร์
        totalTextView = (TextView) findViewById(R.id.textView23); // ตำแหน่งราคารวม
        orderListView = (ListView) findViewById(R.id.listView2); // ตำแหน่งรายการสินค้าที่ลูกค้าสั่งซื้อ
        idReceiveTextView = (TextView) findViewById(R.id.textView30); // ตำแหน่ง รหัสรายการสั่งซื้อ
        moreButton = (Button) findViewById(R.id.button6);
        finishButton = (Button) findViewById(R.id.button7);

    }   //bindWidget

}   // Main class
