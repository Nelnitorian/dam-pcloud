package com.dam.pcloud.rest;

public interface Error {

    /**
     * Este método devuelve el código de error.
     * @return codigo de error
     * @see "https://docs.pcloud.com/errors/"
     */
    public int getCode();

    /**
     * Este método devuelve la descripción del error.
     * @return descripción del error
     * @see "https://docs.pcloud.com/<method>/"
     */
    public String getDescription();
}
