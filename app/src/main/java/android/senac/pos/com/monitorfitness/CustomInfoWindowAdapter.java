package android.senac.pos.com.monitorfitness;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ricardoogliari on 6/17/17.
 */

public class CustomInfoWindowAdapter implements
        GoogleMap.InfoWindowAdapter
{
    private final View mWindow;
    private HomeActivity act;

    CustomInfoWindowAdapter(HomeActivity act) {
        this.act = act;
        mWindow = act.getLayoutInflater().inflate(
                R.layout.custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        DadoExercicio dados = act.getDadoByMarker(marker);

        TextView txtDetalhes = (TextView)
                mWindow.findViewById(R.id.txtDetalhes);
        StringBuffer informacao = new StringBuffer();
        //String - imutavel
        if (dados.encerrou){
            informacao.append("Exercício Finalizado.\n");
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dados.fim);

            Date now = new Date(dados.fim);
            DateFormat.getInstance().format(now);

            informacao.append("Finalizado em: " +
                    DateFormat.getInstance().format(now)
                    + ".\n");
            long timeTotal = dados.fim - dados.inicio;
            informacao.append("Tempo Total: " + (timeTotal / 1000) + " segundos!");
        } else {
            informacao.append("Exercício em Andamento.\n");
            long timeTotal = System.currentTimeMillis() - dados.inicio;
            Date now = new Date(dados.inicio);

            informacao.append("Iniciado em: " +
                    DateFormat.getInstance().format(now)
                    + ".\n");
            informacao.append("Há " + (timeTotal / 1000) + " segundos!");
        }

        txtDetalhes.setText(informacao);

        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
