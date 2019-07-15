definition(name: "Weather.gov Temperature Forecast", namespace: "davewagoner", author: "David Wagoner", description: "Simple app to call weather.gov forecast API and return first day's forecast", iconUrl: "", iconX2Url: "");

preferences {

}

//lifecycle functions
def installed() {
    log.debug("Installed");
    
    addDevice();
    
    initialize();
    
    runEvery5Minutes(fetchNewWeather);
}

def updated() {
    log.debug("Updated");
    
    unsubscribe();
    unschedule();
    installed();
    initialize(); 
}

def initialize() {
    fetchNewWeather();
    
    //chron schedule, refreshInterval is int
    def m = refreshInterval;
    def h = Math.floor(m / 60);
    m -= h * 60;
    
    m = m == 0 ? "*" : "0/" + m.toInteger();
    h = h == 0 ? "*" : "0/" + h.toInteger();
    
    log.debug("Set CHRON schedule with m: $m and h: $h");
    
    schedule("0 $m $h * * ? *", fetchNewWeather);
}

//children

//fetch functions
def getStations() throws groovyx.net.http.HttpResponseException {
    def data = [];
    
    def params = [
        uri: "",
        path: "/v1/devices",
        query: [applicationKey: applicationKey, apiKey: apiKey]
    ];
    
    requestData("/v1/devices", [applicationKey: applicationKey, apiKey: apiKey]) { response ->
        data = response.data;
    };
        
    return data;
}

def getWeather() throws groovyx.net.http.HttpResponseException {
    def data = [];
    
    requestData("/v1/devices/$station", [applicationKey: applicationKey, apiKey: apiKey, limit: 1]) { response ->
        data = response.data;
    };
        
	return data[0];
}

def requestData(path, query, code) {
    def params = [
        uri: "https://api.weather.gov/gridpoints/PQR/110,100/forecast",
    ];
    
    httpGet(params) { response ->
        code(response);
    };
}

//loop
def fetchNewWeather() {
        
    def weather = getWeather();
    
    //log.debug("Weather: " + weather);
	
	childDevices[0].setWeather(weather);
}

