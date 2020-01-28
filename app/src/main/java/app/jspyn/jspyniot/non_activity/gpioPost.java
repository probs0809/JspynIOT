package app.jspyn.jspyniot.non_activity;

/**
 * Created by prabodhmayekar on 23/01/19.
 */

public class gpioPost {
    public int Relay_1, Relay_2, Relay_3, Relay_4, Var_1, Var_2;

    public gpioPost() {
    }

    public gpioPost(int relay_1, int relay_2, int relay_3, int relay_4, int var_1, int var_2) {
        Relay_1 = relay_1;
        Relay_2 = relay_2;
        Relay_3 = relay_3;
        Relay_4 = relay_4;
        Var_1 = var_1;
        Var_2 = var_2;
    }
}
