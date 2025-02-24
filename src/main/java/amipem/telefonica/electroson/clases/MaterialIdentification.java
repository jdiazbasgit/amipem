package amipem.telefonica.electroson.clases;

import lombok.Data;

@Data
public class MaterialIdentification {
	public String idiHeader;

	public String idiLogisticCode;

	public String idiSerialNumber;

	public String idiManufacturerCode;

	public String idiReservedCode;

	public String value;

}
