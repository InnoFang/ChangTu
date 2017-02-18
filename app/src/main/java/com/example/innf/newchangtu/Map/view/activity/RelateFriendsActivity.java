package com.example.innf.newchangtu.Map.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.innf.newchangtu.Map.adapter.ContractsAdapter;
import com.example.innf.newchangtu.Map.bean.Contracts;
import com.example.innf.newchangtu.Map.bean.User;
import com.example.innf.newchangtu.Map.model.ContractsLab;
import com.example.innf.newchangtu.Map.view.base.BaseActivity;
import com.example.innf.newchangtu.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RelateFriendsActivity extends BaseActivity {
    private static final String TAG = "RelateFriendsActivity";
    public static final String EXTRA_CONTRACTS = "com.example.innf.changtu.view.activity.contracts";
    private static final int REFRESH_COMPLETE = 0;

    private RecyclerView mRelateFriendsRecyclerView;
    private TextView mEmptyTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ContractsAdapter mContractsAdapter;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relate_friends);

        mUser = BmobUser.getCurrentUser(User.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        mRelateFriendsRecyclerView = (RecyclerView) findViewById(R.id.relate_friends_recycler_view);
        mEmptyTextView = (TextView) findViewById(R.id.empty_relate_friends_text_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mRelateFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        ContractsLab contractsLab = ContractsLab.get(this);
        List<Contracts> contractsList = contractsLab.getContractsList();

        showEmptyView(contractsList.isEmpty());

        queryContracts();
        if (null == mContractsAdapter) {
            mContractsAdapter = new ContractsAdapter(contractsList);
            mRelateFriendsRecyclerView.setAdapter(mContractsAdapter);
        } else {
            mContractsAdapter.setContractsList(contractsList);
        }

        mContractsAdapter.setOnItemClickListener(new ContractsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Contracts contracts) {
                operateContracts(contracts);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void operateContracts(final Contracts contracts) {
        String[] rideType = {
                "删除联系人", "共享位置"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, rideType
        );
        View view1 = View.inflate(this, R.layout.show_dialog, null);
        final ListView listView = (ListView) view1.findViewById(R.id.dialog_list_view);
        listView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle("关联亲友")
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
        dialog.getWindow().setLayout(width * 4 / 5, height * 2 / 5);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    deleteContracts(contracts);
                } else if (i == 1) {
                    returnContracts(contracts);
                }
                dialog.dismiss();
            }
        });
    }

    private void returnContracts(Contracts contracts) {
        sendContracts(contracts);
        finish();
    }

    private void sendContracts(Contracts contracts) {
        Intent data = new Intent();
        data.putExtra(EXTRA_CONTRACTS, contracts);
        setResult(Activity.RESULT_OK, data);
    }

    private void deleteContracts(final Contracts contracts) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle("你确定删除该联系人吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        contracts.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (null == e) {
                                    showToast("删除成功");
                                } else {
                                    showToast("删除失败");
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    private void queryContracts() {
        showEmptyView(false);
        mSwipeRefreshLayout.setRefreshing(true);
        BmobQuery<Contracts> query = new BmobQuery<>();
        query.order("-createAt");/*按照时间降序*/
        query.findObjects(new FindListener<Contracts>() {
            @Override
            public void done(List<Contracts> list, BmobException e) {
                if (null == e) {
                    mContractsAdapter.clear();
                    if (null == list || list.size() == 0) {
                        showEmptyView(true);
                        mContractsAdapter.notifyDataSetChanged();
                        return;
                    }
                    Log.d(TAG, "queryContracts: is called");
//                    mContractsAdapter.addAll(list);
                    mContractsAdapter.addAll(list, mUser.getUsername());
                    mRelateFriendsRecyclerView.setAdapter(mContractsAdapter);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    showEmptyView(true);
                }
            }
        });
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    updateUI();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    public void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            mRelateFriendsRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mRelateFriendsRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, RelateFriendsActivity.class);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.contracts_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_contracts:
                chooseRelateContractsWay();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")/*消除过时方法警告*/
    public void chooseRelateContractsWay() {
        String[] rideType = {
                "手机号", "畅途用户名"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, rideType
        );
        View view1 = View.inflate(this, R.layout.show_dialog, null);
        final ListView listView = (ListView) view1.findViewById(R.id.dialog_list_view);
        listView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setTitle("关联亲友")
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
        dialog.getWindow().setLayout(width * 4 / 5, height * 2 / 5);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    relateCTUserByPhone();
                } else if (i == 1) {
                    relateCTUserByName();
                }
                dialog.dismiss();
            }
        });
    }

    private void relateCTUserByPhone() {
        BmobQuery<User> query = new BmobQuery<>();
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setTitle("输入畅途用户手机号")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        queryCTUserByPhone(editText.getText().toString());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void relateCTUserByName() {
        BmobQuery<User> query = new BmobQuery<>();
        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setTitle("输入畅途用户名")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        queryCTUserByName(editText.getText().toString());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void queryCTUserByPhone(String phone) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("mobilePhoneNumber", phone);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (null == e) {
                    User user = list.get(0);
                    final Contracts contracts = new Contracts();
                    contracts.setContractUser(user);
                    contracts.setName(user.getName());
                    contracts.setPhoneNumber(user.getMobilePhoneNumber());
                    contracts.setShareConnect((Boolean) user.getObjectByKey("mIsShare"));
                    ContractsLab contractsLab = ContractsLab.get(RelateFriendsActivity.this);
                    contractsLab.addContracts(contracts);
                    contracts.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (null == e) {
                                showToast("添加成功");
                            } else {

                                showToast(e.getMessage());
                            }
                        }
                    });
                    updateUI();
                } else {
                    showToast(e.getMessage());
                }
            }
        });
    }

    public void queryCTUserByName(String username) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (null == e) {
                    User user = list.get(0);
                    final Contracts contracts = new Contracts();
                    Log.i(TAG, "done: use.name = " + user.getUsername());
//                        Log.i("relate", "relate : " + (String) user.getObjectByKey("name"));
                    contracts.setContractUser(user);
                    contracts.setName(user.getUsername());
                    contracts.setPhoneNumber(user.getMobilePhoneNumber());
                    contracts.setShareConnect((Boolean) user.getObjectByKey("mIsShare"));
                    ContractsLab contractsLab = ContractsLab.get(RelateFriendsActivity.this);
                    contractsLab.addContracts(contracts);
                    contracts.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (null == e) {
                                showToast("添加成功");
                            } else {
                                showToast(e.getMessage());
                            }
                        }
                    });
                    updateUI();
                } else {
                    showToast(e.getMessage());
                }
            }
        });

    }
}
