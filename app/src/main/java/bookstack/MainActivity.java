/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bookstack;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import bookstack.Tools.Parser;
import bookstack.Tools.SignedRequestsHelper;
import bookstack.Tools.Statistics;
import bookstack.Tools.UrlParameterHandler;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    //Bluetooth
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    static BluetoothSerialService mSerialService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = "Features";
        mPlanetTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        // DB TEST CASES. can comment out. please keep.
        MySQLiteHelper db = new MySQLiteHelper(this);

        // pre-populate db
        if (db.getAllReadPeriod().size() == 0) {
            // add Books
            db.addBook(new Book("Design Patterns", "Allan Cao"));

            // add read period
            db.addReadPeriod(new ReadPeriod(
                    1426813200, // Mar 20 2015 1:00
                    1426816800, // Mar 20 2015 2:00
                    12,
                    28,
                    26,
                    1
            ));

            db.addReadPeriod(new ReadPeriod(
                    1426813200, // Mar 20 2015 1:00
                    1426816800, // Mar 20 2015 2:00
                    12,
                    28,
                    26,
                    1
            ));

            db.addReadPeriod(new ReadPeriod(
                    1426899600, // Mar 21 2015 1:00
                    1426901400, // Mar 21 2015 1:30
                    5,
                    300,
                    297,
                    1
            ));

            db.addReadPeriod(new ReadPeriod(
                    1426986000, // Mar 22 2015 1:00
                    1426993200, // Mar 22 2015 3:00
                    5,
                    300,
                    297,
                    1
            ));

            // READ PERIOD
            db.addReadPeriod(new ReadPeriod(
                    1427072400, // Mar 23 2015 1:00
                    1427076000, // Mar 23 2015 2:00
                    10,
                    30,
                    28,
                    1
            ));

        }

        // get all books
        // List<Book> list = db.getAllBooks();

        // delete one book
        // db.deleteBook(list.get(0));

        // get all books
        // db.getAllBooks();

//        db.getAllReadPeriod();
//        db.getAllReadPeriod(1);
//        db.getAllReadPeriod(2);

        // POPUP TEST
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                initiatePopupWindow();
//            }
//        }, 100);


        // activate bluetooth
        try {
            findBT();
            openBT();

        } catch(IOException e) {
            Log.e("BT Error", "Could not open bt");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Fragment graph = new Graph();
        Fragment reco = new RecommendationFragment();
        Fragment blueToothPair = new BluetoothFragment();
        Fragment week = new WeekFragment();
        Fragment progress = new ProgressFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        if (position == 0) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, progress).commit();
        } else if (position == 1) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, reco).commit();
        } else if (position == 2) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, week).commit();
        } else if (position == 3) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, graph).commit();
        } else if (position == 5) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, blueToothPair).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Log.e("BT", "bt adapter not available");
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("SeeedBTSlave")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        Log.d("BT", "BT Connected");
    }

    public void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID

        mmDevice.fetchUuidsWithSdp();
        BluetoothConnector bluetoothConnector = new BluetoothConnector(mmDevice, false, mBluetoothAdapter, null);
        BluetoothConnector.BluetoothSocketWrapper wrapper = bluetoothConnector.connect();

        mmOutputStream = wrapper.getOutputStream();
        mmInputStream = wrapper.getInputStream();

        beginListenForData();

        Log.d("BT", "BT Opened");
    }

    public void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run() {
                Looper.prepare();
                final MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                Date utilDate = Calendar.getInstance().getTime();
                LinkedList<Integer> average = new LinkedList<>();
                boolean opened = false;
                int startForce = 0;
                while(average.size() < 20) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            String data = "";
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                            if (!data.isEmpty()) {
                                average.add(Integer.parseInt(data.substring(0, data.length()-1)));
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        Log.e("BT", e.getMessage());
                        stopWorker = true;
                    }
                }

                if (isOpen(average)) {
                    opened = true;
                    startForce = (int) Statistics.getMean(average);
                    new Thread() {
                        public void run() {
                            try {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        initiatePopupWindow();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }

                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            String data = "";
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                            if (!data.isEmpty()) {
                                if (average.size() >= 20) {
                                    average.add(Math.abs(Integer.parseInt(data.substring(0, data.length() - 1))));
                                    average.removeFirst();

                                    if (isOpen(average) && !opened) {
                                        utilDate = Calendar.getInstance().getTime();
                                        opened = true;
                                        new Thread() {
                                            public void run() {
                                                try {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            initiatePopupWindow();
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                        startForce =(int) Statistics.getMean(average);
                                        Log.d("BT", "BOOK IS OPENED");
                                    } else if (!isOpen(average) && opened) {
                                        final long startTime = utilDate.getTime();
                                        final long endTime = Calendar.getInstance().getTime().getTime();
                                        final int endForce = (int) Statistics.getMean(average);
                                        final int sForce = startForce;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                db.addReadPeriod(new ReadPeriod(
                                                        startTime,
                                                        endTime,
                                                        1 - endForce/740,
                                                        sForce,
                                                        endForce,
                                                        1
                                                ));
                                            }
                                        });
                                        Log.d("BT", "Start time: " + startTime + " End time: " + endTime);
                                        Log.d("BT", "BOOK IS CLOSED");
                                        new Thread() {
                                            public void run() {
                                                try {
                                                    runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            dismissPopup();
                                                        }
                                                    });
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                        opened = false;
                                    }
                                } else {
                                    average.add(Math.abs(Integer.parseInt(data.substring(0, data.length() - 1))));
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        Log.e("BT", ex.getMessage());
                        stopWorker = true;
                    }
                }
                Looper.loop();
            }
        });

        workerThread.start();
    }

    public boolean isOpen(List<Integer> list) {
        return Statistics.median(list) < 25;
    }


    // The method that displays the popup.
    // http://mobilemancer.com/2011/01/08/popup-window-in-android/
    // start popup: initiatePopupWindow()
    // hide popup: pw.dismiss();
    private PopupWindow pw;
    private void initiatePopupWindow() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
//            LayoutInflater inflater = (LayoutInflater) MainActivity.this
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = getLayoutInflater().inflate(R.layout.popup_layout,
                    (ViewGroup) findViewById(R.id.popup_element));
            // create a 300px width and 470px height PopupWindow
            pw = new PopupWindow(layout, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true);
            // display the popup in the center
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button cancelButton = (Button) layout.findViewById(R.id.hide_message_button);
            cancelButton.setOnClickListener(cancel_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnClickListener cancel_button_click_listener = new OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };

    protected void dismissPopup() {
        try {
            if ((this.pw != null) && this.pw.isShowing()) {
                this.pw.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }
    }

    private String itemLookup(String isbn) throws Exception {
        SignedRequestsHelper helper = new SignedRequestsHelper();
        Parser parser = new Parser();

        Map<String, String> map = new HashMap<>();
        String url = helper.sign(map);

        NodeList nodeList = parser.getResponseNodeList(url);
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(nodeList.item(i));
        }

        return null;
    }

    public static void main(String[] args) throws Exception {

        SignedRequestsHelper helper = new SignedRequestsHelper();
        Parser parser = new Parser();

        Map<String, String> map = UrlParameterHandler.getInstance().buildMapForItemSearch("B005B1CECU");
        String url = helper.sign(map);
        System.out.println(url);

        NodeList nodeList = parser.getResponseNodeList(url);
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(nodeList.item(i).getNodeValue());
        }


    }

}

