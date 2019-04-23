
public class Star {
	public String id;
	public String name;
	public int birthYear;
	
	
	public Star()
	{
	}
	
	public Star(String id, String name, int birthYear)
	{
		this.id = id;
		this.name = name;
		this.birthYear = birthYear;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getBirthYear()
	{
		return this.birthYear;
	}
	
	public void setId(String newId)
	{
		this.id = newId;
	}
	
	public void setName(String newName)
	{
		this.name = newName;
	}
	
	public void setBirthYear(int newYear)
	{
		this.birthYear = newYear;
	}
	
	public String toString()
	{
		String toString = this.id + "," + this.name + "," + this.birthYear + "\n";
		
		return toString;
	}
}
