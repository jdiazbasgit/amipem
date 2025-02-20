package amipem.telefonica.electroson.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import amipem.telefonica.electroson.clases.ParamRecibidos;
import amipem.telefonica.electroson.clases.TokenTelefonica;

@RestController
public class TelefonicaRestController {

	@PostMapping("tokenTelefonica")
	public String getTokenTelefonica (@RequestBody ParamRecibidos paramRecibidos) {
		return  obtenerToken(paramRecibidos.getRutaPfx(), paramRecibidos.getRutaUrl(), paramRecibidos.getClientId(), paramRecibidos.getClientSecret());
		
	}

	public String obtenerToken(String rutaPfx, String rutaUrl, String clientId, String clientSecret) {
		String salida=null;
		try {

			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			File file= new File("electroson.b2b.pro2.pfx");
			try (FileInputStream fis = new FileInputStream(file)) {

				keyStore.load(fis, "".toCharArray());
			} catch (Exception e) {
				
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
			}
			catch (Exception e) {
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
			salida=buffer.toString();

		} catch (Exception e) {
			
			return e.getMessage();
		}
		return salida;
		
	}
}
