
public class StarsInMovie {
	public String starId;
	public String movieId;
	
	public StarsInMovie()
	{
	}
	
	public StarsInMovie(String starId, String movieId)
	{
		this.starId = starId;
		this.movieId = movieId;
	}
	
	public String getStarId()
	{
		return this.starId;
	}
	
	public String getMovieId()
	{
		return this.movieId;
	}
	
	public void setStarId(String newId)
	{
		this.starId = newId;
	}
	
	public void setMovieId(String newId)
	{
		this.movieId = newId;
	}
	
	public String toString()
	{
		String toString = starId + "," + movieId + "\n";
		
		return toString;
	}
}
