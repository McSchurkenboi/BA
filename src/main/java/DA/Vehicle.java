/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DA;

/**
 * might act as a "vehicle-ontology" later
 * @author rittfe1
 */
public enum Vehicle {
    vehicle,
    car,
    golf7,
    polo;

    public static boolean contains(String test) {
        for (Vehicle v : Vehicle.values()) {
            if (v.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}