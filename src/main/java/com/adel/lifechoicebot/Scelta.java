package com.adel.lifechoicebot;

public class Scelta {
    private String testo;
    private int salute;
    private int felicita;
    private int denaro;
    private int energia;
    private String risultato;

    public Scelta(String testo, int salute, int felicita, int denaro, int energia, String risultato) {
        this.testo = testo;
        this.salute = salute;
        this.felicita = felicita;
        this.denaro = denaro;
        this.energia = energia;
        this.risultato = risultato;
    }

    public String getTesto() {
        return testo;
    }

    public int getSalute() {
        return salute;
    }

    public int getFelicita() {
        return felicita;
    }
    public int getDenaro() {
        return denaro;
    }
    public int getEnergia() {
        return energia;
    }
    public String getRisultato() {
        return risultato;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public void setSalute(int salute) {
        this.salute = salute;
    }

    public void setFelicita(int felicita) {
        this.felicita = felicita;
    }
    public void setDenaro(int denaro) {
        this.denaro = denaro;
    }
    public void setEnergia(int energia) {
        this.energia = energia;
    }
    public void setRisultato(String risultato) {
        this.risultato = risultato;
    }
}
