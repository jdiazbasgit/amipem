package amipem.telefonica.electroson;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Prueba {

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<>();
        map.put("nombre", "Juan");
        map.put("apellido", "Pérez");
        map.put("edad", "30");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(map);
            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }// TODO Auto-generated method stub

	}

}
