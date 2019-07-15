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

def refresh() {
	parent.fetchNewWeather(); 
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
	
