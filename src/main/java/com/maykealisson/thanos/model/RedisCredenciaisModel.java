package com.maykealisson.thanos.model;

import lombok.Getter;

@Getter
public class RedisCredenciaisModel {

    public String url;
    public Integer porta;
    public String senha;
    public Boolean ssl;
    public Boolean abortConexao;

    public RedisCredenciaisModel(String connectString) {
        var parts = connectString.split(",");
        var host = parts[0];
        var hostParts = host.split(":");
        this.url = hostParts[0];
        this.porta = Integer.parseInt(hostParts[1]);
        this.senha = parts[1].replace("password=","");
        this.ssl = parts[2].replace("ssl=","").equals("True");
        this.abortConexao = parts[3].replace("abortConexao=","").equals("True");
    }
}
