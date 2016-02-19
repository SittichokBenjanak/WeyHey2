package drucc.sittichok.heyheybread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class showMenuActivity extends AppCompatActivity {

    // Explicit
    private String strID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_menu);

        //ListView Controller
        ListViewController();

    }   //  onCreate

    public void clickConfirmOrder(View view) {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME, // เปิดฐานข้อมูล Heyhey.db
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER,null);
                                                // ดึงค่าจากตาราง OrderTABLE ทั้งหมด
        if (objCursor.getCount() > 0) { // นับค่าที่ดึงมาว่ามีกี่แถว แล้ว เปรียบเทียบกับ 0

            //Have Data มีข้อมูล
            Intent objIntent = new Intent(showMenuActivity.this, ConfirmOrderActivity.class); // ให้โชว์หน้า ConfirmOrderActivity
            objIntent.putExtra("idUser", strID); // ส่งID ของลูกค้าไปด้วย
            startActivity(objIntent);  // เปิดการส่ง

        } else {

            //No Data ไม่มีข้อมูล
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(showMenuActivity.this,"\n" + "Please order","กรุณาสั่งสินค้าก่อนครับ");
            // แสดงกล่องข้อความว่า "กรุณา Order","กรุณาสั่งอาหารก่อนครับ"
        }

    }   // clickConfirmOrder

    private void ListViewController() {

        // Setup Value Array
        ManageTABLE objManageTABLE = new ManageTABLE(this);

        final String[] breadStrings = objManageTABLE.readAllBread(1); // รับค่า ชื่อ ขนมปัง
        final String[] priceStrings = objManageTABLE.readAllBread(2); // ราคา ขนมปัง
        String[] stockStrings = objManageTABLE.readAllBread(3); // จำนวน ขนมปัง
        String[] iconStrings = objManageTABLE.readAllBread(4); // รูปขนมปัง

        ListView menuListView = (ListView) findViewById(R.id.listView);  // นำ ListView ที่สร้างมาใช้งาน
        MenuAdapter objMenuAdapter = new MenuAdapter(showMenuActivity.this, // ให้ ListView โชว์ค่า ชื่อ ราคา จำนวน รูป
                stockStrings, priceStrings, breadStrings, iconStrings);
        menuListView.setAdapter(objMenuAdapter);

        // Active When Click ListView   // ถ้าคลิก เลือก สินค้า จะโชว์ หน้าต่างจำนวน สินค้าให้เลือก
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChooseItem(breadStrings[i], priceStrings[i]); //ถ้าคลิกเลือกจำนวนสินค้าจะเรียกใช้เมดธอท ChooseItem
            }   // event
        });


    }   //  ListViewController

    private void ChooseItem(final String breadString, final String priceString) {

        CharSequence[] mySequences = {"1 ชิ้น", "2 ชิ้น", "3 ชิ้น", "4 ชิ้น", "5 ชิ้น",
                "6 ชิ้น", "7 ชิ้น", "8 ชิ้น", "9 ชิ้น", "10 ชิ้น",};
        //final int intItem = 0;

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setTitle(breadString);  // หัวข้อคือ ชื่อ ขนมปังที่เลือก
        objBuilder.setSingleChoiceItems(mySequences, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int intItem = i +1;
                UpdateOrderToSQLit(breadString, priceString, intItem);

                dialogInterface.dismiss(); // ปิดหน้าต่าง ที่โชว์
            }   // event
        });

        objBuilder.show();


    }   // ChooseItem

    private void UpdateOrderToSQLit(String breadString, String priceString, int intItem) {

        strID = getIntent().getStringExtra("ID"); // อ่านค่า ID ว่า ลูกค้าคนนี้คือใคร
        int intID = Integer.parseInt(strID);  //  parseInt(strID) ถ้าโยน อักษร 5 มา จะเป็น เลข 5
        ManageTABLE objManageTABLE = new ManageTABLE(this);
        String[] resultStrings = objManageTABLE.readAtPosition(intID -1);

        addValueToSQLite(resultStrings[1],  // ชื่อลูกค้า
                resultStrings[2],   // นามสกุลลูกค้า
                resultStrings[3],   // ที่อยู๋ลูกค้า
                resultStrings[4],   // เบอร์โทรลูกค้า
                breadString,        // ชื่อขนมปังที่สั่ง
                priceString,        // ราคาขนมปัง
                Integer.toString(intItem));  // จำนวนขนมปัง


    }   //UpdateOrderToSQLit

    private void addValueToSQLite(String strName, String strSurname,
                                  String strAddress, String strPhone,
                                  String strbread, String strPrice, String strItem) {

        Log.d("hey", "Name " +strName);
        Log.d("hey", "Surname " +strSurname);
        Log.d("hey", "Address " +strAddress);
        Log.d("hey", "Phone " +strPhone);
        Log.d("hey", "Bread " +strbread);
        Log.d("hey", "Price " +strPrice);
        Log.d("hey", "Item " +strItem);

        //update to SQLite
        DateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy"); // วันที่ปัจจุบัน
        Date clickDate = new Date();
        String strDate = myDateFormat.format(clickDate);

        try {
            ManageTABLE objManageTABLE = new ManageTABLE(this);
            String[] myResultStrings = objManageTABLE.SearchBread(strbread); // ถ้าลูกค้า สั่งสินค้า ชื่อเดิม
            int oldItem = Integer.parseInt(myResultStrings[2]); //เอาไอเทมมา
            int newItem = Integer.parseInt(strItem) + oldItem;  // + กับไอเทมปัจจุบัน  parseInt เปลี่ยน String เป็น Integer
            String strNewItem = Integer.toString(newItem);

            SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                    MODE_PRIVATE,null);
            objSqLiteDatabase.delete(ManageTABLE.TABLE_ORDER,  // ลบที่ซ้ำ
                    ManageTABLE.COLUMN_id + "=" + Integer.parseInt(myResultStrings[0]),null );

            addOrderToMySQLite(strName,strDate,strSurname,strAddress,strPhone,strbread,strPrice, strNewItem);
            //ส่ง Orderที่ลูกค้าสั่ง อีก 1 แถว เพราะลบชื่อสินค้าที่ ลูกค้าสั่งซ้ำ


        } catch (Exception e) {
            addOrderToMySQLite(strName,strDate,strSurname,strAddress,  //ถ้าลูกค้าไม่ได้ เลือกสินค้า เดิม ก็ เพิ่ม ปกติ
                    strPhone,strbread,strPrice,strItem);
        }

    }   // addValueToSQLite

    private void addOrderToMySQLite(String strName, // ชื่อลูกค้า
                                    String strDate, // วันที่สั่ง
                                    String strSurname, // นามสกุล
                                    String strAddress, // ที่อยู๋
                                    String strPhone, // เบอร์โทร
                                    String strbread, // ชื่อขนม
                                    String strPrice, // ราคา
                                    String strItem) { // จำนวน
        ManageTABLE objManageTABLE = new ManageTABLE(this);
        objManageTABLE.addNewOrder(strName, strDate, strSurname,
                strAddress, strPhone, strbread, strPrice, strItem );

        Toast.makeText(showMenuActivity.this, "Add bread success",Toast.LENGTH_SHORT ).show();
        // โขว์ข้อความ "เพิ่มสินค้าสำเร็จ" แล้วหายไปภายใน 3.5 วิ
    }


}   // Main Class
