package com.example.bar_retina_app;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private int number;
    private int clients;

    public Table(int number, int clients) {
        this.number = number;
        this.clients = clients;
    }

    public int getNumber() {
        return number;
    }

    public int getClients() {
        return clients;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }
}