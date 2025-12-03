package com.adel.lifechoicebot;

public abstract class Evento {
    protected int id;
    protected String descrizione;
    protected Scelta sceltaA;
    protected Scelta sceltaB;

    public Evento(int id, String descrizione, Scelta sceltaA, Scelta sceltaB) {
        this.id = id;
        this.descrizione = descrizione;
        this.sceltaA = sceltaA;
        this.sceltaB = sceltaB;
    }

    public int getId(){
        return id;
    }
    public String getDescrizione(){
        return descrizione;
    }
    public Scelta getSceltaA(){
        return sceltaA;
    }
    public Scelta getSceltaB(){
        return sceltaB;
    }

    public abstract String getTipo();
}
