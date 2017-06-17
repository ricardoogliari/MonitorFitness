package android.senac.pos.com.monitorfitness;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.LinkedList;

public class HomeActivity extends FragmentActivity
        implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationClient;

    private final int MY_REQUEST_CODE = 10;

    private LocationCallback locationCallback;
    private Location lastLocation;

    private SensorManager sensorManager;

    private float maximumRange;

    private boolean parado = true;

    private Marker markerRunning;

    private Bitmap bmpYellow, bmpGreen;

    //tabela de espelhamento - hash
    //dica de estudo: caelum - estrutura de dados e Java
    private HashMap<String, DadoExercicio> hashPoiDados =
            new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //pega o mapa assincronamente. Nos temos que passar uma listener de callback
        mapFragment.getMapAsync(this);

        //recupera o cliente do LocationService, que acompanha as
        //mudanças de geolocalização do device
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    lastLocation = location;
                }
                //código que move a câmera do mapa para a nova location
                //moveCameraToNewPosition();
            };
        };

        sensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorAccelerometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        maximumRange = sensorAccelerometer.getMaximumRange();
        sensorManager.registerListener(
                this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
        );

        bmpYellow =
                BitmapFactory.decodeResource(
                        getResources(),
                        R.mipmap.ic_dumbbell_yellow
                );

        bmpGreen =
                BitmapFactory.decodeResource(
                        getResources(),
                        R.mipmap.ic_dumbbell_green
                );

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Se o usuário retirou a permissão pro ACCESS_FINE_LOCATION
            //nós requisitamos novamente;
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_REQUEST_CODE);
        } else {
            //se a permissão para o ACCESS_FINE_LOCATION ainda existe
            //chama o método que pega a última localização conhecida
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissão dada pelo usuário
                    getLastLocation();
                } else {
                    // permissão negada pelo usuário
                }
                return;
            }
        }
    }

    public void getLastLocation(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastLocation = location;
                        moveCameraToNewPosition();
                        startLocationUpdates();
                        // Got last known location. In some rare situations this can be null.
                    }
                });
    }

    public void moveCameraToNewPosition(){
        if (lastLocation != null) {
            //LatLng: classe que encapsula apenas latitude e longitude

            //atualização da câmera. A CameraUpdateFactory fabrica
            //instância desta classe, com seus métodos estáticos
            //temos várias opções de atualização da câmera

            LatLng latLng = new LatLng(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );
            CameraUpdate cameraUpdate =
                    CameraUpdateFactory.newLatLngZoom(latLng, 18);
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, //detalhes da requisição: tempo, custo, metros
                locationCallback,  //interface de retorno de mudança de posição
                null /* Looper  - é responsável pelo desenho da tela */);
    }

    /* DAQUI PRA BAIXO TEM OS CÓDIGOS DE TRATAMENTO DO SENSOR  */

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    //estrutura de dados
    //list - lista 0,1,2
    //lista ligada - muito performático em inserção e remoção nas pontas
    private LinkedList<Float> valoresSensor = new LinkedList<>();

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;

        //encontrar o valor máximo entre todos os eixos
        float max = Math.max(values[0], Math.max(values[1], values[2]));

        //guardo sempre os últimos trˆ´ valores lidos
        //nesse caso, o valor, é o máximo entre os 3 eixos
        if (valoresSensor.size() == 3){
            valoresSensor.removeFirst();
            valoresSensor.addLast(max);
        } else {
            valoresSensor.addLast(max);
        }

        //valoresSensor = armazena o valor máximo dos eixos
        if (valoresSensor.size() == 3) {
            float total = 0;
            for (Float valor : valoresSensor) {
                total += valor;
            }

            total /= 3;

            //traz o valor absoluto para não quebrar a lógica do if a seguir
            total = Math.abs(total);

            if (total >= maximumRange/10) {
                if (parado) {
                    Log.e("SENSOR", "mudou para movendo");
                    parado = false;

                    //POI = Point Of INterest
                    //ANDROID - MARKER <- markeroptions
                    MarkerOptions mOpt = new MarkerOptions();

                    mOpt.icon(
                            BitmapDescriptorFactory.fromBitmap(bmpYellow));
                    mOpt.title("Titulo");
                    mOpt.snippet("Subtitutlo");
                    mOpt.position(
                            new LatLng(
                                    lastLocation.getLatitude(),
                                    lastLocation.getLongitude())
                    );

                    DadoExercicio dados = new DadoExercicio();
                    dados.encerrou = false;
                    dados.inicio = System.currentTimeMillis();

                    //mOpt.draggable(true); deixa o marcador arrastável
                    //mOpt.anchor() muda o ponto de âncora do ícone do marcador
                    //mOpt.rotation(0-360); rotaciona o ícone do marcador
                    markerRunning = mMap.addMarker(mOpt);
                    hashPoiDados.put(markerRunning.getId(), dados);
                }
            } else {
                if (!parado) {
                    Log.e("SENSOR", "mudou para parado");
                    parado = true;

                    markerRunning.setIcon(
                            BitmapDescriptorFactory.fromBitmap(bmpGreen)
                    );
                    markerRunning.setPosition(
                            new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())
                    );

                    //passagem de parametro por referencia
                    DadoExercicio dadosExercicio =
                            hashPoiDados.get(markerRunning.getId());
                    dadosExercicio.encerrou = true;
                    dadosExercicio.fim = System.currentTimeMillis();
                }
            }
        }

    }

    public DadoExercicio getDadoByMarker(Marker marker){
        return hashPoiDados.get(marker.getId());
    }
}
