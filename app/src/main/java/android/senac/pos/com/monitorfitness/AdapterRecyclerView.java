package android.senac.pos.com.monitorfitness;

/**
 * Created by ricardoogliari on 6/17/17.
 */

import android.hardware.Sensor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ricardoogliari on 6/15/17.
 */

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {
    private List<Sensor> sensores;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtNome;

        public ViewHolder(View v) {
            super(v);
            txtNome = (TextView)v.findViewById(R.id.txtNomeSensor);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerView(List<Sensor> sensores) {
        this.sensores = sensores;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.txtNome.setText(sensores.get(position).getName());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sensores.size();
    }
}
