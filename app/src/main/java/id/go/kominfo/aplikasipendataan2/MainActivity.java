package id.go.kominfo.aplikasipendataan2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String FILENAME = "roman.txt";
    private ListView lvName;
    private final String lineSeparator = System.getProperty("line.separator");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvName = findViewById(R.id.lv_list);
        loadList(); //load data dan load list

        findViewById(R.id.fab).setOnClickListener(this::addTaskDialog);
    }

    private void addTaskDialog(View view) {
        //buat object View dan tempelkan layout R.layout.add_item_layout
        View subView = LayoutInflater.from(this).inflate(R.layout.add_item_layout, null);

        //rujuk object EditText ke view R.id.et_name yang ada di subView
        EditText etName = subView.findViewById(R.id.et_name);

        //tampilkan alert dialog
        new AlertDialog.Builder(this)
                //yang ditampilkan adalah subView didalam alert dialog
                .setView(subView)
                //jika menekan tombol "cancel"
                //tidak melakukan apa-apa
                .setNegativeButton("Cancel", null)
                //jika user mengklik tombol "Add" maka
                .setPositiveButton("Add", (dialog, which) -> {

                    //jika etName tidak kosong
                    if (!etName.getText().toString().isEmpty()) {

                        //buat object FileOutputStream yang merujuk ke FILENAME
                        //dengan mode tambah (append)
                        try (FileOutputStream fos = openFileOutput(FILENAME, MODE_APPEND)) {
                            //tulis ke FILENAME isi dari etName (dalam bytes)
                            fos.write(etName.getText().toString().getBytes());
                            //tambahkan karakter ganti baris dibagian baris (dalam bytes)
                            fos.write(lineSeparator.getBytes());
                            //commit penulisan
                            fos.flush();

                            //panggil method loadLlist()
                            loadList();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    private void loadList() {
        List<String> friends = getDataText(); //load data dari text berikan ke data

        //buat object adapter, yang menangani data pada listview
        //datanya adalah berupa list friends
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, friends);

        //set adapter untuk lvName
        lvName.setAdapter(adapter);
    }

    //membaca data text perbaris dari file
    private List<String> getDataText() {
        //buat object list untuk menampung data sebagai list
        List<String> result = new ArrayList<>();
        //buat object FileInputStream yang menunjuk ke file internal storage "FILENAME"
        if (new File(getFilesDir(), FILENAME).exists()) {
            try (FileInputStream fis = openFileInput(FILENAME)) {
                //isi file ditampung di object BufferedReader br.
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String line;
                //baca br perbaris, berikan ke line
                //lakukan perulangan untuk menguji apakah line tidak null
                //(artinya baris text ada)
                while ((line = br.readLine()) != null) {
                    result.add(line);   //jika line tidak null, tambahkan ke result.
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //kirim result sebagai object kembalian
        //dari method getDataText()
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //jika yang diklik adalah R.id.mi_hapus_data dan FILENAME berhasil dihapus
        if (item.getItemId() == R.id.mi_hapus_data && deleteFile(FILENAME)) {
            //maka jalankan method loadList()
            loadList();
            //tampilkan pesan
            Toast.makeText(this, "data telah terhapus",
                    Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

