package com.adel.lifechoicebot;

public class Giocatore {
    private String nome;
    private int eta;
    private int felicita;
    private int denaro;
    private int salute;
    private int energia;

    public Giocatore(String nome, int eta, int felicita, int denaro, int salute, int energia) {
        this.nome = nome;
        this.eta = eta;
        this.felicita = 100;
        this.denaro = 1000;
        this.salute = 100;
        this.energia = 100;       
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public int getFelicita() {
        return felicita;
    }

    public void setFelicita(int felicita) {
        this.felicita = felicita;
    }

    public int getDenaro() {
        return denaro;
    }

    public void setDenaro(int denaro) {
        this.denaro = denaro;
    }

    public int getSalute() {
        return salute;
    }

    public void setSalute(int salute) {
        this.salute = salute;
    }

    public int getEnergia() {
        return energia;
    }
    
    public void setEnergia(int energia) {
        this.energia = energia;
    }
    
}
