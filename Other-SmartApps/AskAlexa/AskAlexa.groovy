/**
 *  Ask Alexa 
 *
 *  Version 1.0.2b - 5/15/16 Copyright © 2016 Michael Struck
 *  Special thanks for Keith DeLong for code and assistance
 *  
 *  Version 1.0.0 - Initial release
 *  Version 1.0.0a - Same day release. Bugs fixed: nulls in the device label now trapped and ensure LIST_OF_PARAMS and LIST_OF_REPORTS is always created
 *  Version 1.0.0b - Remove punctuation from the device, mode and routine names. Fixed bug where numbers were removed in modes and routine names 
 *  Version 1.0.1c - Added presense sensors; added up/down/lower/increase/decrease as commands for various devices
 *  Version 1.0.2b - Added motion sensors and a new function, "events" to list to the last events for a device; code optimization, bugs removed
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Ask Alexa",
    namespace: "MichaelStruck",
    singleInstance: true,
    author: "Michael Struck",
    description: "Provide interfacing to control and report on SmartThings devices with the Amazon Echo ('Alexa').",
    category: "My Apps",
    iconUrl: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/smartapps/michaelstruck/ask-alexa.src/AskAlexa.png",
    iconX2Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/smartapps/michaelstruck/ask-alexa.src/AskAlexa@2x.png",
    iconX3Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/smartapps/michaelstruck/ask-alexa.src/AskAlexa@2x.png",
  	oauth: true)
preferences {
    page name:"mainPage"
    page name:"pageSwitches"
    page name:"pageReset"
    page name:"pageAbout"
    page name:"pageDoors"
    page name:"pageTemps"
    page name:"pageSettings"
    page name:"pageSpeakers"
    page name:"pageReports"
    page name:"pageSensors"
    page name:"pageHomeControl"
    page name:"pageConfirmation"
}
//Show main page
def mainPage() {
    dynamicPage(name: "mainPage", install: true, uninstall: false) {
        if (!state.deviceTypes) fillTypeList()
        section("Items to interface to Alexa") {
        	href "pageSwitches", title: "Switches/Dimmers/Colored Lights", description: getDesc(switches, dimmers ,cLights), state: getState(switches, dimmers, cLights),
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/power.png"
            href "pageDoors", title: "Doors/Windows/Locks", description: getDesc(doors, locks, ocSensors), state: getState(doors, locks, ocSensors),
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/lock.png"
            href "pageTemps", title: "Thermostats/Temperature/Humidity", description:getDesc(tstats, temps, humid), state: getState (tstats, temps, humid),
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/temp.png"
            href "pageSpeakers", title: "Connected Speakers", description: getDesc(speakers, "", ""), state: getState(speakers, "", ""),
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/speaker.png"     
            href "pageSensors", title: "Other Sensors", description:getDesc(water, presence, motion), state: getState (water, presence, motion),
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/sensor.png"
            href "pageHomeControl", title: "Modes/Routines/SHM", description: getDesc(modes, routines, SHM), state: getState(modes, routines, SHM),
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/modes.png"        
        }
        section("Configure voice reports"){
        	href "pageReports", title: "Voice Reports", description: reportDesc(), state: reportGrey(), image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/speak.png"
        }
        section("Options") {
			href "pageSettings", title: "Settings", description: "Tap to configure app settings, get setup information or to reset the access token",
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/img/settings.png"
			href "pageAbout", title: "About ${textAppName()}", description: "Tap to get version information, license, instructions or to remove the application",
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/info.png"
        }
	}
}
def pageSwitches(){
    dynamicPage(name: "pageSwitches", install: false, uninstall: false) {
        section { paragraph "Switches/Dimmers/Colored Lights", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/power.png"} 
        section("Choose the devices to interface") {
            input "switches", "capability.switch", title: "Choose Switches (On/Off/Status)", multiple: true, required: false
            input "dimmers", "capability.switchLevel", title: "Choose Dimmers (On/Off/Level/Status)", multiple: true, required: false
            input "cLights", "capability.colorControl", title: "Choose Colored Lights (On/Off/Level/Color/Status)", multiple: true, required: false, submitOnChange: true
        }
        if (cLights) {
            section("Custom color"){
                input "customName", "text", title: "Custom Color Name", required: false
                input "customHue", "number", title: "Custom Color Hue", required: false
                input "customSat", "number", title: "Custom Color Saturation", required: false
            }
        }
	}
}
def pageDoors() {
    dynamicPage(name: "pageDoors", install: false, uninstall: false) {
        section { paragraph "Doors/Windows/Locks", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/lock.png" }
        section("Choose the devices to interface") {
            input "doors", "capability.doorControl", title: "Choose Door Controls (Open/Close/Status)" , multiple: true, required: false
            input "ocSensors", "capability.contactSensor", title: "Open/Close Sensors (Status)", multiple: true, required: false
            input "locks", "capability.lock", title: "Choose Locks (Lock/Unlock/Status)", multiple: true, required: false  
        }
    }
}
def pageTemps(){
    dynamicPage(name: "pageTemps",  install: false, uninstall: false) {
        section {paragraph "Thermostats/Temperature/Humidity", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/temp.png"}
        section("Choose the devices to interface") {
            input "tstats", "capability.thermostat", title: "Choose Thermostats (Temperature Setpoint/Status)", multiple: true, required: false 
            input "temps", "capability.temperatureMeasurement", title: "Choose Temperature Devices (Status)", multiple: true, required: false
        	input "humid", "capability.relativeHumidityMeasurement", title: "Choose Humidity Devices (Status)", multiple: true, required: false
        }
    }
}
def pageSpeakers(){
    dynamicPage(name: "pageSpeakers",  install: false, uninstall: false) {
        section {paragraph "Connected Speakers", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/speaker.png"}
        section("Choose the devices to interface") {
            input "speakers", "capability.musicPlayer", title: "Choose Speakers (Speaker Control, Status)", multiple: true, required: false 
        }
        section("Speaker Control"){
        	paragraph "Speaker control consists of the following:\nPlay, Stop, Pause\nMute, Un-mute\nNext Track, Previous Track\nLevel (Volume)"
		}
    }
}
def pageSensors(){
    dynamicPage(name: "pageSensors",  install: false, uninstall: false) {
        section {paragraph "Other Sensors", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/sensor.png"}
        section("Choose the devices to interface") {
            input "water", "capability.waterSensor", title: "Choose Water Sensors (Status)", multiple: true, required: false
            input "presence", "capability.presenceSensor", title: "Choose Presence Sensors (Status)", multiple: true, required: false
            input "motion", "capability.motionSensor", title: "Choose Motion Sensors (Status)", multiple: true, required: false
        }
    }
}
def pageHomeControl(){
	dynamicPage(name: "pageHomeControl", uninstall: false) {
        section { paragraph "Modes/Routines/SHM", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/modes.png" }
        section ("Choose the features to have voice control over") {
        	input "modes", "bool", title: "Modes (Change/Status)", defaultValue: false
            input "SHM", "bool", title: "Smart Home Monitor (Change/Status)", defaultValue: false
            input "routines", "bool", title: "Routines (Execute)", defaultValue: false
		}
    }
}
def pageReports() {
    dynamicPage(name: "pageReports", install: false, uninstall: false) {
    	if (childApps.size() && childApps[0].versionInt() <102){
        	section{
            	paragraph "You report (child app) version is less than the recommended version. Be sure to keep up-to-date on the latest versions "+
                	"of both the parent and the child app. You cannot continue with creating reports until you upgrade.", 
                    image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/warning.png"
            }
        }
        else{
            section{ paragraph "Voice Reports", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/speak.png" }
            if (childApps.size()) section("Reports available"){}
            section(" "){
                app(name: "childReports", appName: "Ask Alexa - Report", namespace: "MichaelStruck", title: "Create New Voice Report...", description: "Tap to create new voice report", multiple: true, 
                        image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/add.png")
            }
            section ("Please note") {
                paragraph "Be sure each report has a unique name."
            }
		}
    }
}
def pageAbout(){
	dynamicPage(name: "pageAbout", uninstall: true) {
        section {paragraph "${textAppName()}\n${textCopyright()}",
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/smartapps/michaelstruck/ask-alexa.src/AskAlexa@2x.png"}
        section ("SmartApp / Voice report version") { paragraph "${textVersion()}" } 
        section ("Access token / Application ID"){
            if (!state.accessToken) OAuthToken()
            def msg = state.accessToken != null ? state.accessToken : "Could not create Access Token. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
            paragraph "Access Token:\n${msg}\n\nApplication ID:\n${app.id}"
    	}
        section ("Apache license"){ paragraph textLicense() }
    	section("Instructions") { paragraph textHelp() }
        section("Tap below to remove the application and all reports"){}
	}
}
def pageSettings(){
    dynamicPage(name: "pageSettings", title: none, uninstall: false){
        section { paragraph "Settings", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/img/settings.png" }
        section ("Additional voice settings"){ 
        	input "otherStatus", "bool", title: "Speak Addition Status Attributes Of Devices", defaultValue: false, submitOnChange: true
            input "batteryWarn", "bool", title: "Speak Battery Level When Below Threshold", defaultValue: false, submitOnChange: true
            if (batteryWarn) input "batteryThres", "enum", title: "Battery Status Threshold", required: false, defaultValue: 20, options: [5:"<5%",10:"<10%",20:"<20%",30:"<30%",40:"<40%",50:"<50%",60:"<60%",70:"<70%",80:"<80%",90:"<90%",101:"Always play battery level"]
			if (doors || locks) input "pwNeeded", "bool", title: "Password (PIN) Required For Lock Or Door Commands", defaultValue: false, submitOnChange: true
            if ((doors || locks) && pwNeeded) input "password", "num", title: "Numeric Password (PIN)", description: "Enter a short numeric PIN (i.e. 1234)", required: false
       		input "eventCt", "enum", title: "Default Number Of Past Events to Report", options: [[1:"1"],[2:"2"],[3:"3"],[4:"4"],[5:"5"],[6:"6"],[7:"7"],[8:"8"],[9:"9"]], required: false, defaultValue: 1
        }        	
		if (dimmers || tstats || cLights || speakers){
        	section("Increase / Decrease default value (When no values are requested)"){
            	if (dimmers || cLights) input "lightAmt", "number", title: "Dimmer/Colored Lights", defaultValue: 20, required: false
        		if (tstats) input "tstatAmt", "number", title: "Thermostat Temperature", defaultValue: 5, required: false
            	if (speakers) input "speakerAmt", "number", title: "Speaker Volume", defaultValue: 5, required: false
        	}
        }	
        section ("Setup variables (For Amazon Lambda / Developer site)"){
        	if (!state.accessToken) OAuthToken()
            href url:"${getApiServerUrl()}/api/smartapps/installations/${app.id}/setup?access_token=${state.accessToken}", style:"embedded", required:false, title:"Show Setup Information", description: none
        }
        section ("Advanced") { 
        	href "pageConfirmation", title: "Revoke/Reset Access Token", description: "Tap to confirm this action",
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/warning.png"
        }
    }
}
def pageConfirmation(){
	dynamicPage(name: "pageConfirmation", title: "Revoke/Reset Access Token Confirmation", uninstall: false){
        section {
		href "pageReset", title: "Revoke / Reset Access Token", description: "Tap to take action - READ WARNING BELOW", 
            	image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/warning.png"
        	    paragraph "PLEASE CONFIRM! By resetting the access token you will disable the ability to interface this SmartApp with "+        	
        		"your Amazon Echo. You will need to copy the new access token to your Amazon Lambda code to re-enable access." +
                "Tap below to go back to the main menu with out resetting the token. You may also tap Done in the upper left corner."
        }
        section(" "){
        	href "mainPage", title: "Cancel And Go Back To Main Menu", description: " "
        }
	}
}
def pageReset(){
	dynamicPage(name: "pageReset", title: "Access Token Reset", uninstall: false){
        section{
			revokeAccessToken()
            state.accessToken = null
            OAuthToken()
            def msg = state.accessToken != null ? "New access token:\n${state.accessToken}\n\nClick 'Done' above to return to the previous menu." : "Could not reset Access Token. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth."
	    	paragraph "${msg}"
            href "mainPage", title: "Tap Here To Return To The Main Menu", description: " "
		}
	}
}
//---------------------------------------------------------------
def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}
def updated() {
	log.debug "Updated with settings: ${settings}"
	initialize()
}
def initialize() {
	if (!state.accessToken) {
		log.error "Access token not defined. Ensure OAuth is enabled in the SmartThings IDE."
	}
    fillColorSettings()
    fillTypeList()
}
mappings {
      path("/d") { action: [GET: "processDevice"] }
      path("/r") { action: [GET: "processReport"] }
      path("/h") { action: [GET: "processSmartHome"] }
      path("/l") { action: [GET: "processList"] }
      path("/v") { action: [GET: "processVersion"] }
      path("/setup") { action: [GET: "displayData"] }
}
//--------------------------------------------------------------
def processDevice() {    
    log.debug "Device command received with params $params"
	def dev = params.Device 	//Label of device
	def op = params.Operator	//Operation to perform
    def numVal = params.Num     //Number for dimmer/PIN type settings
    def param = params.Param	//Other parameter (color)
    def lVer = params.lVer		//Version number of Lambda code
    if (numVal == "undefined" || numVal == "?" ) numVal = 0 
    def num = numVal as int
    log.debug "Dev: " + dev
    log.debug "Op: " + op
    log.debug "Num: " + num
    log.debug "Param: " + param
    log.debug "Lambda Ver: " + lVer
    def outputTxt = "", deviceList, count = 0
    getDeviceList().each{if (it.name==dev.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase()) {deviceList=it; count++}}
	if (count > 1) outputTxt ="You have multiple devices that are named '${dev}' in your SmartThings SmartApp. Please rename these items to something unique so I may properly utlize them. "   
    else if (deviceList) {
        if (op == "status" || (op=="undefined" && param=="undefined" && num==0)) outputTxt = getReply (deviceList.devices,deviceList.type, dev.toLowerCase(), "status", "", "") 
		else if (op == "events" || op == "event") {
        	def finalCount = num != 0 ? num as int : eventCt as int
            if (finalCount>0 && finalCount < 10) outputTxt = getLastEvent(deviceList.devices?.find{it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase() == dev.toLowerCase()}, finalCount)
            else if (finalCount == 0) outputTxt = "You do not have the number of events you wish to hear specified in your Ask Alexa SmartApp, and you didn't specify a number in your request. Please try again. "
        	else if (finalCount > 9) outputTxt = "The maximum number of events to list is 9. Please try again with a lower number. "
        }
        else outputTxt = getReply (deviceList.devices,deviceList.type, dev.toLowerCase(), op, num, param)
    }
    if (outputTxt=="" && op != "report") outputTxt = "I had some problems finding the device you specified. Please try again." 
    sendJSON(outputTxt, lVer)
}
//List Request
def processList(){
	log.debug "List command received with params $params"
	def listType = params.Type	//Help type
    def lVer = params.lVer		//Version number of Lambda code
    log.debug "List Type: " + listType
    log.debug "Lambda Ver: " + lVer
    def outputTxt = ""
	if (listType=="mode" || listType=="modes" ){
		outputTxt = location.modes.size() >1 ? "The available modes include the following: " + getList(location.modes) : location.modes.size()==1 ? "You have one mode available named " + getList(location.modes)  : "There are no modes defined within your SmartThings' account. "
	}
    if (listType=="security" || listType=="smart home monitor" || listType=="SHM") outputTxt = "The valid Smart Home Monitor commands are: 'disarm', 'away' or 'stay'. "
    if (listType=="temperature" || listType=="temperature sensors"){
    	if (temps) outputTxt = "The devices you can get temperature readings from include the following: " + getList(temps) + ". "
        if (tstats && temps) outputTxt +="In addition, the following thermostats can also give you temperature readings: " +  getList(tstats)
        if (tstats && tstats.size()>1 && !temps) outputTxt ="The only devices you have selected for temperature readings are the following thermostats: " +  getList(tstats)
        if (tstats && tstats.size()==1 && !temps) outputTxt ="The only device you have selected for temperature readings is the " +  getList(tstats)
        if (!tstats && !temps) outputTxt="You don't have any devices selected that will provide temperature readings. "
    }
    if (listType=="thermostats" || listType=="thermostat"){
     	outputTxt = tstats && tstats.size()>1 ? "The available thermostats are as follows: " +  getList(tstats) 
        	: tstats && tstats.size()==1 ? "The only available thermostat is the " +  getList(tstats) 
        		: "You don't have any thermostats defined within your Ask Alexa SmartApp. "
    }
    if (listType=="humidity"){
     	outputTxt = humid && humid.size()>1 ? "The available humidity sensors include the following: " +  getList(humid)  
        	: humid && humid.size()==1 ? "The "+ getList(humid)+ " is the only humidity sensor available. "    
       			: "You don't have any humidity sensors selected in your Ask Alexa SmartApp. "
    }
    if (listType=="presence" || listType=="presence sensor" || listType=="presence sensors"){
     	outputTxt = presence && presence.size()>1 ? "The available presence sensors are: " +  getList(presence)  
        	: presence && presence.size()==1 ? "The "+ getList(presence)+ " is the only presence sensor available. "    
       			: "You don't have any presence sensors selected in your Ask Alexa SmartApp. "
    }
    if (listType=="motion" || listType=="motion sensor" || listType=="motion sensors"){
     	outputTxt = motion && motion.size()>1 ? "The available motion sensors are: " +  getList(motion)  
        	: motion && motion.size()==1 ? "The "+ getList(motion)+ " is the only motion sensor available. "    
       			: "You don't have any motion sensors selected in your Ask Alexa SmartApp. "
    }
    if (listType=="door sensor" || listType=="window sensor" || listType=="door sensors" || listType=="window sensors" || listType=="open close sensors"){
     	outputTxt = ocSensors && ocSensors.size()>1 ? "The available open close sensors are: " +  getList(ocSensors)  
        	: ocSensors && ocSensors.size()==1 ? "The "+ getList(ocSensors)+ " is the only open close sensor available. "    
       			: "You don't have any open close sensors selected in your Ask Alexa SmartApp. "
    }
    if (listType=="dimmer" || listType=="dimmers"){
     	outputTxt = dimmers && dimmers.size()>1 ? "You have the following dimmers selected in your SmartApp: " +  getList(dimmers) 
        	: dimmers && dimmers.size()==1 ? "You only have one dimmer selected in your SmartApp named " +  getList(dimmers) 
            	: "You don't have any dimmers selected in the Ask Alexa SmartApp. "
    }
    if (listType=="speakers" || listType=="speaker"){
     	outputTxt = speakers && speakers.size()>1 ? "You can control following speakers: " +  getList(speakers) : 
       		speakers && speakers.size()==1 ? "The " + getList(speakers) + " is the only speaker you can control. "
            	:"You don't have any speakers selected in the Ask Alexa SmartApp. "
    }
    if (listType=="doors" || listType=="door"){
     	outputTxt = doors && doors.size()>1 ? "You have the following doors you can open or close: " +  getList(doors)
        	: doors && doors.size()==1 ? "You have one door, " + getList(doors)+ ", selected that you can open or close."
        		: "You don't have any doors selected in your Ask Alexa SmartApp. "	
    }
    if (listType=="locks" || listType=="lock" ){
     	outputTxt = locks && locks.size()>1 ? "You have the following locks you can lock or unlock: " +  getList(locks)
        	: locks && locks.size()==1 ? "You have one lock named " + getList(locks)+ " that you can control. "
        		:"You don't have any locks selected in your Ask Alexa SmartApp. "	
    }
    if (listType=="colored lights" || listType =="colored light"){
     	outputTxt = cLights && cLights.size()>1 ? "You have the following colored lights you can control: " +  getList(cLights) 
        	: cLights && cLights.size()==1 ? "You have one colored light you can control named " +  getList(cLights) 	
            	: "You don't have any colored lights selected within your Ask Alexa SmartApp. "
    }
    if (listType=="switch" || listType=="switches"){
     	outputTxt = switches? "You can turn on or off the following switches: " +  getList(switches) : "You don't have any switches defined selected your Ask Alexa SmartApp. "	
    }
    if (listType=="routine" || listType=="routines" ){
		def phrases = location.helloHome?.getPhrases()*.label
		outputTxt= phrases.size() >0 ? "The available routines include the following: " + getList(phrases) : "There are no routines set up within your Ask Alexa SmartApp. "
	}
    if (listType=="water" || listType=="water sensor" || listType=="water sensors" ){
		outputTxt= water && water.size()>1 ? "The available water sensors include the following: " + getList(water) 
        	: water && water.size()==1 ? "The only available water sensor is the " + getList(water)  
        		: "There are no water sensors set up within your Ask Alexa SmartApp. "
	}
    if (listType =="colors" || listType =="color"){
    	outputTxt = cLights ? "The available colors to use with colored lights include: " + getList(state.colorData.name) : "You don't have any colored lights selected within your Ask Alexa SmartApp. "
    }
    if (listType == "report" || listType == "reports" ) {
        def repName = "", repCount = childApps.size()
        outputTxt = repCount==1 ? "You can ask me to play the following report: " : repCount> 1 ? "You can ask me to play the following reports: " : "You don't have any reports for me to play. "
        if (repCount){
            childApps.each{repName += it.label ; repCount= repCount-1
                repName += repCount>1 ? ", " : repCount==1 ? " and " : ""
            }
        }
		outputTxt += repName
	}
    if (listType=="events") {
    	outputTxt = "To list events, you must give me a device name to query. For example, you could say, 'tell SmartThings to give me the last events for "+ 
        "the Bedroom'. You may also include the number of events you would like to hear. An example would be, 'tell SmartThings to give me the last 4 events for " +
        "the Bedroom'. "
    }
    if (outputTxt == "") outputTxt = "I didn't understand what you wanted information about. Please try again. "
	sendJSON(outputTxt, lVer)
}
//Report Settings
def processReport() {
    log.debug "Report command received with params $params"
	def rep = params.Report 	//Report name
    def lVer = params.lVer		//Version number of Lambda code
    log.debug "Report Name: " + rep
    log.debug "Lambda Ver: " + lVer
    def outputTxt = "", count = 0
    if (childApps.size()){
        childApps.each {child -> 
        log.debug child.label
        def reportName=child.label.replaceAll("[^a-zA-Z0-9 ]", "")
        if (reportName.toLowerCase().replaceAll(" report", "") == rep) { outputTxt = child.reportResults(); count ++;}}
        if (!outputTxt) outputTxt = "I could not find the report named ${rep}. Please check the name of the report and try again." 
    }
    if (count > 1) outputTxt ="You have multiple reports named '${rep}' in your Ask Alexa SmartApp. Please rename these reports to something unique so I may properly utlize them. "
    sendJSON(outputTxt, lVer)
}
//Smart Home Commands
def processSmartHome() {
    log.debug "Smart home command received with params $params"
	def cmd = params.SHCmd 		//Smart Home Command
	def param = params.SHParam	//Smart Home Parameter
	def lVer = params.lVer		//Version number of Lambda code
    log.debug "Cmd: " + cmd
    log.debug "Param: " + param
    log.debug "Lambda Ver: " + lVer
   	def outputTxt = ""
    if (cmd =="undefined") {
    	if (location.modes?.find{it.name.toLowerCase()==param} && param != currMode) cmd = "mode"
    	if (param=="list" || param=="arm" || param=="undefined") cmd = "security"
        def phrases = location.helloHome?.getPhrases()*.label
        if (phrases.find{it.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase()==param}) cmd = "routine"
    }
    if (cmd == "mode"){
        def currMode = location.mode.toLowerCase()
        if (param=="undefined") outputTxt ="The current SmartThings mode is set to, '${currMode}'. "
        if (location.modes?.find{it.name.toLowerCase()==param} && param != currMode) {
        	def newMode=location.modes?.find{it.name.toLowerCase()==param}
            outputTxt ="I am setting the SmartThings mode to, '${newMode}'. "
            setLocationMode(newMode)
    	}
        else if (param == currMode) outputTxt ="The current SmartThings mode is set already set to, '${currMode}'. No changes are being made. "
        if (!outputTxt) outputTxt = "I did not understand the mode you wanted to set. For a list of available modes, simply say, 'ask SmartThings for mode list'.  "
    }
    if (cmd=="security" || cmd=="smart home" || cmd=="smart home monitor" || cmd=="SHM" ){
    	def SHMstatus = location.currentState("alarmSystemStatus")?.value
		def SHMFullStat = [off : "disarmed", away: "armed away", stay: "armed stay"][SHMstatus] ?: SHMstatus
        def newSHM = "", SHMNewStat = "" 
        if (param=="list" || param=="arm" || param=="undefined") {
            if (param=="arm") outputTxt ="I did not understand how you want me to arm the Smart Home Monitor. Be sure to say 'armed stay' or 'armed away' to properly change the setting. "
            if (param=="undefined") outputTxt ="The Smart Home Monitor is currently set to, '${SHMFullStat}'. "
        }
        else {
            if (param =="off" || param =="disarm") newSHM="off"
            if (param =="away" || param =="armed away") newSHM="away"
            if (param =="stay" || param =="armed stay") newSHM="stay"
    		if (newSHM && SHMstatus!=newSHM) {
        		sendLocationEvent(name: "alarmSystemStatus", value: newSHM)
        		SHMNewStat = [off : "disarmed", away: "armed away", stay: "armed stay"][newSHM] ?: newSHM
            	outputTxt ="I am setting the Smart Home monitor to, '${SHMNewStat}'. "
        	}
            else if (SHMstatus==newSHM) outputTxt ="The Smart Home Monitor is already set to, '${SHMFullStat}'. No changes are being made. " 
        	else if (!newSHM) outputTxt ="I did not understand how you wanted to set the Smart Home Monitor. The proper commands are 'disarm','away' or 'stay'. "
    	}
	}
    if (cmd=="routine"){
    	def phrases = location.helloHome?.getPhrases()*.label
        def runRoutine = phrases.find{it.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase()==param}
        if (runRoutine) {
        	location.helloHome?.execute(runRoutine)
            outputTxt="I am executing the '${param}' routine. "
        }
        if (!outputTxt) outputTxt ="To run SmartThings' routines, ask me to run the routine by its full name. For a list of available routines, simply say, 'ask SmartThings for routine list'. "
    }
	sendJSON(outputTxt, lVer)
}
def getReply(devices, type, dev, op, num, param){
	def result = ""
    log.debug "Type: " + type
	try {
    	def STdevice = devices?.find{it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase() == dev}
        def supportedCaps = STdevice.capabilities
        if (op=="status") {
            if (type == "temperature"){
                result = "The temperature of the ${STdevice} is ${STdevice.currentValue(type)} degrees"
                if (otherStatus) {
                    def humidity =STdevice.currentValue("humidity"), lux =STdevice.currentValue("illuminance"), wet=STdevice.currentValue("water")
                    result += humidity ? ", and the relative humidity is ${humidity}%." : ". "
                    result += lux ? " In addition, the illuminance is currently at ${lux} lux." : ""
                    result += wet ? "Also, this device is a leak sensor, and it is currently ${wet}. " : ""
				}
            }
            else if (type == "presence"){
            	result = "The presence sensor, ${STdevice}, is showing ${STdevice.currentValue(type)}. "
            }
            else if (type =="motion"){
            	def currVal =STdevice.currentValue(type)
                def motionStat=[active : "movement", inactive: "no movement"][currVal] ?: currVal
                result = "The motion sensor, ${STdevice}, is currently reading ${motionStat}. "
            }
            else if (type == "humidity"){
                result = "The relative humidity at the ${STdevice} is ${STdevice.currentValue(type)}%"
                if (otherStatus) {
                    def temp =STdevice.currentValue("temperature")
                    result += temp ? ", and the temperature is ${temp} degrees." : ". "
				}
            }
            else if (type == "level" || type=="color" || type == "switch") {
                def onOffStatus = STdevice.currentValue("switch")
                result = "The ${STdevice} is ${onOffStatus}"
                if (otherStatus) { 
                	def level = STdevice.currentValue("level"), power = STdevice.currentValue("power")
                    result += onOffStatus == "on" && level ? ", and it's set to ${level}%" : ""
                    result += onOffStatus=="on" && power > 0 ? ", and is currently drawing ${power} watts of power. " : ". "
            	}
            }
            else if (type == "thermostat"){
                def temp = STdevice.currentValue("temperature")
                result = "The ${STdevice} temperature reading is currently ${temp as int} degrees"
                if (otherStatus){
                    def heat = STdevice.currentValue("heatingSetpoint"), cool = STdevice.currentValue("coolingSetpoint")
                    def humidity = STdevice.currentValue("humidity"), opState = STdevice.currentValue("thermostatMode")
                    result += opState ? " and the thermostat mode is, '${opState}'." : ". "
                    result += humidity ? "The relative humidity reading is ${humidity}%." : ""
                    result += heat ? " The heating setpoint is set to ${heat as int} degrees. " : ""
                    result += humidity || cool ? " and finally, " : ""
                    result += cool ? " The cooling setpoint is set to ${cool as int} degrees. " : ""
            	}
            }
            else if (type == "contact"){
                result = "The ${STdevice} is currently ${STdevice.currentValue(type)}. "
                if (otherStatus){
                	def temp = STdevice.currentValue("temperature")
                    if (temp) result += "In addition, the temperature reading from this device is ${temp as int} degrees. "
                }
            }
            else if (type == "music"){
                def onOffStatus = STdevice.currentValue("status"), track = STdevice.currentValue("trackDescription"), level = STdevice.currentValue("level")
                def mute = STdevice.currentValue("mute")
                result = "The ${STdevice} is currently ${onOffStatus}"
                result += onOffStatus =="stopped" ? ". " : onOffStatus=="playing" && track ? ": '${track}'" : ""
                result += onOffStatus == "playing" && level && mute =="unmuted" ? ", and it's volume is set to ${level}%. " : mute =="muted" ? ", and it's currently muted. " :""
            }
            else if (type == "water"){
                result = "The water sensor, ${STdevice}, is currently ${STdevice.currentValue(type)}. "
                if (otherStatus){
                	def temp = STdevice.currentValue("temperature")
                    if (temp) result += "In addition, the temperature reading from this device is ${temp as int} degrees. "
                }
            }
            else result = "The ${STdevice} is currently ${STdevice.currentValue(type)}. "
        }
        else {
            if (type == "thermostat"){
                if ((op == "increase" || op=="raise" || op=="up" || op == "decrease" || op=="down" || op=="lower")){
                     def newValues = upDown(STdevice, type, op, num) 
                     num = newValues.newLevel
                }
                if ((param=="heat" || param=="heating" || param =="cool" || param=="cooling" || param =="auto" || param=="automatic" || param=="eco" || param=="comfort") && num == 0 && op=="undefined") op="on"
                if (op == "on" || op=="off") {
                	if (param == "undefined" && op == "on") result="You must designate 'heating mode' or 'cooling mode' when turning the ${STdevice} on. "
                    if (param =="heat" || param=="heating") {result="I am setting the ${STdevice} to 'heating' mode. "; STdevice.heat()}
                    if (param =="cool" || param=="cooling") {result="I am setting the ${STdevice} to 'cooling' mode. "; STdevice.cool()}
                    if (param =="auto" || param=="automatic") {result="I am setting the ${STdevice} to 'auto' mode. Please note, "+
                    	"to properly set the temperature in 'auto' mode, you must specify the heating or cooling setpoints separately. " ; STdevice.auto()}
                    if (op =="off") {result = "I am turning the ${STdevice} thermostat ${op}. "; STdevice.off()}
                    if (STdevice.name.contains("Stelpro") && param=="eco"){ result="I am setting the ${STdevice} to 'eco' mode. "; STdevice.setThermostatMode("eco") }
                    if (STdevice.name.contains("Stelpro") && param=="comfort"){ result="I am setting the ${STdevice} to 'comfort' mode. "; STdevice.setThermostatMode("comfort") }
                }
                else {
                    if (param == "undefined") 
                        if (STdevice.currentValue("thermostatMode")=="heat") param = "heat"
                        else if (STdevice.currentValue("thermostatMode")=="cool") param = "cool"
                        else result = "You must designate a 'heating' or 'cooling' parameter when setting the temperature. "+
                            "The thermostat will not accept a generic setpoint in its current mode. For example, you could simply say, 'Set the ${STdevice} heating to 65 degrees'. "
                    if (num == 0) result = "I didn't understand the temperature setting. Please try again. "
                    if ((param =="heat" || param =="heating") && num > 0) {
                        result="I am setting the heating setpoint of the ${STdevice} to ${num} degrees. "
                        STdevice.setHeatingSetpoint(num) 
                        if (STdevice.name.contains("Stelpro")) STdevice.applyNow()
                    }
                    if ((param =="cool" || param =="cooling") && num>0) {
                        result="I am setting the cooling setpoint of the ${STdevice} to ${num} degrees. "
                        STdevice.setCoolingSetpoint(num)
                    }
                }
            }
            if (type == "color" || type == "level" || type=="switch"){
                def overRideMsg = ""              
                if ((op == "increase" || op=="raise" || op=="up" || op == "decrease" || op=="down" || op=="lower") && (type == "color" || type == "level")){
                     def newValues = upDown(STdevice, type, op, num) 
                     num = newValues.newLevel
                     op= num > 0 ? "on" : "off"
                     overRideMsg = newValues.msg
                }
                if ((type == "switch") || ((type=="level" || type == "color") && num==0 )){
               		if ((type=="level" || type == "color") && num==0 && op=="undefined" && param=="undefined") op="off"
                	if (op=="on" || op=="off"){
                		STdevice."$op"() 
                        result = overRideMsg ? overRideMsg: "I am turning the ${STdevice} ${op}."
                    }
            	}
                if ((type == "color" || type == "level") && num > 0) {
                	STdevice.setLevel(num)
                    result = overRideMsg ? overRideMsg : "I am setting the ${STdevice} to ${num}%. "                    
				}
                if (type == "color" && param !="undefined" && supportedCaps.name.contains("Color Control")){
                    def getColorData = state.colorData.find {it.name.toLowerCase()==param}
                    if (getColorData){
                        def hueColor = getColorData.hue, satLevel = getColorData.sat
                        def newLevel = num > 0 ? num : STdevice.currentValue("level")
                        def newValue = [hue: hueColor as int, saturation: satLevel as int, level: newLevel]
                        STdevice?.setColor(newValue)
                        result = "I am setting the color of the ${STdevice} to ${param}"
                        result += num>0 ? ", at a level of ${num}%. " : ". "
                	}
                }
            	if (!result){
                	if (type=="switch") result = "For the ${STdevice} switch, be sure to give an 'on' or 'off' command. "
            		if (type=="level") result = "For the ${STdevice} dimmer, be sure to use an 'on', 'off', or brightness level setting. "
            		if (type=="color") result = "For the ${STdevice} color controller, remember it can be operated like a switch. You can ask me to turn "+  
                    "it on, off, or set a brightness level. You can also set it to a variety of common colors. For listing of these colors, simply "+
                    "say, 'tell SmartThings to list the colors'. "
                }
            }
            if (type == "music"){             
                if ((op == "increase" || op=="raise" || op=="up" || op == "decrease" || op=="down" || op=="lower")){
                     def newValues = upDown(STdevice, type, op, num) 
                     num = newValues.newLevel
                     if (num==0) op= "off"
                }
                if (op=="off" || op=="stop") {STdevice.stop(); result = "I am turning off the ${STdevice}. "}
                else if (op == "play" || op=="on") {STdevice.play(); result = "I am playing the ${STdevice}. "}
                else if (op=="mute") {STdevice.mute(); result = "I am muting the ${STdevice}. "}
                else if (op=="unmute") {STdevice.unmute(); result = "I am unmuting the ${STdevice}. "}
                else if (op=="pause") {STdevice.pause(); result = "I am pausing the ${STdevice} speaker. "}
                else if (op=="next track") {STdevice.nextTrack(); result = "I am playing the next track on the ${STdevice}. "}
            	else if (op=="previous track") {STdevice.previousTrack(); result = "I am playing the next track on the ${STdevice}. "}
                else result = "I didn't understand what you wanted me to do with the ${STdevice} speaker. "
                if (num > 0) {STdevice.setLevel(num); result = "I am setting the volume of the ${STdevice} to ${num}%. "}
        	}
            if (type == "door"){
                def currentDoorState = STdevice.currentValue(type)
				if (currentDoorState==op || (currentDoorState == "closed" && op=="close")) result = "The ${STdevice} is already ${currentDoorState}. "
                else {
                    if (op != "open" || op != "close") result ="For the ${STdevice}, you must give an 'open' or 'close' command. "
                    if ((op=="open" || op=="close") && (pwNeeded && password && num == 0)) result="You must say your password to ${op} the ${STdevice}. "
                    if ((op=="open" || op=="close") && (pwNeeded && password && num>0 && num != password as int)) result="Sorry, I did not hear the correct password to ${op} the ${STdevice}. "
                    else if ((op=="open" || op=="close") && (!pwNeeded || (password && pwNeeded && num ==password as int) || !password)) {
                        STdevice."$op"() 
                        if (op == "close") op="clos"
                        result = "I am ${op}ing the ${STdevice}. "
                    }
             	}
			}
            if (type == "lock"){
                if (STdevice.currentValue("lock") == op+"ed") result = "The ${STdevice} is already ${op}ed. "
                else {
                    if (op != "lock" || op != "unlock" ) result= "For the ${STdevice}, you must give a 'lock' or 'unlock' command. "
                    if ((op=="lock" || op=="unlock") && (pwNeeded && password && num ==0)) result= "You must say your password to ${op} the ${STdevice}. "
                    if ((op=="lock" || op=="unlock") && (pwNeeded && password && num>0 && num != password as int)) result="Sorry, I did not hear the correct password to ${op} the ${STdevice}. "
                    else if ((op=="lock" || op=="unlock") && (!pwNeeded || (password && pwNeeded && num ==password as int) || !password)) {
                        STdevice."$op"()
                        result = "I am ${op}ing the ${STdevice}. "
                    }
                }
        	}
		}
        if (STdevice.currentValue("battery") && batteryWarn){
			def battery = STdevice.currentValue("battery")
			def battThresLevel = batteryThres as int
			result += battery && battery < battThresLevel ? "Please note, the battery in this device is at ${battery}%. " : ""
		}
    }
    catch (e){ result = "I could not process your request on ${dev}. Ensure you are using the correct commands with the device and try again. " }
    result
}
//Version operation
def processVersion(){
	log.debug "Version command received with params $params"
	def ver = params.Ver 		//Lambda Code Verisons
    def lVer = params.lVer		//Version number of Lambda code
    log.debug "Lambda Code Version: " + ver
   	def outputTxt = "The Ask Alexa SmartApp was developed by Michael Struck to intergrate the SmartThings platform with the Amazon Echo. "+
    	"The Parent SmartApp version is: "  +  versionLong() + ". "
    outputTxt += childApps.size()>0 ? "The Child SmartApp version is: " + childApps[0].versionLong() + ". " : "There are no child apps installed yet. "
	outputTxt += "And the Amazon Lambda code version is: " + ver + ". "
    sendJSON(outputTxt, lVer)
}
def setupData(){
	log.info "Set up web page located at : ${getApiServerUrl()}/api/smartapps/installations/${app.id}/setup?access_token=${state.accessToken}"
    def result ="<div style='padding:10px'><i><b>Lambda code variables:</b></i><br><br>var STappID = '${app.id}';<br>var STtoken = '${state.accessToken}';<br>"
    result += "var url='${getApiServerUrl()}/api/smartapps/installations/' + STappID + '/' ;<br><br><hr>"
	result += "<i><b>Amazon ASK Custom Slot Information:</b></i><br><br><b>DEVICE_TYPE_LIST</b><br><br>"
	state.deviceTypes.each{result += it + "<br>"}
    result += "<br><b>LIST_OF_DEVICES</b><br><br>"
    def deviceList = getDeviceList(), deviceNames = deviceList.name.unique()
    if (deviceNames) result += deviceList.name != deviceNames ? "<b>**NOTICE:</b> You have duplicated device names that have been<br>removed from the list below. Be sure to have unique names for<br>each device.**<br><br>" : ""
    if (deviceNames) deviceNames.each{result += it + "<br>" }
    result += "<br><b>LIST_OF_OPERATORS</b><br><br>on<br>off<br>up<br>down<br>increase<br>decrease<br>lower<br>raise<br>status<br>events<br>event<br>"
    result += locks ? "lock<br>unlock<br>" : ""
    result += doors ? "open<br>close<br>" : ""
    result += speakers ?"play<br>stop<br>pause<br>mute<br>unmute<br>next track<br>previous track<br>" : ""
    result += "<br><b>LIST_OF_REPORTS</b><br><br>"
    if (childApps.size()) {
    	childApps.each { 
        	def reportParsed = it.label.toLowerCase().replaceAll(" report", "")
            result += reportParsed.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase() + "<br>"
        }       
	}
    result += "<br><b>LIST_OF_PARAMS</b><br><br>"
	if (tstats) result += "heat<br>cool<br>heating<br>cooling<br>auto<br>automatic<br>"
    if (tstats?.find{it.name.contains("Stelpro")}) result += "eco<br>comfort<br>"
    if (cLights) fillColorSettings(); state.colorData.each {result += it.name.toLowerCase()+"<br>"}
    result +="<br><b>LIST_OF_SHPARAM</b><br><br>"
    def cmd = "<br><b>LIST_OF_SHCMD</b><br><br>"
    if (modes || routines || SHM) {
        if (routines){
            cmd += "routine<br>"
            def phrases = location.helloHome?.getPhrases()*.label
            if (phrases) phrases.each{result += it.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase() + "<br>"}
        }
        if (SHM) {
        	cmd += "security<br>smart home<br>smart home monitor<br>"
            result +="arm<br>disarm<br>stay<br>armed stay<br>armed away<br>off<br>"
        }
        if (modes) {
        	cmd += "mode<br>"
            location.modes.each {result += it.name.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase() + "<br>"}
        }
	result += cmd
    }	
	result += "<br><hr><br><i><b>URL of this setup page:</b></i><br><br>${getApiServerUrl()}/api/smartapps/installations/${app.id}/setup?access_token=${state.accessToken}<br><br><hr></div>"
}
def displayData(){
	render contentType: "text/html", data: """<!DOCTYPE html><html><head><meta charset="UTF-8" /><meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0"/></head><body style="margin: 0;">${setupData()}</body></html>"""
}
//Common Code
def OAuthToken(){
	try {
        createAccessToken()
		log.debug "Creating new Access Token"
	} catch (e) { log.error "Access Token not defined. OAuth may not be enabled. Go to the SmartApp IDE settings to enable OAuth." }
}
def reportDesc(){def results = childApps.size() ? childApps.size()==1 ? "One Voice Report Configured" : childApps.size() + " Voice Reports Configured" : "No Voices Reports Configured\nTap to create new report"}
def reportGrey(){def results =childApps.size() ? "complete" : ""}
def getDesc(param1, param2, param3) {def result = param1 || param2 || param3 ? "Status: CONFIGURED - Tap to edit/view" : "Status: UNCONFIGURED - Tap to configure"}
def getState(param1, param2, param3) {def result = param1 || param2 || param3 ? "complete" : ""}
def fillColorSettings(){ 
	def colorData = []
    colorData << [name: "White", hue: 10, sat: 56] << [name: "Orange", hue: 8, sat: 100] << [name: "Red", hue: 100, sat: 100]
    colorData << [name: "Green", hue: 37, sat: 100] << [name: "Blue", hue: 64, sat: 100] << [name: "Yellow", hue: 16, sat: 100] 
    colorData << [name: "Purple", hue: 78, sat: 100] << [name: "Pink", hue: 87, sat: 100]
    if (customName && (customHue > -1 && customerHue < 101) && (customSat > -1 && customerSat < 101)){
    	colorData << [name: customName, hue: customHue as int, sat: customSat as int]
    }
    state.colorData=colorData
}
def getList(items){
	def result = "", itemCount=items.size() as int
	items.each{ result += it; itemCount=itemCount-1	
		result += itemCount>1 ? ", " : itemCount==1 ? " and " : ""
    }
	result
}
def getDeviceList(){
	def result = []
    try {
        if (switches) switches.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type: "switch", devices: switches] }
        if (dimmers) dimmers.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"level", devices: dimmers]  }
        if (cLights) cLights.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"color", devices: cLights] }
        if (doors) doors.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"door", devices: doors]  }
        if (locks) locks.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"lock", devices: locks] }
        if (tstats) tstats.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"thermostat", devices: tstats] }
        if (speakers) speakers.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"music", devices: speakers] }
        if (temps) temps.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"temperature", devices: temps] }
        if (humid) humid.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"humidity", devices: humid] }
        if (ocSensors) ocSensors.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"contact", devices: ocSensors] }
        if (water) water.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"water", devices: water] }
        if (motion) motion.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"motion", devices: motion] }
        if (presence) presence.collect{ result << [name: it.label.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase(), type:"presence", devices: presence] }
    }
    catch (e) {
    	log.warn "There was an issue parsing the device labels. Be sure all of the devices are uniquley named/labeled and that none of them are blank (null). "
    }
    result
}
def fillTypeList(){
	state.deviceTypes =["reports","report","switches","switch","dimmers","dimmer","colored lights","color","colors","speakers","speaker","water sensor","water sensors","water",
    	"lock","locks","thermostats","thermostat","temperature sensors","modes","routines","smart home monitor","SHM","security","temperature","door","doors", "humidity", "humidity sensor", 
        "humidity sensors", "presence", "presence sensors", "motion", "motion sensor", "motion sensors", "door sensor", "door sensors", "window sensor", "window sensors", "open close sensors",
        "colored light", "events"]    	  
}
def upDown(device, type, op, num){
    def numChange, newLevel, currLevel, defMove, txtRsp = ""
    if (type=="color" || type=="level") { defMove = lightAmt ; currLevel = device.currentValue("switch")=="on" ? device.currentValue("level") as int : 0 } 
    if (type=="music") { defMove = speakerAmt ; currLevel = device.currentValue("level") as int }
    if (type=="thermostat") { defMove=tstatAmt ; currLevel =device.currentValue("temperature") as int }
    if (op == "increase" || op=="raise" || op=="up")  numChange = num == 0 ? defMove : num > 0 ? num : 0
    if (op == "decrease" || op=="down" || op=="lower") numChange = num == 0 ? -defMove : num > 0 ? -num  : 0
    newLevel = currLevel + numChange; newLevel = newLevel > 100 ? 100 : newLevel < 0 ? 0 : newLevel
    if (type =="level" || type=="color"){
        if (device.currentValue("switch")=="on") {
            if (newLevel < 100 && newLevel > 0 ) txtRsp="I am setting the ${device} to a new value of ${newLevel}%. "
            if (newLevel == 0) txtRsp= "The new value would be zero or below, so I am turning the ${device} off. "	
        }
        if (device.currentValue("switch")=="off") {
            if (newLevel == 0) txtRsp= "The ${device} is off. I am taking no action. "
            if (newLevel < 100 && newLevel > 0 ) txtRsp="I am turning ${device} on and setting it to level of ${newLevel}%. "
        }
    	if (newLevel == 100) txtRsp= currLevel < 99 ? "I am increasing the level of the ${device} to its maximum level. " : "The ${device} is at its maximum level. "      
    }
    def response = [newLevel: newLevel, msg:txtRsp]
}
def getLastEvent(device, count) {
    def lastEvt= device.events(), eDate, eDesc, today, eventDay, voiceDay, i , evtCount = 0
    def result = ""
    for( i = 0 ; i < 10 ; i++ ) {
        eDate = lastEvt.date[i].getTime()
        eDesc = lastEvt.descriptionText[i]
    	if (eDesc) {
        	today = new Date(now()).format("EEEE, MMMM dd, yyyy", location.timeZone)
    		eventDay = new Date(eDate).format("EEEE, MMMM dd, yyyy", location.timeZone)
    		voiceDay = today == eventDay ? "Today" : "On " + eventDay
    		result += voiceDay + " at " + new Date(eDate).format("h:mm aa", location.timeZone) + " the event was: " + eDesc + ". "
   	 		evtCount ++
            if (evtCount == count) break
        }
	}
    def diff = count - evtCount
	result = evtCount>1 ? "The following are the last ${evtCount} events for the ${device}: " + result : "The last event for the ${device} was: " + result
    result += diff > 1 ? "There were ${diff} items that were skipped due to the description being empty. " : diff==1 ? "There was one item that was skipped due to the description being a null value. " : ""
    if (evtCount==0) result="There were no past events in the device log. "
    return result
}
def sendJSON(outputTxt, lVer){
	def LambdaVersion = lVer as int
    if (LambdaVersion != 101) outputTxt = "I am unable to complete your request. The version of the Lambda code you are using is out-of-date. Please install the latest code and try again. "
    log.debug outputTxt
    return ["voiceOutput":outputTxt]
}
//Version/Copyright/Information/Help
private def textAppName() { def text = "Ask Alexa" }	
private def textVersion() {
    def version = "Parent App Version: 1.0.2b (05/15/2016)"
    def childCount = childApps.size()
    def childVersion = childCount ? childApps[0].textVersion() : "No voice reports installed"
    return "${version}\n${childVersion}"
}
private def versionInt(){ return 102 }
private def versionLong(){ return "1.0.2b" }
private def textCopyright() {return "Copyright © 2016 Michael Struck" }
private def textLicense() {
	def text = "Licensed under the Apache License, Version 2.0 (the 'License'); "+
		"you may not use this file except in compliance with the License. "+
		"You may obtain a copy of the License at"+
		"\n\n"+
		"    http://www.apache.org/licenses/LICENSE-2.0"+
		"\n\n"+
		"Unless required by applicable law or agreed to in writing, software "+
		"distributed under the License is distributed on an 'AS IS' BASIS, "+
		"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. "+
		"See the License for the specific language governing permissions and "+
		"limitations under the License."
}
private def textHelp() { 
	def text = "This SmartApp allows provides an interface to control and "+
    	"query the SmartThings environment via the Amazon Echo ('Alexa'). "+
    	"For more information, go to http://thingsthataresmart.wiki/index.php?title=Ask_Alexa."
}
