package com.example.catolica.findhospital;

/**
 * Created by catolica on 09/10/16.
 */
public class Local {
    public double latitude;
    public double longitude;
    public String nome;
    public String endereco;

    public Local(String nome, String endereco, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nome = nome;
        this.endereco = endereco;
    }
}
