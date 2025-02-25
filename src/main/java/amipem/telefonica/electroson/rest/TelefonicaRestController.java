package amipem.telefonica.electroson.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import amipem.telefonica.electroson.clases.ObjectSend;
import amipem.telefonica.electroson.clases.ParamRecibidos;
import amipem.telefonica.electroson.clases.TokenTelefonica;
import lombok.Data;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RestController
@Data
public class TelefonicaRestController {

	private String token;

	@PostMapping("tokenTelefonica")
	public Object getTokenTelefonica(@RequestBody ParamRecibidos paramRecibidos) throws ParseException {
		String entrada = obtenerToken(paramRecibidos.getRutaPfx(), paramRecibidos.getRutaUrl(),
				paramRecibidos.getClientId(), paramRecibidos.getClientSecret());
		@SuppressWarnings("deprecation")
		JSONParser jsonParser = new JSONParser();
		ObjectMapper mapper = new ObjectMapper();
		try {
			TokenTelefonica tokenTelefonica = mapper.readValue(entrada, TokenTelefonica.class);
			setToken(tokenTelefonica.getId_token());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return jsonParser.parse(entrada);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return jsonParser.parse(e.getMessage());
		}

	}

	@PostMapping("getRespuesta")
	public Object getRespuestaConJson(@RequestBody ObjectSend objetoSend) throws ParseException {

		String salida = null;
		@SuppressWarnings("deprecation")
		JSONParser jsonParser = new JSONParser();
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(objetoSend.getRutaUrl());
			if (objetoSend.getPath() != null || objetoSend.getPath().length != 0) {
				for (int i = 0; i < objetoSend.getPath().length; i++) {
					builder.append("/");
					builder.append(objetoSend.getPath()[i]);
				}
			}

			if (objetoSend.getParametros() != null) {
				builder.append("?");
				objetoSend.getParametros().keySet().forEach(p -> {
					builder.append("&" + p + "=" + objetoSend.getParametros().get(p));
				});
			}
			if (objetoSend.getMetodo().equals("GET")) {
				URL url = new URL(builder.toString());
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod(objetoSend.getMetodo());
				con.setRequestProperty("Content-Type", "application/json");

				con.setDoOutput(true);
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuffer buffer = new StringBuffer();
				while (br.ready()) {

					buffer.append(br.readLine());
				}
				salida = buffer.toString();
				return jsonParser.parse(salida);
			}
			if (objetoSend.getMetodo().equals("POST") && objetoSend.getObjeto() != null) {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				Optional<HashMap<String, String>> optionalCabeceras = Optional.ofNullable(objetoSend.getCabeceras());
				if (optionalCabeceras.isPresent()) {
					objetoSend.getCabeceras().keySet().forEach(c -> headers.add(c, objetoSend.getCabeceras().get(c)));
				}
				HttpEntity<Object> request = new HttpEntity<>(objetoSend.getObjeto(), headers);
				Object response = restTemplate.postForObject(builder.toString(), request, Object.class);
				System.out.println("Respuesta del servicio: " + response);
				return response;
			}
			if (objetoSend.getMetodo().equals("POST") && objetoSend.getObjeto() == null) {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				Optional<HashMap<String, String>> optionalCabeceras = Optional.ofNullable(objetoSend.getCabeceras());
				if (optionalCabeceras.isPresent()) {
					objetoSend.getCabeceras().keySet().forEach(c -> headers.add(c, objetoSend.getCabeceras().get(c)));
				}
				HttpEntity<Object> request = new HttpEntity<>(null, headers);
				Object response = restTemplate.postForObject(builder.toString(), request, Object.class);
				System.out.println("Respuesta del servicio: " + response);
				return response;
			}

		} catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
			return jsonParser.parse(e.getMessage());
		}
		return jsonParser.parse(salida);
	}

	public String obtenerToken(String rutaPfx, String rutaUrl, String clientId, String clientSecret) {
		String salida = null;
		try {

			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			File file = new File(rutaPfx);
			try (FileInputStream fis = new FileInputStream(file)) {

				keyStore.load(fis, "".toCharArray());
			} catch (Exception e) {
				e.printStackTrace();

				return e.getMessage();
			}

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);

			SSLContext sslContext = SSLContextBuilder.create().loadKeyMaterial(keyStore, "".toCharArray()).build();

			URL url = new URL(rutaUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(sslContext.getSocketFactory());
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);

			// Configurar los parámetros del cuerpo de la solicitud
			String params = "client_id=" + clientId + "&client_secret=" + clientSecret
					+ "&scope=openid&grant_type=CERT";

			// Escribir parámetros en la solicitud
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = params.getBytes("utf-8");
				os.write(input, 0, input.length);
			} catch (Exception e) {
				return e.getMessage();
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			while (br.ready()) {

				buffer.append(br.readLine());
			}
			salida = buffer.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return salida;

	}
}
