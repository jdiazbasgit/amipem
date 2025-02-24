package amipem.telefonica.electroson.clases;

import java.util.List;

import lombok.Data;

@Data
public class InfoMovistar {
	public List<Material> materiales;

	public List<Material> getMateriales() {
		return materiales;
	}

	public void setMateriales(List<Material> materiales) {
		this.materiales = materiales;
	}

}
