package app.jspyn.jspyniot;

import android.view.View;
import android.widget.AdapterView;

import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by prabodhmayekar on 30/12/18.
 */

public class DropDownClass implements MaterialSpinner.OnItemSelectedListener {
    public int position;
    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        this.position = position;
    }

}
