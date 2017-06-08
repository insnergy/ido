/*
 * Copyright (C) 2017 The InApi Project
 */
package com.insnergy.sample.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.insnergy.sample.R;
import com.insnergy.sample.domainobj.ApiResult;
import com.insnergy.sample.domainobj.Device;
import com.insnergy.sample.domainobj.DeviceInfo;
import com.insnergy.sample.domainobj.Widget;
import com.insnergy.sample.model.ApiCallback;
import com.insnergy.sample.presenter.DevicePresenter;
import com.insnergy.sample.view.js.JsEvaluator;
import com.insnergy.sample.view.js.interfaces.JsCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DeviceActivity extends AbstractAnimActivity {
    private static final String TAG = "DeviceActivity";
    private static final int REQUIRE_CODE = 1;

    private DevicePresenter mDevicePresenter = DevicePresenter.getInstance();
    private DeviceItemAdapter mNewDevItemAdapter;
    private WidgetItemAdapter mWidgetItemAdapter;
    private Device mSelectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setCurrentTabEnabled(R.id.imgDevList);

        Log.d(TAG, "onCreate here");

        ListView listViewNewDev = (ListView) findViewById(R.id.listViewNew);
        listViewNewDev.setOnItemClickListener(getNewDevItemClickListener());
        mNewDevItemAdapter = new DeviceItemAdapter(this, new ArrayList<Device>());
        listViewNewDev.setAdapter(mNewDevItemAdapter);

        ListView listViewDevices = (ListView) findViewById(R.id.listView);
        listViewDevices.setOnItemClickListener(getItemClickListener());
        listViewDevices.setOnItemLongClickListener(getItemLongClickListener());
        mWidgetItemAdapter = new WidgetItemAdapter(this, new ArrayList<Widget>());
        listViewDevices.setAdapter(mWidgetItemAdapter);

        final EditText editText = (EditText) findViewById(R.id.editTextDevAlias);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().toString() != null &&
                        !editText.getText().toString().isEmpty() &&
                        mSelectedDevice != null) {
                    findViewById(R.id.button).setAlpha(1f);
                } else {
                    findViewById(R.id.button).setAlpha(0.3f);
                }
            }
        });

        // 1. 取得未註冊裝置列表
        getNewDevices();

        // 2. 取得已註冊裝置列表
        getDevices();
    }

    public void scanQRCode(View v) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
            // 未安裝
            Toast.makeText(this, "請至 Play 商店安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show();
        } else {
            // SCAN_MODE, 可判別所有支援的條碼
            // QR_CODE_MODE, 只判別 QRCode
            // PRODUCT_MODE, UPC and EAN 碼
            // ONE_D_MODE, 1 維條碼
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

            // 呼叫ZXing Scanner，完成動作後回傳 1 給 onActivityResult 的 requestCode 參數
            startActivityForResult(intent, REQUIRE_CODE);
        }
    }

    // 接收 ZXing 掃描後回傳來的結果
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUIRE_CODE) {
            if (resultCode == RESULT_OK) {
                // ZXing回傳的內容
                String contents = intent.getStringExtra("SCAN_RESULT");
                decryptAES(contents, new JsCallback() {
                    @Override
                    public void onResult(String value) {
                        String[] params = value.split(";");
                        if (params.length >= 3) {
                            mSelectedDevice = new Device();
                            mSelectedDevice.setDev_id(params[1]);
                            mSelectedDevice.setDev_ext_type(DeviceInfo.Ext_Type.getEnum(params[2]));
                            ((TextView)findViewById(R.id.textSelectedDev)).setText(mSelectedDevice.getDev_id());
                        }
                    }

                    @Override
                    public void onError(String errorMessage) { }
                });
            } else if(resultCode==RESULT_CANCELED) {
                Toast.makeText(this, "取消掃描", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void decryptAES(String contents, JsCallback jsCallback) {
        //讀取 CryptoToJS 的 JS 檔來對 QR Code Content 做 decode 的動作
        StringBuilder jsCode = getJsCode();
        jsCode.append("var decryptedContent = CryptoJS.AES.decrypt('" + contents + "', key, options).toString(CryptoJS.enc.Utf8);\n");
        jsCode.append("jsEvaluatorResult += decryptedContent;");
        JsEvaluator jsEvaluator = new JsEvaluator(getApplicationContext());
        jsEvaluator.evaluate(jsCode.toString(), jsCallback);
    }

    private StringBuilder getJsCode() {
        StringBuilder jsCode = new StringBuilder(readFile("javascript/CryptoToJS.js"));
        jsCode.append("var key = CryptoJS.enc.Hex.parse('4875383519512e01aff63dc3958edec5'),\n");
        jsCode.append("iv = CryptoJS.enc.Hex.parse('7a70dad21d08b97e54b49c9a006ed02e'),\n");
        jsCode.append("options = { mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7, iv: iv },\n");
        jsCode.append("jsEvaluatorResult = ''; ");
        return jsCode;
    }

    private String readFile(String fileName) {
        try {
            AssetManager assetManager = getApplicationContext().getAssets();
            InputStream inputStream = assetManager.open(fileName);
            Scanner scanner = new Scanner(inputStream, "UTF-8");
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void addDevice(View view) {
        if (mSelectedDevice == null)
            return;

        String deviceAlias = ((EditText)findViewById(R.id.editTextDevAlias)).getText().toString();

        addDevice(mSelectedDevice.getDev_id(), mSelectedDevice.getDev_ext_type(), deviceAlias);
//        addDevice("BN90600347______TEST", DeviceInfo.Ext_Type.GATEWAY_600, deviceAlias);
    }

    private void addDevice(String deviceId, DeviceInfo.Ext_Type deviceExtType, String deviceAlias) {
        mDevicePresenter.addDevice(deviceId, deviceExtType, deviceAlias, new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                getDevices();
                getNewDevices();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void getNewDevices() {
        mDevicePresenter.getNewDevices(new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mNewDevItemAdapter.clear();
                mNewDevItemAdapter.addAll(apiResult.getDevices());
                mNewDevItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void getDevices() {
        mDevicePresenter.getDeviceWidgets(new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mWidgetItemAdapter.clear();
                mWidgetItemAdapter.addAll(apiResult.getWidgets());
                mWidgetItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private void deleteDevice(final Widget widget) {
        mDevicePresenter.deleteDevice(widget.getDev_id(), new ApiCallback() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                mWidgetItemAdapter.remove(widget);
                mWidgetItemAdapter.notifyDataSetChanged();

                getNewDevices();
            }

            @Override
            public void onFailure(ApiResult apiResult) { }
        });
    }

    private AdapterView.OnItemClickListener getNewDevItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedDevice = (Device)parent.getAdapter().getItem(position);
                ((TextView)findViewById(R.id.textSelectedDev)).setText(mSelectedDevice.getDev_id());
            }
        };
    }

    private AdapterView.OnItemClickListener getItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Widget item  = (Widget)parent.getAdapter().getItem(position);
                Log.i(TAG, "devid " + item.getDev_id());
                if (DeviceInfo.Ext_Type.OUTLET_02.equals(item.getDev_ext_type()) ||
                        DeviceInfo.Ext_Type.OUTLET_I18N_0910.equals(item.getDev_ext_type())) {
                    startActivity(new Intent(getApplicationContext(), ScheduleActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            .putExtra(ScheduleActivity.DEV_ID, item.getDev_id())
                    );
                }
            }
        };
    }

    private AdapterView.OnItemLongClickListener getItemLongClickListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Widget item  = (Widget)parent.getAdapter().getItem(position);
                showAlertDialogToDeleteDevice(item);
                return true;
            }
        };
    }

    private void showAlertDialogToDeleteDevice(final Widget item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceActivity.this);
        builder.setCancelable(false);
        builder.setMessage(R.string.delete_message)
                .setPositiveButton(R.string.delete_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteDevice(item);
                    }
                })
                .setNegativeButton(R.string.delete_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(TAG,  "not delete");
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
