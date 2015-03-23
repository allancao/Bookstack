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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import android.widget.Button;
import android.widget.PopupWindow;

import bookstack.Tools.Statistics;

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

        // add Books
        db.addBook(new Book("Android Application Development Cookbook", "Wei Meng Lee"));
        db.addBook(new Book("Android Programming: The Big Nerd Ranch Guide", "Bill Phillips and Brian Hardy"));
        db.addBook(new Book("Learn Android App Development", "Wallace Jackson"));

        // get all books
        List<Book> list = db.getAllBooks();

        // delete one book
        db.deleteBook(list.get(0));

        // get all books
        db.getAllBooks();

        // READ PERIOD
        db.addReadPeriod(new ReadPeriod(
            1420074061,
            1420080000,
            10,
            30,
            28,
            1
        ));

        db.addReadPeriod(new ReadPeriod(
            1520074061,
            1520080000,
            12,
            28,
            26,
            1
        ));

        db.addReadPeriod(new ReadPeriod(
            1520074061,
            1520080000,
            5,
            300,
            297,
            2
        ));

        db.getAllReadPeriod();
        db.getAllReadPeriod(1);
        db.getAllReadPeriod(2);

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
        } else if (position == 6) {
            startActivity(new Intent(getBaseContext(), DeviceListActivity.class));
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
            public void run()
            {
                final MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                Date utilDate = Calendar.getInstance().getTime();
                LinkedList<Integer> average = new LinkedList<>();
                boolean opened = false;
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
                                    average.add(Integer.parseInt(data.substring(0, data.length() - 1)));
                                    average.removeFirst();

                                    if (isOpen(average) && !opened) {
                                        utilDate = Calendar.getInstance().getTime();
                                        opened = true;
                                        Log.d("BT", "BOOK IS OPENED");
                                    } else if (!isOpen(average) && opened) {
                                        final long startTime = utilDate.getTime();
                                        final long endTime = Calendar.getInstance().getTime().getTime();
                                        handler.post(new Runnable() {
                                            public void run() {
                                                db.addReadPeriod(new ReadPeriod(
                                                        startTime,
                                                        endTime,
                                                        12,
                                                        28,
                                                        26,
                                                        1
                                                ));
                                            }
                                        });
                                        Log.d("BT", "Start time: " + startTime + " End time: " + endTime);
                                        Log.d("BT", "BOOK IS CLOSED");
                                        opened = false;
                                    }
                                } else {
                                    average.add(Integer.parseInt(data.substring(0, data.length() - 1)));
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
            }
        });

        workerThread.start();
    }

    public boolean isOpen(List<Integer> list) {
        return Statistics.median(list) < 20;
    }

}

