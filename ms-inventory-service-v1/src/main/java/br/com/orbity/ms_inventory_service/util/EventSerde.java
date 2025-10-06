package br.com.orbity.ms_inventory_service.util;

import br.com.orbity.ms_inventory_service.domain.event.StockAdjusted;
import br.com.orbity.ms_inventory_service.domain.event.StockDecremented;
import br.com.orbity.ms_inventory_service.domain.event.StockReleased;
import br.com.orbity.ms_inventory_service.domain.event.StockReserved;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventSerde {

    public static Object deserialize(String type, String json, ObjectMapper om) {

        try {
            return switch (type) {
                case "StockAdjusted"   -> om.readValue(json, StockAdjusted.class);
                case "StockDecremented"-> om.readValue(json, StockDecremented.class);
                case "StockReserved"   -> om.readValue(json, StockReserved.class);
                case "StockReleased"   -> om.readValue(json, StockReleased.class);
                default -> throw new IllegalArgumentException("Tipo de evento desconhecido: " + type);
            };
        } catch (Exception e) {
            throw new IllegalArgumentException("Falha ao desserializar evento type=" + type, e);
        }

    }

}