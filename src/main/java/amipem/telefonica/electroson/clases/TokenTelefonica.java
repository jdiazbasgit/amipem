package amipem.telefonica.electroson.clases;

import lombok.Data;

@Data
public class TokenTelefonica {

	private String access_token, token_type, refresh_token, expires_in, scope, id_token;
}
