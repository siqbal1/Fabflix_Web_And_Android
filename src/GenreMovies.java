
public class GenreMovies {
	public int genreId;
	public String movieId;
	
	
	public GenreMovies()
	{
	}
	
	public GenreMovies(int genreId, String movieId)
	{
		this.genreId =  genreId;
		this.movieId = movieId;
	}
	
	public int getGenreId()
	{
		return genreId;
	}
	
	public String getMovieId()
	{
		return movieId;
	}
	
	public void setGenreId(int newId)
	{
		this.genreId = newId;
	}
	
	public void setMovieId(String newId)
	{
		this.movieId = newId;
	}
	
	public String toString()
	{
		String toString = genreId + "," + movieId + "\n";
		
		return toString;
	}
}
