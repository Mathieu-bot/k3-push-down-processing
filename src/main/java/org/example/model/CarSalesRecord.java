package org.example.model;

public class CarSalesRecord {

    private final String marque;
    private final String modele;
    private final int nbrePiecesVendus;

    public CarSalesRecord(String marque, String modele, int nbrePiecesVendus) {
        this.marque = marque;
        this.modele = modele;
        this.nbrePiecesVendus = nbrePiecesVendus;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public int getNbrePiecesVendus() {
        return nbrePiecesVendus;
    }

    @Override
    public String toString() {
        return marque + " | " + modele + " | " + nbrePiecesVendus;
    }
}
