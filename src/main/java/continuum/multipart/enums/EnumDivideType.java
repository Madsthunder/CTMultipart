package continuum.multipart.enums;

public enum EnumDivideType
{
	D1D2(0.5D),
	D1D4(0.25D),
	D1D8(0.125D);
	private final Double division;
	
	private EnumDivideType(Double division)
	{
		this.division = division;
	}
	
	public Double getDivision()
	{
		return this.division;
	}
}
