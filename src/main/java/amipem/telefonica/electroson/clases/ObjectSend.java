
package amipem.telefonica.electroson.clases;

import java.util.HashMap;

import lombok.Data;

@Data
public class ObjectSend {

	String rutaUrl;
	String metodo;
	Object objeto;
	HashMap<String, String> parametros;
	HashMap<String, String> cabeceras;
	String[] path;
}
