package com.example.mac.yandexdemo;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.HttpsURLConnection;
import static com.example.mac.yandexdemo.R.array.lang;
import static com.example.mac.yandexdemo.R.id.spinner;
import static com.example.mac.yandexdemo.R.id.spinner2;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerLang, spinnerLang2;
    private  Button switchButton;
    private TextView textAfterTranslation;
    private EditText textToTranslate;
    private ImageButton wordsInHistory, list, add, reset;
    private DatabaseHelper myDB;
    private String [] langListArray;
    ImageButton fav;

    private TranslationTask translationTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Инициализируем наши элементы:
        switchButton = (Button) findViewById(R.id.button);
        textAfterTranslation = (TextView) findViewById(R.id.textView2);
        textToTranslate = (EditText) findViewById(R.id.editText2);
        add = (ImageButton) findViewById(R.id.add);
        list = (ImageButton) findViewById(R.id.list);
        wordsInHistory = (ImageButton) findViewById(R.id.imageButton);
        reset = (ImageButton) findViewById(R.id.reset);

        //подключаемся к бд
        myDB = new DatabaseHelper(this);
        textToTranslate.getText().toString();
        textAfterTranslation.setVisibility(View.GONE);
        translationTask = null;


        //Настраиваем для поля ввода слушателя изменений в тексте TextChangedListener:
        textToTranslate.addTextChangedListener(translatedWords);


       //переход на ListView на сохраненые слова
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reasonIntent = getIntent();

                int pos = reasonIntent.getIntExtra("position", -1);
                Intent intent = new Intent(MainActivity.this, ListContent.class);
                intent.putExtra("position", pos);
                startActivity(intent);
            }
        });

        //специальная кнопка добавления слов в историю, так как не всегда хочется сохранять все слова
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = textToTranslate.getText().toString();
                if (textToTranslate.length() != 0) {
                    AddData(newEntry);
                    textToTranslate.setText(" ");
                } else {
                    Toast.makeText(MainActivity.this, "You must put something in the text field!", Toast.LENGTH_LONG).show();
                }
            }
        });
        //кнопка сброса на главную страницу
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == reset) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            }

        });

    }

    //Создаем экземпляр TextWatcher:
    private final TextWatcher translatedWords = new TextWatcher() {
      //подключаем таймер чтоб сервак при запросах не загнулся, и запрашивал через определенный промежуток времени
        private Timer timer = new Timer();
        private final int DELAY = 3000; //milliseconds of delay for timer

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textAfterTranslation.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(final Editable s) {
            //Если длина введенного текста =0, то видимость TextView исчезает:
            if (s.length() == 0) {
                textAfterTranslation.setVisibility(View.GONE);
            } else {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {

//
                                       if (translationTask == null) {
                                           translationTask = new TranslationTask();
                                           //по умолчанию настроила(или все как на скрине)язык с русского на англиский, но если вставить вместо абривиатур lang можно будет выбрать любой язык
                                           translationTask.execute("en-ru", s.toString());
                                       }
                                   }
                               },
                        DELAY
                );

            }
        }

      };
    //метод инсерта в бд/точнее добавление слов в нашу таблицу
    public void AddData(String newEntry) {
        boolean insertData = myDB.addData(newEntry);

        if(insertData){
            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }

    //спинеры на menu actionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.android_action_bar_spinner_menu, menu);
        MenuItem item = menu.findItem(spinner);
        MenuItem item1 = menu.findItem(spinner2);
        spinnerLang = (Spinner) MenuItemCompat.getActionView(item);
        spinnerLang2 = (Spinner) MenuItemCompat.getActionView(item1);
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter.createFromResource(this, lang, android.R.layout.simple_spinner_dropdown_item));
        ArrayAdapter<CharSequence> adapter2 = (ArrayAdapter.createFromResource(this, lang, android.R.layout.simple_spinner_dropdown_item));
        //spinnerLang.setSelection(adapter.getPosition("Russian"));
        //spinnerLang2.setSelection(adapter2.getPosition("English"));
        spinnerLang.setAdapter(adapter);
        spinnerLang2.setAdapter(adapter2);
        return super.onCreateOptionsMenu(menu);
    }
    //кнопочка switch между двумя спинерами
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button1:
                int spinner1Index = spinnerLang.getSelectedItemPosition();
                spinnerLang.setSelection(spinnerLang2.getSelectedItemPosition());
                spinnerLang2.setSelection(spinner1Index);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //асинхронный class translate здесь и весь главный экшн происходит
    class TranslationTask extends AsyncTask<String, Void, String> {

        @Override
        //обращение и получение API-yandex
        protected String doInBackground(String... params) {
            String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20170324T114830Z.e185bd72aa281fac.c224a6b064b0f73d6fb23c95adc34bbf265d9f5f";
           //первый параметр запроса language
            langListArray = getResources().getStringArray(R.array.lang);
            String language = langListArray.toString();
            language = params[0];

            //второй текст который мы хотим перевести
            String textToTranslate = params[1];

            InputStream response = null;
            String responseText = "";
            try {
                //создаем url
                URL urlObj = new URL(urlStr);
                //http запрос
                HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //фильтрация, ну или фильтровой класс принимающий или отсылающий данные простым потокам
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes("text=" + URLEncoder.encode(textToTranslate, "UTF-8") + "&lang=" + language);
                //запрос на конекшн
                response = connection.getInputStream();
               //считывает текст из символьного потока ввода, буферизируя прочитанные символы
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));
                //принимает на вход строку, с которой можно проводить дальнейшие манипуляции.
                StringBuilder stringBuilder = new StringBuilder();
                String nextLine;
                //далее проверки на ошибочки
                while ((nextLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(nextLine);
                }
                responseText = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return responseText;
        }

        @Override
        protected void onPostExecute(String responseText) {
            try {
                //форматируем результат в Json
                JSONObject result = new JSONObject(responseText);
                JSONArray translatedTexts = result.getJSONArray("text");
                if (translatedTexts.length() > 0) {
                    String translatedText = translatedTexts.getString(0);
                    textAfterTranslation.setText(translatedText);
                    translationTask = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}





















