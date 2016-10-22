package com.example.catolica.findhospital;

/**
 * Created by catolica on 09/10/16.
 */
public class Local {
    public double latitude;
    public double longitude;
    public String nome;
    public String endereco;
    public String telefone;
    public String imagem;

    public Local(String nome, String endereco, String imagem, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nome = nome;
        this.endereco = endereco;
        this.imagem = imagem;
    }

    public Local(String nome, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.nome = nome;
    }
}
