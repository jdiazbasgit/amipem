package amipem.telefonica.electroson.clases;

import java.io.Serializable;

import lombok.Data;

@Data

public class TokenTelefonica implements Serializable {

	private String access_token, token_type, refresh_token, expires_in, scope, id_token;
}
