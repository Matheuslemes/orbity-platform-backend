package br.com.catalog_plataform.ms_catalog_service_v1.domain.model;

public record Stock(int available) {

    public Stock {
        if (available < 0) available = 0;
    }

}
