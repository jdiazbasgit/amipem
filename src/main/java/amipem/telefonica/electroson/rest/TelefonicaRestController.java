package amipem.telefonica.electroson.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import amipem.telefonica.electroson.clases.ParamRecibidos;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

@RestController
public class TelefonicaRestController {

	@PostMapping("tokenTelefonica")
	public Object getTokenTelefonica(@RequestBody ParamRecibidos paramRecibidos) throws ParseException {
		String entrada = obtenerToken(paramRecibidos.getRutaPfx(), paramRecibidos.getRutaUrl(),
				paramRecibidos.getClientId(), paramRecibidos.getClientSecret());
		JSONParser jsonTexto = new JSONParser();
		try {
			return jsonTexto.parse(entrada);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return jsonTexto.parse(e.getMessage());
		}

	}

	@PostMapping("informacionAMandar")
	public Object getAlgo(String rutaUrl, String token, Object jsonData) {
		String salida = null;
		try {
			URL url = new URL(rutaUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Authorization", "Bearer " + token); // Agrega el token al header
			con.setRequestProperty("COCO.idProceso", "ELECTROSON");
			con.setRequestProperty("COCO.idOrigen", "ELECTROSON");
			// Enviar datos POST
			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = ((String) jsonData).getBytes("utf-8");
				os.write(input, 0, input.length);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new JSONObject();
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

			// Obtener la respuesta del servidor
			// int responseCode = conn.getResponseCode();
			System.out.println("Respuesta: " + conn);
			// Aquí puedes leer más de la respuesta si lo necesitas
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
