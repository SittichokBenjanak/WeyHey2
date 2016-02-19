package drucc.sittichok.heyheybread;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HubActivity extends AppCompatActivity implements View.OnClickListener {

    // Explicit
    private ImageView orderImageView, readImageView,
    editImageView, mapImageView, complacencyImageView;

    private String idString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        //Bind Widget
        bindWidget();

        //Image Controller
        imageController();

    }   // onCreate


    private void imageController() {

        idString = getIntent().getStringExtra("ID");
        orderImageView.setOnClickListener(this);
        readImageView.setOnClickListener(this);
        editImageView.setOnClickListener(this);
        mapImageView.setOnClickListener(this);
        complacencyImageView.setOnClickListener(this);

    }

    private void bindWidget() {
        orderImageView = (ImageView) findViewById(R.id.imageView2);
        readImageView = (ImageView) findViewById(R.id.imageView3);
        editImageView = (ImageView) findViewById(R.id.imageView4);
        mapImageView = (ImageView) findViewById(R.id.imageView5);
        complacencyImageView = (ImageView) findViewById(R.id.imageView6);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView2:
                Intent objIntent = new Intent(HubActivity.this, showMenuActivity.class);
                objIntent.putExtra("ID", idString);
                startActivity(objIntent);

                break;
            case R.id.imageView3:
                clickReadOrder();

                break;
            case R.id.imageView4:
                break;
            case R.id.imageView5:
                break;
            case R.id.imageView6:
                break;
        }   //switch


    }   // onClick

    private void clickReadOrder() {
        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase(MyOpenHelper.DATABASE_NAME,
                MODE_PRIVATE, null);
        Cursor objCursor = objSqLiteDatabase.rawQuery("SELECT * FROM " + ManageTABLE.TABLE_ORDER, null);
        if (objCursor.getCount() > 0) {
            Intent objIntent = new Intent(HubActivity.this, ConfirmOrderActivity.class);
            startActivity(objIntent);
        } else {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.errorDialog(HubActivity.this,"Please order","กรุณาสั่งสินค้าก่อนครับ");
        }

    }
}   // Main Class
