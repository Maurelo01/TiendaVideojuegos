package com.mycompany.tiendavideojuegos.DTO;

import java.io.Serializable;

public class ReporteRankingDTO implements Serializable
{
    private String nickname;
    private int cantidad;
    private float totalGastado;

    public ReporteRankingDTO() {}

    public ReporteRankingDTO(String nickname, int cantidad, float totalGastado)
    {
        this.nickname = nickname;
        this.cantidad = cantidad;
        this.totalGastado = totalGastado;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public int getCantidad()
    {
        return cantidad;
    }

    public void setCantidad(int cantidad)
    {
        this.cantidad = cantidad;
    }

    public float getTotalGastado()
    {
        return totalGastado;
    }

    public void setTotalGastado(float totalGastado)
    {
        this.totalGastado = totalGastado;
    }
}
