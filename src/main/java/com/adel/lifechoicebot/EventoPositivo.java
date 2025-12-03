package com.adel.lifechoicebot;

public class EventoPositivo extends Evento {
    public EventoPositivo(int id, String descrizione, Scelta sceltaA, Scelta sceltaB) {
        super(id, descrizione, sceltaA, sceltaB);
    }

    @Override
    public String getTipo() {
        return "positivo";
    }
}
