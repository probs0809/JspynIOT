package app.jspyn.jspyniot.non_activity;

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
