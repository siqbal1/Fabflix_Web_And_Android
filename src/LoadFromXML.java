
public class LoadFromXML {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MovieXMLParser mxp = new MovieXMLParser();
		mxp.runParse();
		
		GenreXMLParser gxp = new GenreXMLParser();
		gxp.runParse();
		
		GenreInMoviesXMLParser gmxp = new GenreInMoviesXMLParser();
		gmxp.runParse();
		
		StarXMLParser sxp = new StarXMLParser();
		sxp.runParse();
	}

}
