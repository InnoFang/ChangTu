package com.example.innf.newchangtu.Map.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.example.innf.newchangtu.Map.bean.Track;
import com.example.innf.newchangtu.Map.model.TrackLab;
import com.example.innf.newchangtu.Map.utils.AddressInitTask;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class StartRecordActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    private static final String TAG = "StartRecordActivity";

    private static final String DEFAULT_BUTTON_TEXT = "请选择";
    private static final String DEFAULT_EDIT_TEXT_TEXT = "请填写";
    public static final String EXTRA_TRACK = "com.example.innf.changtu.view.activity.track";
    public static final String EXTRA_TIME_INTERVAL = "com.example.innf.changtu.view.activity.time_interval";
    public static final String EXTRA_START = "com.example.innf.changtu.view.activity.start";
    public static final String EXTRA_END = "com.example.innf.changtu.view.activity.end";
    public static final String EXTRA_PHONE = "com.example.innf.changtu.view.activity.phone";
    public static final String EXTRA_START_POSITION = "com.example.innf.changtu.view.activity.start_position";
    private static final int REQUEST_CONTACT = 1;

    private Button mStartPositionButton;/*区域选择：出发地区域选择*/
    private Button mChooseAreaButton;/*区域选择：目的地区域选择*/
    private EditText mChoosePlaceEditText;/*地址名选择：目的地地址名选择*/
    private Button mSelectTransportationButton;/*乘车类型选择*/
    private RadioGroup mTimeIntervalRadioGroup;/*时间间隔选择*/
    private LinearLayout mTimeIntervalLinearLayout;/*自定义事件间隔布局*/
    private Button mSelectContractsButton;/*选择联系人*/
    private EditText mRemarkEditText;/*备注信息*/
    private EditText mTimeIntervalOtherEditText;/*自定义时间间隔*/

    private int mTimeInterval = 10;/*默认时间间隔*/
    private String phone;/*保存联系人手机号码*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_record);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String startPosition = getIntent().getStringExtra(EXTRA_START_POSITION);

        mStartPositionButton = (Button) findViewById(R.id.start_position_button);
        mChooseAreaButton = (Button) findViewById(R.id.choose_area_button);
        mChoosePlaceEditText = (EditText) findViewById(R.id.choose_place_edit_text);
        mSelectTransportationButton = (Button) findViewById(R.id.select_transportation_button);
        mTimeIntervalRadioGroup = (RadioGroup) findViewById(R.id.time_interval_radio_group);
        mTimeIntervalLinearLayout = (LinearLayout) findViewById(R.id.time_interval_other_linear_layout);
        mTimeIntervalOtherEditText = (EditText) findViewById(R.id.time_interval_other_edit_text);
        mSelectContractsButton = (Button) findViewById(R.id.select_contracts_button);
        mRemarkEditText = (EditText) findViewById(R.id.remark_edit_text);

        mStartPositionButton.setText(startPosition);
        mChooseAreaButton.setOnClickListener(this);
        mSelectTransportationButton.setOnClickListener(this);
        mSelectContractsButton.setOnClickListener(this);
        mTimeIntervalRadioGroup.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.choose_area_button:
                new AddressInitTask(this, mChooseAreaButton).execute("山西", "太原", "尖草坪区");
                break;
            case R.id.select_transportation_button:
                showSelectTransportationDialog();
                break;
            case R.id.select_contracts_button:
//                Intent intent = RelateFriendsActivity.newIntent(this);
//                startActivityForResult(intent, REQUEST_CODE);
                Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(pickContact, REQUEST_CONTACT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode){
//            case REQUEST_CODE:
//                Log.d(TAG, "onActivityResult: is called");
//                if (resultCode == Activity.RESULT_OK){
//                    String contracts = data.getStringExtra(RelateFriendsActivity.EXTRA_KEY_CONTRACTS_STRING);
//                }
//                break;
//        }
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_CONTACT && null != data) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor c = getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                c.moveToFirst();
                String contract = c.getString(0);  /*获取第0列数据, 即手机联系人名字*/
                String phoneNumber = c.getString(1);
                Log.d(TAG, phoneNumber);
                phone = c.getString(1);    /*获取第1列数据，即手机联系人手机号码*/
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                mSelectContractsButton.setText(contract);
            }finally {
                c.close();
            }

        }
    }

    public static Intent newIntent(Context context, String startPosition){
        Intent intent = new Intent(context, StartRecordActivity.class);
        intent.putExtra(EXTRA_START_POSITION, startPosition);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.start_record_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /******************************/
    /*缺少关联联系人！！！！！！！！*/
    /******************************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_start_record:
                if (!isRecordEmpty()){
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.show();
                    final Track track = new Track();
                    final String startPosition = mStartPositionButton.getText().toString();
                    track.setStartPosition(startPosition);
                    String endPlace = mChoosePlaceEditText.getText().toString();
                    final String endPosition = mChooseAreaButton.getText().toString() + endPlace;
                    track.setEndPosition(endPosition);
                    String transportation = mSelectTransportationButton.getText().toString();
                    track.setTransportation(transportation);
                    track.setTakeTime("2小时15分钟");
                    track.setDistance("19公里");
                    String remark = mRemarkEditText.getText().toString();
                    track.setRemark(remark);
                    TrackLab.get(this).addTrack(track);
                    track.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (null == e){
                                track.setObjectId(s);
                                track.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (null == e){
                                            progressDialog.cancel();
                                            sendResult(track, startPosition, endPosition);
                                            finish();
                                            showToast("创建成功");
                                        } else {
                                            showToast("创建失败");
                                        }
                                    }
                                });
                                showToast("开始记录");
                            }else{
                                showToast(e.getMessage());
                            }
                        }
                    });
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.time_interval_ten_radio_button:
                showOtherTimeIntervalLayout(false);
                mTimeInterval = 10;
                break;
            case R.id.time_interval_thirty_radio_button:
                showOtherTimeIntervalLayout(false);
                mTimeInterval = 30;
                break;
            case R.id.time_interval_sixty_radio_button:
                showOtherTimeIntervalLayout(false);
                mTimeInterval = 60;
                break;
            case R.id.time_interval_other_radio_button:
                showOtherTimeIntervalLayout(true);
                break;
        }
    }

    @SuppressWarnings("deprecation")/*消除过时方法警告*/
    public void showSelectTransportationDialog(){
        String[] rideType = {
                "公共汽车", "中型载客车", "出租车", "私家车", "自行车", "步行", "其它"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, rideType
        );
        View view1 = View.inflate(this, R.layout.show_dialog, null);
        final ListView listView = (ListView) view1.findViewById(R.id.dialog_list_view);
        listView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle("选择交通工具")
                .setView(view1)
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        dialog.getWindow().setLayout(width * 4 / 5, height / 2 );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectTransportationButton.setText(listView.getItemAtPosition(i).toString());
                dialog.dismiss();
            }
        });
    }


    /*是否显示自定义事件间隔布局*/
    public void showOtherTimeIntervalLayout(boolean show){
        if (show){
            mTimeIntervalLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mTimeIntervalLinearLayout.setVisibility(View.GONE);
            mTimeIntervalOtherEditText.setText("");
        }
    }

    public boolean isRecordEmpty(){
        if (mChooseAreaButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showToast("请选择地点区域");
            return true;
        }
        if (mChoosePlaceEditText.getText().toString().equals(DEFAULT_EDIT_TEXT_TEXT)){
            showToast("请填写地点名");
            return true;
        }
        if (mSelectTransportationButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showToast("请选择交通工具");
            return true;
        }
        if (mSelectContractsButton.getText().toString().equals(DEFAULT_BUTTON_TEXT)){
            showToast("请选择联系人");
            return true;
        }
        return false;
    }

    private void sendResult(Track track, String start, String end){
        Intent data = new Intent();
        data.putExtra(EXTRA_TRACK, track);
        String otherTimeInterval = mTimeIntervalOtherEditText.getText().toString();
        if (!otherTimeInterval.equals("")){
            mTimeInterval = Integer.parseInt(otherTimeInterval);
        }
        data.putExtra(EXTRA_TIME_INTERVAL, mTimeInterval);
        data.putExtra(EXTRA_START, start);
        data.putExtra(EXTRA_END, end);
        data.putExtra(EXTRA_PHONE, phone);
        setResult(Activity.RESULT_OK, data);
    }
}
