package com.advantech.ethernetsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.advantech.ethernetsample.receiver.NetworkStateReceiver;
import com.advantech.ethernetsample.utils.IpGetUtil;
import com.advantech.ethernetsample.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String[] ethArray;

    private String mEthIndex;

    private NetworkStateReceiver networkStateReceiver;

    private EditText IPAddressText, MaskText, GatewayText, DNS1Text, DNS2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {
        ethArray = getResources().getStringArray(R.array.com_baudrate);
        Spinner ethernetEthSpinner = findViewById(R.id.ethernet_sp);
        ethernetEthSpinner.setSelection(0);
        ethernetEthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEthIndex = ethArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        IPAddressText = findViewById(R.id.ip);
        MaskText = findViewById(R.id.mask);
        GatewayText = findViewById(R.id.gateway);
        DNS1Text = findViewById(R.id.dns1);
        DNS2Text = findViewById(R.id.dns2);
        Button setStaticIpButton = findViewById(R.id.set_btn);
        setStaticIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IpGetUtil.setEthernetIP(MainActivity.this, "STATIC",
//                        "192.168.2.168", "255.255.255.0",
//                        "192.168.2.1", "4.4.4.4", "114.114.114.114");
                setStaticIp();
            }
        });
        Button dhcpCheckButton = findViewById(R.id.dhcp_btn);
        dhcpCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IpGetUtil.setEthernetIP(MainActivity.this, "DHCP",
//                        "", "", "", "", "");
                setDhcpIp();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        networkStateReceiver = new NetworkStateReceiver();
        registerReceiver(networkStateReceiver, filter);

        networkStateReceiver.setNetworkStateListener(new NetworkStateReceiver.NetworkStateListener() {
            @Override
            public void getNetworkState(int state) {
                String ip = IpGetUtil.getIpAddress(MainActivity.this);
                showToast(state > NetworkStateReceiver.NETSTATUS_INAVAILABLE
                        ? getString(R.string.ip_address, ip)
                        : getString(R.string.ip_address, "没有网络"));
            }
        });
    }

    protected void showToast(String message) {
        ToastUtil.show(this, message, Gravity.CENTER, Toast.LENGTH_SHORT);
    }

    protected void cancelToast() {
        ToastUtil.cancel();
    }

    private void setStaticIp() {
        if (TextUtils.isEmpty(IPAddressText.getText())
                || TextUtils.isEmpty(MaskText.getText())
                || TextUtils.isEmpty(GatewayText.getText())
                || TextUtils.isEmpty(DNS1Text.getText())
                || TextUtils.isEmpty(DNS2Text.getText())) {
            showToast("IP address info is null.");
            return;
        }
        IpGetUtil.setEthernetIP(this, "STATIC", IPAddressText.getText().toString(),
                MaskText.getText().toString(),
                GatewayText.getText().toString(),
                DNS1Text.getText().toString(),
                DNS2Text.getText().toString());
    }

    private void setDhcpIp() {
        IpGetUtil.setEthernetIP(this, "DHCP", "", "", "", "", "");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            cancelToast();
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelToast();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "unregisterReceiver");
        cancelToast();
        unregisterReceiver(networkStateReceiver);
    }
}