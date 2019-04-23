pastResults = [];
getPreviousResults(pastResults);

function getPreviousResults(pastResults)
{
	if(localStorage.getItem("pastResults") === null)
	{
		console.log("No results exists. Making new array.");
		pastResults = [];
	}
	else
	{
		console.log("Loading past results");
		var cachedResults = JSON.parse(localStorage.getItem("pastResults"));
		
		for(i = 0; i < cachedResults.length; i++)
		{
			var key = cachedResults[i]['key'].toLowerCase();
			var value = cachedResults[i]['value'];
			var movieId = cachedResults[i]['id'];
			
			pastResults.push({key: key, id: movieId, value: value});
		}
		
		console.log(pastResults);
	}
}
//function to load cache

function loadCache()
{
	console.log("Cacheing past results.");
	localStorage.setItem("pastResults", JSON.stringify(pastResults));
	console.log(JSON.stringify(pastResults));
}

//function to load the pastResults in localStorage for it to persist across the pages

	

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated");
	
	// TODO: if you want to check past query results first, you can do it here
	//push values in query in ajaxSuccess function
	
	//check to see if the item has been searched before
	//if it has been searched before push values into a json Array
	
	//json Array stores to values movieIds and movieTitles
	
	
	//examople jsonStr
	/*
	 * [{"value":"A Scanner Darkly","data":{"movieId":"tt0405296"}},
	 * {"value":"Shot in the Dark","data":{"movieId":"tt0323965"}},
	 * {"value":"Alone in the Dark","data":{"movieId":"tt0369226"}},
	 * {"value":"In the Darkness of the Night","data":{"movieId":"tt0413053"}},
	 * {"value":"In the Dark","data":{"movieId":"tt0456069"}},
	 * {"value":"Terror in the Darkness","data":{"movieId":"tt0215244"}},
	 * {"value":"8:17 p.m. Darling Street","data":{"movieId":"tt0364193"}},
	 * {"value":"The Dark","data":{"movieId":"tt0411267"}},
	 * {"value":"Darkness","data":{"movieId":"tt0289146"}},
	 * {"value":"Darklovestory","data":{"movieId":"tt0359353"}}]
	 */
	
	/*
	 * [{"value":"Dark the dark knight","data":{"movieId":"tt0468569"}}{"value":"Shot in the Dark","data":{"movieId":"tt0323965"}}
	 * 
	 */
	
	var searchedBefore = false;
	var jsonStr = "[";
	
	for(i = 0; i < pastResults.length; i++)
	{
		if(pastResults[i]['key'] === query.toLowerCase())
		{
			searchedBefore = true;	
			jsonStr += "{\"value\":\"" + pastResults[i]["value"] + "\",\"data\":{\"movieId\":\"" + pastResults[i]["id"] + "\",\"category\":\"Movie\"}},";
		}
	}
	
	//get rid of comma at the end
	jsonStr = jsonStr.slice(0, -1);
	jsonStr += "]"
	
	//if searched before found results in object that have the same key as the query
	if(searchedBefore)
	{
		console.log("Found search result in cache");
		handleLookupCacheSuccess(jsonStr, query, doneCallback);
	}
	else
	{
		
		
		// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
		// with the query data
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "AutoComplete?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
	
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("Sent AJAX request to backend Java Servlet")
	console.log("lookup ajax successful");
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	// TODO: if you want to cache the result into a global variable you can do it here
	addJsonValuesToCache(jsonData, query);
	
	console.log(pastResults);

	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}

function addJsonValuesToCache(jsonData, query)
{
	//pastResults.push({key:'Dark Knight', id: 'tt0323965', value: 'Shot in the Dark'});
	for(var key in jsonData)
	{
		if(jsonData.hasOwnProperty(key))
		{
			var movieId = jsonData[key].data.movieId;
			var movieTitle = jsonData[key].value;
			
			pastResults.push({key: query.toLowerCase(), id: movieId, value: movieTitle});
		}		
	}
}


function handleLookupCacheSuccess(jsonStr, query, doneCallback)
{
	console.log("Using cached Results Successful");
	
	var jsonData = JSON.parse(jsonStr);
	
	console.log(jsonData);
	
	doneCallback({ suggestions: jsonData } );
}



/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"])
	var url = "SingleMovie" + "?movieId=" + suggestion["data"]["movieId"]
	console.log(url)
	
	//load pastResults array to access across multiple pages
	localStorage.setItem("pastResults", JSON.stringify(pastResults));
	
	window.location.href = 'SingleMovie?movieId=' + suggestion["data"]["movieId"];
}


/*
 * This statement binds the autocomplete library with the input box element and 
 *   sets necessary parameters of the library.
 * 
 * The library documentation can be find here: 
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 * 
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section

	lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    
    
    groupBy: "category",
   
    // set the groupby name in the response json data field
   // groupBy: "category",
    // set delay time
    minChars: 3,
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters

});


/*
 * do normal full text search if no suggestion is selected 
 */

 function handleNormalSearch(query) {
	
	console.log("doing normal search with query: " + query);
	
	//load pastResults cache
	//localStorage.setItem("pastResults", JSON.stringify(pastResults));
	loadCache();
	
	// TODO: you should do normal search here
	window.location.href = 'Search?itemSearch=' + escape(query);
}


// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button
