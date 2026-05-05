package factory;

import parts.Accessory;
import parts.Body;
import parts.Motor;

public class Auto {

    private Motor motor;
    private Body body;
    private Accessory accessory;

    private int id;

    Auto(Motor motor, Body body, Accessory accessory, int id) {
        this.motor = motor;
        this.accessory = accessory;
        this.body = body;

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Accessory getAccessory() {
        return accessory;
    }

    public Body getBody() {
        return body;
    }

    public Motor getMotor() {
        return motor;
    }
}
