package com.adel.lifechoicebot;

public class EventoNormale extends Evento {
    public EventoNormale(int id, String descrizione, Scelta sceltaA, Scelta sceltaB) {
        super(id, descrizione, sceltaA, sceltaB);
    }

    @Override
    public String getTipo() {
        return "normale";
    }
}
