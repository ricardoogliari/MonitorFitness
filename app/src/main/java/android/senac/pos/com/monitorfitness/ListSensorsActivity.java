package android.senac.pos.com.monitorfitness;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ListSensorsActivity extends AppCompatActivity implements SensorEventListener{

    private RecyclerView listView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Sensor> sensors;

    private SensorManager sensorManager;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensors);

        //recupero um serviço do sistema operacional
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //recupera a lista de todos sensores. O TYPE_ALL pode ser substituído
        //por um tipo específico de sensor, por exemplo, TYPE_LIGHT
        sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        //link sobre o treinamento de RecyclerView do android: https://developer.android.com/training/material/lists-cards.html?hl=pt-br
        listView = (RecyclerView) findViewById(R.id.listView);

        listView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        mAdapter = new AdapterRecyclerView(sensors);
        listView.setAdapter(mAdapter);

        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ALTAMENTE indicado parar de ouvir qualquer sensor no momento que o ciclo de vida
        //da tela se encerra
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //quando um novo dado é lido
        //índices 0, 1 e 2. Depende do sensor lido para saber quais dados são úteis.
        //Por exemplo: sensor de luz, somente índice 0. Sensor de aceleração, os três índices (x, y, z)
        float[] leitura = sensorEvent.values;

        Log.e("MONITORFITNESS", "Luz: " + leitura[0]);

        if (leitura[0] < 15){
            //toca o som do grilo
            tocaMidia();
        } else {
            //para o som do grilo
            paraMidia();
        }
    }

    public void paraMidia(){
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    public void tocaMidia(){
        if (mp != null && mp.isPlaying()) {
            return;
        }
        mp = MediaPlayer.create(this, R.raw.grilo);
        mp.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /* Não é mais usada. Foi criada para o ListView. No RecyclerView usamos uma classe separada.*/
    class MeuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return sensors.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {
            View v = LayoutInflater.from(ListSensorsActivity.this)
                    .inflate(R.layout.item_sensor, null);
            ((TextView)v.findViewById(R.id.txtNomeSensor)).setText(sensors.get(pos).getName());
            return v;
        }
    }
}
