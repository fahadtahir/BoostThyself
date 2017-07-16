package exploringpsyche.com.helloworld;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import android.os.AsyncTask;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.AdapterView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.GridView;



public class MyActivity extends AppCompatActivity implements View.OnClickListener {

    int selector;
    int previousrandomnumber = 0;
    int screentag = 0;
    static int lineno = 0;
    InputStream stream;
    List<String[]> quotes;
    ArrayList<Integer> saved = new ArrayList<Integer>();
    GridView gridView;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter;


    @Override
    public void onBackPressed() {
        if (screentag != 0) {
            setContentView(R.layout.layout);
            createGrid();
            screentag = 0;
        } else {
            System.exit(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        screentag = 0;
        creatCustomGrid();


        new DownloadFiles().execute();


        try {
            stream = openFile("quotes.csv");
            quotes = processFile(stream);

            try {
                FileReader f = new FileReader(getFilesDir() + "/saved_quotes.txt");
            } catch (IOException e) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(getFilesDir() + "/saved_quotes.txt", true));
                try {
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            FileReader f = new FileReader(getFilesDir() + "/saved_quotes.txt");
            BufferedReader reader = new BufferedReader(f);
            String line = reader.readLine();
            while (line != null) {
                try {
                    System.out.println(line);
                    saved.add(Integer.parseInt(line));
                } catch (Exception e) {

                }
                line = reader.readLine();
            }
            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        //check for internet connection on file load
        if (isOnline()) {
            System.out.println("aye, internet");
        } else {
            System.out.println("nae, internet");
        }
    }


    public InputStream openFile(String filename) throws IOException {
        InputStream file = null;
        File downloaded = new File(getFilesDir() + "/quotes.csv");
        //when offline
        if (!isOnline()) {
            if (downloaded.length() == 0) {
                file = MyActivity.this.getAssets().open(filename);
                System.out.println("I'm using the Asset File");
            } else {
                file = new FileInputStream(getFilesDir() + "/quotes.csv");
                System.out.println("I'm using the downloaded file");
            }
        }
        //when online
        else {
            file = new FileInputStream(getFilesDir() + "/quotes.csv");
            System.out.println("I'm using the downloaded file");
        }
        return file;
    }

    public List<String[]> processFile(InputStream st) {
        List<String[]> list = new ArrayList<String[]>();
        String next[] = {};
        try {
            InputStreamReader csvStreamReader = new InputStreamReader(st);
            CSVReader reader = new CSVReader(csvStreamReader, '|');
            next = reader.readNext();

            while (next != null) {
                list.add(lineno++, next);
                next = reader.readNext();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void findQuote(int quoterefmin, int quoterefmax) {
        int randomnumber = randInt(0, lineno);
        int quoteref = Integer.parseInt(quotes.get(randomnumber)[2]);

        if ((quoteref > quoterefmin) && (quoteref < quoterefmax) && (randomnumber != previousrandomnumber)) {
            setContentView(R.layout.displayquote_new);
            TextView quote = (TextView) findViewById(R.id.QuoteView);
            quote.setText(quotes.get(randomnumber)[0]);
            TextView author = (TextView) findViewById(R.id.AuthorView);
            author.setText(quotes.get(randomnumber)[1]);

            if (saved.contains(randomnumber)) {
                ImageView save = (ImageView) findViewById(R.id.savebutton);
                save.setColorFilter(Color.argb(255, 255, 165, 0));
            }

            previousrandomnumber = randomnumber;
        } else
            findQuote(quoterefmin, quoterefmax);
    }


    public void DisplayQuote(int quotetoprint) {

        setContentView(R.layout.displayquote_new);
        TextView quote = (TextView) findViewById(R.id.QuoteView);
        quote.setText(quotes.get(quotetoprint)[0]);
        TextView author = (TextView) findViewById(R.id.AuthorView);
        author.setText(quotes.get(quotetoprint)[1]);
        previousrandomnumber = quotetoprint;

        if (saved.contains(quotetoprint)) {
            ImageView save = (ImageView) findViewById(R.id.savebutton);
            save.setColorFilter(Color.argb(255, 255, 165, 0));
        }


    }


    public void buttonClickGeneral(View v) {
        switch ((String) v.getTag()) {
            //Cat : Happy
            case "happy":
                findQuote(0, 100);
                selector = 1;
                screentag = 1;
                break;
            // Cat : Afraid
            case "afraid":
                findQuote(100, 200);
                selector = 2;
                screentag = 1;
                break;
            //Cat : Sad
            case "sad":
                findQuote(200, 300);
                selector = 3;
                screentag = 1;
                break;
            //Cat  : Disgusted
            case "disgusted":
                findQuote(300, 400);
                selector = 4;
                screentag = 1;
                break;
            //Cat : Angry
            case "angry":
                findQuote(400, 500);
                selector = 5;
                screentag = 1;
                break;
            //Saved
            case "saved":
                setContentView(R.layout.displaysavedquote);
                screentag = 1;
                initsavedquote();
                break;
        }

    }

    public void saveButton(View v) throws IOException {
        ImageView save = (ImageView) findViewById(R.id.savebutton);
        if (!saved.contains(previousrandomnumber)) {
            saved.add(saved.size(), previousrandomnumber);
            save.setColorFilter(Color.argb(255, 255, 165, 0));
            Toast.makeText(getApplicationContext(), "Quote Saved",
                    Toast.LENGTH_LONG).show();
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFilesDir() + "/saved_quotes.txt", true));
            writer.write(String.valueOf(previousrandomnumber));
            writer.newLine();
            writer.close();
        } else {
            saved.remove((Object) previousrandomnumber);
            Toast.makeText(getApplicationContext(), "Quote Removed",
                    Toast.LENGTH_LONG).show();
            save.setColorFilter(null);
            BufferedWriter writer = new BufferedWriter(new FileWriter(getFilesDir() + "/saved_quotes.txt"));
            for (int number : saved) {
                writer.write(String.valueOf(number));
                writer.newLine();
            }
            writer.close();
        }
    }

    public void refreshButton(View v) {
        switch (selector) {
            case 1:
                findQuote(0, 100);
                break;
            case 2:
                findQuote(100, 200);
                break;
            case 3:
                findQuote(200, 300);
                break;
            case 4:
                findQuote(300, 400);
                break;
            case 5:
                findQuote(400, 500);
                break;
        }

    }

    public void backButton(View v) {
        setContentView(R.layout.layout);
        createGrid();
        screentag = 1;
    }


    public void initsavedquote() {
        TableLayout stk = (TableLayout) findViewById(R.id.tablesavedquotes);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" No. ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Quote ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        /*TextView tv2 = new TextView(this);
        tv2.setText(" Unit Price ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Stock Remaining ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);*/
        stk.addView(tbrow0);

        int lines = saved.size();

        for (int i = 0; i < lines; i++) {
            TableRow tbrow = new TableRow(this);
            tbrow0.setClickable(true);
            tbrow.setOnClickListener(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + saved.get(i));
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            //t2v.setText("" + quotes.get(Integer.parseInt(saved.get(i-1)))[0]);
            t2v.setText("" + (quotes.get(saved.get(i))[0]));
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.LEFT);
            tbrow.addView(t2v);
            /*TextView t3v = new TextView(this);
            t3v.setText("Rs." + i);
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText("" + i * 15 / 32 * 10);
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);*/
            stk.addView(tbrow);
        }
    }


    public int randInt(int min, int max) {
        Random random = new Random();
        int randomNum = random.nextInt((max - min)) + min;
        return randomNum;
    }

    // checks for internet connectivity by pinging Google's DNS
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public void onClick(View view) {
        TableRow tablerow = (TableRow) view;
        TextView sample = (TextView) tablerow.getChildAt(0);
        String result = sample.getText().toString();
        System.out.println("Row clicked: " + result);
        DisplayQuote(Integer.parseInt(result));
    }


    private class DownloadFiles extends AsyncTask<Void, Void, Void> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {

            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected Void doInBackground(Void... params) {
            FTPClient client = new FTPClient();
            OutputStream os = null;
            try {

                client.connect("ftp.ipage.com");
                client.login("exploringpsychecom1", "Tahir488$**");
                client.enterLocalPassiveMode();
                client.changeWorkingDirectory("/app");
                String[] file_list = (client.listNames());

                // Create an OutputStream for each file
                for (String filename : file_list) {
                    os = new BufferedOutputStream(new FileOutputStream(new File(getFilesDir(), filename)));
                    System.out.println("Get Files location is" + getFilesDir());
                    // Fetch file from server
                    client.retrieveFile(filename, os);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Do something that takes a long time, for example:
            return null;
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Void... Void) {

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(Void result) {

            // Do things like hide the progress bar or change a TextView
        }
    }

    protected void createGrid() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                switch (position) {
                    //Cat : Happy
                    case 1:
                        findQuote(0, 100);
                        selector = 1;
                        screentag = 1;
                        Toast.makeText(MyActivity.this, "Happy",
                                Toast.LENGTH_SHORT).show();
                        break;
                    // Cat : Afraid
                    case 2:
                        findQuote(100, 200);
                        selector = 2;
                        screentag = 1;
                        break;
                    //Cat : Sad
                    case 3:
                        findQuote(200, 300);
                        selector = 3;
                        screentag = 1;
                        break;
                    //Cat  : Disgusted
                    case 4:
                        findQuote(300, 400);
                        selector = 4;
                        screentag = 1;
                        break;
                    //Cat : Angry
                    case 5:
                        findQuote(400, 500);
                        selector = 5;
                        screentag = 1;
                        break;
                    //Saved
                    case 6:
                        setContentView(R.layout.displaysavedquote);
                        screentag = 1;
                        initsavedquote();
                        break;
                }

            }
        });
    }

    public void creatCustomGrid()
    {
        Bitmap happyIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.smiley1);
        Bitmap delightedIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.smiley2);
        Bitmap joyousIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.smiley3);

        gridArray.add(new Item(happyIcon,"Happy"));
        gridArray.add(new Item(delightedIcon,"Delighted"));
        gridArray.add(new Item(joyousIcon,"Joyous"));

        gridView = (GridView) findViewById(R.id.gridview);
        customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, gridArray);
        gridView.setAdapter(customGridAdapter);

    }

}

