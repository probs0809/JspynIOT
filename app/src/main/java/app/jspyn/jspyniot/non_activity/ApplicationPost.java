package app.jspyn.jspyniot;

/**
 * Created by prabodhmayekar on 31/12/18.
 */

public class ApplicationPost {
    public int Id,gpio;
    public String Name,Type,Pin;
    public float cord_x,cord_y;

    public ApplicationPost() {
    }



    public ApplicationPost(int Id, String Name, String Type, String Pin, float cord_x, float cord_y) {
        this.Id = Id;
        this.Pin = Pin;
        this.Name = Name;
        this.Type = Type;
        this.cord_x = cord_x;
        this.cord_y = cord_y;
        this.gpio = 0;
    }
}
