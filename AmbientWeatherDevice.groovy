metadata {
    definition(name: "Weather.gov Forecast Device", namespace: "davewagoner", author: "davewagoner") {
        capability "Temperature Measurement"
        capability "Refresh"
        capability "Sensor"
	capability "Actuator"
        
		//Current Conditions
        attribute "weather", "string"
        attribute "weatherIcon", "string"

    }
	preferences {
        section("Preferences") {
            input "showLogs", "bool", required: false, title: "Show Debug Logs?", defaultValue: false
        }
    }
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
	
	setWeather(weather);
}

def refresh() {
	fetchNewWeather(); 
}

def setWeather(weather){
	logger("debug", "Weather: "+weather);
	
	//Set temperature
	sendEvent(name: "temperature", value: weather.tempf, unit: '°F', isStateChange: true);
}

private logger(type, msg){
	 if(type && msg && settings?.showLogs) {
        log."${type}" "${msg}"
    }
}
	
