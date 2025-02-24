package amipem.telefonica.electroson.clases;

import lombok.Data;

@Data
public class Material {
	public String manufactureDate;

	public String manufactureBatch;

	public String packagingCode;

	public LogisticCodeUse logisticCodeUse;

	public Delivery delivery;

	public MaterialIdentification materialIdentification;

	public MaterialMassiveRequest materialMassiveRequest;

}
