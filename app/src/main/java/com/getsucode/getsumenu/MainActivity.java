package com.getsucode.getsumenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    //general
    private static final String urlListMenu = "https://www.dropbox.com/s/q5vkrmy9sd0bvo8/getsumenu.xml?dl=1";
    private int totalHarga = 0;
    private List<ItemMenu> listDataMenu;
    private String pesanan = "";

    //component
    private GridView gridView;
    private TextView txtTotal;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       /* MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Ads Event
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }); */

        // ADS

        txtTotal = findViewById(R.id.total);
        gridView = findViewById(R.id.gridViewMenu);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewItem(listDataMenu.get(position));
            }
        });

        if (listDataMenu == null) {
            new MyTask().execute(urlListMenu);
        }

        swipe = findViewById(R.id.swipeMenu);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MyTask().execute(urlListMenu);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkout:
                viewPembayaran();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class MyTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                listDataMenu = getData(params[0]);
            } catch (Exception e) {
                swipe.setRefreshing(false);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            swipe.setRefreshing(false);
            if (null == listDataMenu || listDataMenu.size() == 0) {
                Toast.makeText(MainActivity.this, "Menu Tidak Ditemukan", Toast.LENGTH_LONG).show();
            } else {
                gridView.setAdapter(new AdapterGridview(MainActivity.this, R.layout.gridview, listDataMenu));
            }

            pesanan = " ";
            setTotal(0);
            int total = setTotal(0);
            txtTotal.setText("Total Pesanan : Rp." + total);
        }
    }

    public List<ItemMenu> getData(String url) {
        ItemMenu objItem;
        List<ItemMenu> listItem = null;

        try {
            listItem = new ArrayList<>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(url).openStream());
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("item");

            int batas = nList.getLength();

            for (int temp = 0; temp < batas; temp++) {
                {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        objItem = new ItemMenu();
                        objItem.setNama(getTagValue("nama", eElement));
                        objItem.setHarga(getTagValue("harga", eElement));
                        objItem.setLink(getTagValue("link", eElement));
                        listItem.add(objItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listItem;
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = nlList.item(0);
        return nValue.getNodeValue();
    }

    private void viewItem(final ItemMenu data) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.item, null);
        ImageView imgItem = convertView.findViewById(R.id.imgItem);
        TextView txtDesc = convertView.findViewById(R.id.txtDesc);
        final EditText txtJml = convertView.findViewById(R.id.txtJml);

        Glide.with(this).load(data.getLink()).into(imgItem);
        txtDesc.setText(data.getNama() + " | Rp." + data.getHarga());
        pesanan += txtDesc.getText().toString() + "\n";

        alertDialog.setView(convertView).setTitle("");
        final AlertDialog mAlertDialog = alertDialog.setPositiveButton("PESAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pesanan = Integer.valueOf(data.getHarga());
                if (!txtJml.getText().toString().equals("") && !txtJml.getText().toString().equals("0")) {
                    pesanan = Integer.valueOf(txtJml.getText().toString()) * Integer.valueOf(data.getHarga());
                }
                setTotal(pesanan);
            }
        }).create();

        mAlertDialog.show();
    }

    private void viewPembayaran() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.payment, null);

        final TextView txtTotal = convertView.findViewById(R.id.txtTotal);
        final TextView txtPesanan = convertView.findViewById(R.id.txtPesanan);

        txtPesanan.setText(pesanan);
        txtTotal.setText(this.txtTotal.getText().toString());

        alertDialog.setView(convertView).setTitle(" ");
        final AlertDialog mAlertDialog = alertDialog.setPositiveButton("BAYAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Terima Kasih", Toast.LENGTH_LONG).show();
                new MyTask().execute(urlListMenu);
                pesanan = " ";
                setTotal(0);
            }
        }).create();

        mAlertDialog.show();
    }

    private int setTotal(int nilai) {
        totalHarga = totalHarga + nilai;
        txtTotal.setText("Total Pesanan : Rp. " + totalHarga);
        return nilai;
    }
}
