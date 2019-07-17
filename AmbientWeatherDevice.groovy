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
import groovy.json.JsonSlurper

def getWeather() throws groovyx.net.http.HttpResponseException {
    def data = [];
    
    requestData() { response ->
        data = response.data;
    };
    log.debug("daveweather: " + data);
	return data[0];
}

def requestData() {
    def apiUrl = "https://api.weather.gov/gridpoints/PQR/110,100/forecast";
    def card = new JsonSlurper().parse(apiUrl);
    log.debug("reqdataslurper: " + card);
    return card;
}

//loop
def fetchNewWeather() {
    def apiUrl = "https://api.weather.gov/gridpoints/PQR/110,100/forecast";
    def card = new JsonSlurper().parse(apiUrl.toURL());
    //def card = new JsonSlurper().parseText(apiUrl.text);
    def weatherMap = [:];
    
    def tempInt = card.properties.periods[0].temperature
    weatherMap.temperature = Integer.toString(tempInt);
    weatherMap.icon = card.properties.periods[0].icon;
    //log.debug("reqdataslurper: " + Integer.toString(tempInt));
    setWeather(weatherMap);
    return tempInt;
	
	
}

def refresh() {
	fetchNewWeather(); 
}

def setWeather(weather){
	//logger("debug", "Weather: "+weather);
    log.debug("setweather: " + weather.temperature);
	
	//Set temperature
	sendEvent(name: "temperature", value: weather.temperature, unit: '°F', isStateChange: true, displayed: true);
    sendEvent(name: "icon", value: weather.icon, isStateChange: true, displayed: true);
}

private logger(type, msg){
	 if(type && msg && settings?.showLogs) {
        log."${type}" "${msg}"
    }
}
	
