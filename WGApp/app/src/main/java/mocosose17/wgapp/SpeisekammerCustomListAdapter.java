package mocosose17.wgapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Sebastian on 18.06.2017.
 */

public class SpeisekammerCustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] amount;

    public SpeisekammerCustomListAdapter(Activity context, String[] itemname, String[] amount){

        super(context, R.layout.speisekammerlistelement, itemname);

        this.context = context;
        this.itemname = itemname;
        this.amount = amount;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.speisekammerlistelement, parent,false);

        TextView itemView = (TextView) rowView.findViewById(R.id.speisekammerItem);
        TextView amountView = (TextView) rowView.findViewById(R.id.speisekammerAmount);

        itemView.setText(itemname[position]);
        amountView.setText(amount[position]);

        Button remove = (Button)rowView.findViewById(R.id.speisekammerRemoveButton);
        remove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                String s = listView.getItemAtPosition(position).toString();
                ((SpeisekammerActivityList)context).delete(s);
            }
        });

        return rowView;

    }

}
