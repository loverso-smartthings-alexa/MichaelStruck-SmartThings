/**
 *  Smart Room Lighting and Dimming
 *
 *  Version - 1.0 5/4/15
 * 
 *  Copyright 2015 Michael Struck - Uses code from Lighting Director by Tim Slagle & Michael Struck
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *	The original licensing applies, with the following exceptions:
 *		1.	These modifications may NOT be used without freely distributing all these modifications freely
 *			and without limitation, in source form.	 The distribution may be met with a link to source code
 *			with these modifications.
 *		2.	These modifications may NOT be used, directly or indirectly, for the purpose of any type of
 *			monetary gain.	These modifications may not be used in a larger entity which is being sold,
 *			leased, or anything other than freely given.
 *		3.	To clarify 1 and 2 above, if you use these modifications, it must be a free project, and
 *			available to anyone with "no strings attached."	 (You may require a free registration on
 *			a free website or portal in order to distribute the modifications.)
 *		4.	The above listed exceptions to the original licensing do not apply to the holder of the
 *			copyright of the original work.	 The original copyright holder can use the modifications
 *			to hopefully improve their original work.  In that event, this author transfers all claim
 *			and ownership of the modifications to "SmartThings."
 *
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 */
 
definition(
    name: "Smart Room Lighting and Dimming",
    namespace: "MichaelStruck",
    author: "Michael Struck",
    description: "Control up to 4 rooms (scenarios) of light/dimmers based on motion and lux levels.",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Smart-Room-Lighting-and-Dimming/SmartLight.png",
    iconX2Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Smart-Room-Lighting-and-Dimming/SmartLight@2x.png",
    iconX3Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Smart-Room-Lighting-and-Dimming/SmartLight@2x.png"
    )

preferences {
    page name:"pageSetup"
    page name:"pageSetupScenarioA"
    page name:"pageSetupScenarioB"
    page name:"pageSetupScenarioC"
    page name:"pageSetupScenarioD"
}

// Show setup page
def pageSetup() {
	dynamicPage(name: "pageSetup", title: "Status", nextPage: null, install: true, uninstall: true) {
        section("Setup Menu") {
            href "pageSetupScenarioA", title: getTitle(settings.ScenarioNameA), description: getDesc(settings.ScenarioNameA), state: greyOut(settings.ScenarioNameA)
            href "pageSetupScenarioB", title: getTitle(settings.ScenarioNameB), description: getDesc(settings.ScenarioNameB), state: greyOut(settings.ScenarioNameB)
            href "pageSetupScenarioC", title: getTitle(settings.ScenarioNameC), description: getDesc(settings.ScenarioNameC), state: greyOut(settings.ScenarioNameC)
			href "pageSetupScenarioD", title: getTitle(settings.ScenarioNameD), description: getDesc(settings.ScenarioNameD), state: greyOut(settings.ScenarioNameD)
            }
        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
        }
    }
}

// Show "pageSetupScenarioA" page
def pageSetupScenarioA() {
    def pageName = ""
    if (ScenarioNameA) {
        	pageName = ScenarioNameA
   		}
    return dynamicPage(name: "pageSetupScenarioA", title: "${pageName}", nextPage: "pageSetup") {
		section("Name your scenario") {
            input name:"ScenarioNameA", type: "text", title: "Scenario Name", multiple: false, required: false, defaultValue: empty
    	}

	section("Devices included in the scenario") {
    	input name: "A_motion",type: "capability.motionSensor", title: "Using these motion sensors...", multiple: true, required: false
        input name: "A_switches", type: "capability.switch",title: "Control the following switches...", multiple: true, required: false
        input name: "A_dimmers", type: "capability.switchLevel", title: "Dim the following...", multiple: true, required:false, refreshAfterSelection: true
	}

	section("Lighting settings") {
        href "levelInputA", title: "Dimmer Options", description: getLevelLabel(A_levelDimOn, A_levelDimOff, A_dimmers, A_calcOn), state: greyedOutLevel(A_dimmers), refreshAfterSelection: true
        input name: "A_turnOnLux",type: "number",title: "Only run this scenario if lux is below...", multiple: false, required: false
        input name: "A_luxSensors",type: "capability.illuminanceMeasurement",title: "On these lux sensors",multiple: false,required: false
        input name: "A_turnOff",type: "number",title: "Turn off this scenario after motion stops (minutes)...", multiple: false, required: false
	}
            
	section("Restrictions") {            
        input name: "A_triggerOnce",type: "bool",title: "Trigger only once per day...", defaultValue: false
        input name: "A_switchDisable", type:"bool", title: "Stop triggering if switches are pressed...", defaultValue:false
        href "timeIntervalInputA", title: "Only during a certain time...", description: getTimeLabel(A_timeStart, A_timeEnd), state: greyedOutTime(A_timeStart, A_timeEnd), refreshAfterSelection: true
        input name:  "A_day", type: "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], title: "Only on certain days of the week...",  multiple: true, required:   false
        input name: "A_mode", type: "mode", title: "Only during the following modes...", multiple: true, required: false
	}

	section("Help") {
    	paragraph helpText()
    	}
    }
    
}

// Show "pageSetupScenarioB" page
def pageSetupScenarioB() {
    def pageName = ""
    if (ScenarioNameB) {
        	pageName = ScenarioNameB
   		}
    return dynamicPage(name: "pageSetupScenarioB", title: "${pageName}", nextPage: "pageSetup") {
		section("Name your scenario") {
            input name:"ScenarioNameB", type: "text", title: "Scenario Name", multiple: false, required: false, defaultValue: empty
    	}

	section("Devices included in the scenario") {
    	input name: "B_motion",type: "capability.motionSensor", title: "Using these motion sensors...", multiple: true, required: false
        input name: "B_switches", type: "capability.switch",title: "Control the following switches...", multiple: true, required: false
        input name: "B_dimmers", type: "capability.switchLevel", title: "Dim the following...", multiple: true, required:false, refreshAfterSelection: true
	}

	section("Lighting settings") {
        href "levelInputB", title: "Dimmer Options", description: getLevelLabel(B_levelDimOn, B_levelDimOff, B_dimmers, B_calcOn), state: greyedOutLevel(B_dimmers), refreshAfterSelection: true
        input name: "B_turnOnLux",type: "number",title: "Only run this scenario if lux is below...", multiple: false, required: false
        input name: "B_luxSensors",type: "capability.illuminanceMeasurement",title: "On these lux sensors",multiple: false,required: false
        input name: "B_turnOff",type: "number",title: "Turn off this scenario after motion stops (minutes)...", multiple: false, required: false
	}
            
	section("Restrictions") {            
        input name: "B_triggerOnce",type: "bool",title: "Trigger only once per day...", defaultValue: false
        input name: "B_switchDisable", type:"bool", title: "Stop triggering if switches are pressed...", defaultValue:false
        href "timeIntervalInputB", title: "Only during a certain time...", description: getTimeLabel(B_timeStart, B_timeEnd), state: greyedOutTime(B_timeStart, B_timeEnd), refreshAfterSelection:true
        input name: "B_day", type: "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], title: "Only on certain days of the week...",  multiple: true, required:   false
        input name: "B_mode", type: "mode", title: "Only during the following modes...", multiple: true, required: false
	}

	section("Help") {
    	paragraph helpText()
    	}
    }
    
}

// Show "pageSetupScenarioC" page
def pageSetupScenarioC() {
    def pageName = ""
    if (ScenarioNameC) {
        	pageName = ScenarioNameC
   		}
    return dynamicPage(name: "pageSetupScenarioC", title: "${pageName}", nextPage: "pageSetup") {
		section("Name your scenario") {
            input name:"ScenarioNameC", type: "text", title: "Scenario Name", multiple: false, required: false, defaultValue: empty
    	}

	section("Devices included in the scenario") {
    	input name: "C_motion",type: "capability.motionSensor", title: "Using these motion sensors...", multiple: true, required: false
        input name: "C_switches", type: "capability.switch",title: "Control the following switches...", multiple: true, required: false
        input name: "C_dimmers", type: "capability.switchLevel", title: "Dim the following...", multiple: true, required:false,refreshAfterSelection: true
	}

	section("Lighting settings") {
        href "levelInputC", title: "Dimmer Options", description: getLevelLabel(C_levelDimOn, C_levelDimOff, C_dimmers, C_calcOn), state: greyedOutLevel(C_dimmers), refreshAfterSelection: true
        input name: "C_turnOnLux",type: "number", title: "Only run this scenario if lux is below...", multiple: false, required: false
        input name: "C_luxSensors",type: "capability.illuminanceMeasurement",title: "On these lux sensors",multiple: false,required: false
        input name: "C_turnOff",type: "number",title: "Turn off this scenario after motion stops (minutes)...", multiple: false, required: false
	}
            
	section("Restrictions") {            
        input name: "C_triggerOnce",type: "bool",title: "Trigger only once per day...", defaultValue: false
        input name: "C_switchDisable", type:"bool", title: "Stop triggering if switches are pressed...", defaultValue:false
        href "timeIntervalInputC", title: "Only during a certain time...", description: getTimeLabel(C_timeStart, C_timeEnd), state: greyedOutTime(C_timeStart, C_timeEnd), refreshAfterSelection:true
        input name: "C_day", type: "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], title: "Only on certain days of the week...",  multiple: true, required:   false
        input name: "C_mode", type: "mode", title: "Only during the following modes...", multiple: true, required: false
	}

	section("Help") {
    	paragraph helpText()
    	}
    }
    
}

// Show "pageSetupScenarioD" page
def pageSetupScenarioD() {
    def pageName = ""
    if (ScenarioNameD) {
        	pageName = ScenarioNameD
   		}
    return dynamicPage(name: "pageSetupScenarioD", title: "${pageName}", nextPage: "pageSetup") {
		section("Name your scenario") {
            input name:"ScenarioNameD", type: "text", title: "Scenario Name", multiple: false, required: false, defaultValue: empty
    	}

	section("Devices included in the scenario") {
    	input name: "D_motion",type: "capability.motionSensor", title: "Using these motion sensors...", multiple: true, required: false
        input name: "D_switches", type: "capability.switch",title: "Control the following switches...", multiple: true, required: false
        input name: "D_dimmers", type: "capability.switchLevel", title: "Dim the following...", multiple: true, required:false, refreshAfterSelection: true
	}

	section("Lighting settings") {
		href "levelInputD", title: "Dimmer Options", description: getLevelLabel(D_levelDimOn, D_levelDimOff, D_dimmers, D_calcOn), state: greyedOutLevel(D_dimmers), refreshAfterSelection: true
        input name: "D_turnOnLux",type: "number",title: "Only run this scenario if lux is below...", multiple: false, required: false
        input name: "D_luxSensors",type: "capability.illuminanceMeasurement",title: "On these lux sensors",multiple: false,required: false
        input name: "D_turnOff",type: "number",title: "Turn off this scenario after motion stops (minutes)...", multiple: false, required: false
	}
            
	section("Restrictions") {            
        input name: "D_triggerOnce",type: "bool",title: "Trigger only once per day...", defaultValue: false
        input name: "D_switchDisable", type:"bool", title: "Stop triggering if switches are pressed...", defaultValue:false
        href "timeIntervalInputD", title: "Only during a certain time...", description: getTimeLabel(D_timeStart, D_timeEnd), state: greyedOutTime(D_timeStart, D_timeEnd), refreshAfterSelection:true
        input name: "D_day", type: "enum", options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"], title: "Only on certain days of the week...",  multiple: true, required:   false
        input name: "D_mode", type: "mode", title: "Only during the following modes...", multiple: true, required: false
	}

	section("Help") {
    	paragraph helpText()
    	}
    }
    
}

def installed() {
    initialize()
}

def updated() {

    unschedule()
    unsubscribe()
    initialize()
}

def initialize() {

midNightReset()

if(A_motion) {
	subscribe(A_motion, "motion", onEventA)
}

if(B_motion) {
	subscribe(B_motion, "motion", onEventB)
}

if(C_motion) {
	subscribe(C_motion, "motion", onEventC)
}

if(D_motion) {
	subscribe(D_motion, "motion", onEventD)
}

if(A_switchDisable) {
	subscribe(A_switches, "switch", onPressA)
}

if(B_switchDisable) {
	subscribe(B_switches, "switch", onPressB)
}

if(C_switchDisable) {
	subscribe(C_switches, "switch", onPressC)
}

if(D_switchDisable) {
	subscribe(D_switches, "switch", onPressD)
}
}

def onEventA(evt) {
  	
if ((!A_mode || A_mode.contains(location.mode)) && getTimeOk (A_timeStart, A_timeEnd) && getDayOk(A_day)) {
if (!A_luxSensors || (A_luxSensors.latestValue("illuminance") <= A_turnOnLux)){
    if (A_motion.latestValue("motion").contains("active")) {
         if ((!A_triggerOnce || (A_triggerOnce && !state.A_triggered)) && (!A_switchDisable || (A_switchDisable && !state.A_triggered))) {
        	log.debug("Motion Detected Running '${ScenarioNameA}'")
            def levelSetOn = A_levelDimOn
            def levelSetOff = A_levelDimOff
            if (!levelSetOn) {
            	levelSetOn = 100
            }
            if (!levelSetOff) {
            	levelSetOff = 0
            }
            if (A_calcOn && A_luxSensors) {
    			levelSetOn = (levelSetOn * (1-(A_luxSensors.latestValue("illuminance")/A_turnOnLux))) + levelSetOff
                if (levelSetOn > 100) {
               		levelSetOn = 100
               	}
    		}
   			log.debug levelSetOn
        	A_dimmers?.setLevel(levelSetOn)
        	A_switches?.on()
        	if (A_triggerOnce){
           		state.A_triggered = true
            	if (!A_turnOff) {
					runOnce (getMidnight(), midNightReset)
            	}
			}
			if (state.A_timerStart){
           		unschedule(delayTurnOffA)
       	   		state.A_timerStart = false
        	}	
		}
}
else {
    	if (A_turnOff) {
		runIn(A_turnOff * 60, "delayTurnOffA")
        state.A_timerStart = true
        }
        else {
        A_switches?.off()
        def levelSetOff = A_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
        A_dimmers?.setLevel(levelSetOff)
        	if (state.A_triggered) {
    			runOnce (getMidnight(), midNightReset)
    		}
        }
}
}
}
else{
log.debug("Motion outside of mode or time/date/trigger restriction.  Not running scenario.")
}
}

def delayTurnOffA(){
	A_switches?.off()
	def levelSetOff = A_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
    A_dimmers?.setLevel(levelSetOff)
	state.A_timerStart = false
	if (state.A_triggered) {
    	runOnce (getMidnight(), midNightReset)
    }

}

def onPressA(evt) {
	if (evt.physical){
    	state.A_triggered = true
        unschedule(delayTurnOffA)
        log.debug "Physical switch in '${ScenarioNameA}' pressed. Trigger for this scenario disabled."
	}
}

def onEventB(evt) {

if ((!B_mode || B_mode.contains(location.mode)) && getTimeOk (B_timeStart, B_timeEnd) && getDayOk(B_day)) {
if (!B_luxSensors || (B_luxSensors.latestValue("illuminance") <= B_turnOnLux)){
    if (B_motion.latestValue("motion").contains("active")) {
         if ((!B_triggerOnce || (B_triggerOnce && !state.B_triggered)) && (!B_switchDisable || (B_switchDisable && !state.B_triggered))) {
        	log.debug("Motion Detected Running '${ScenarioNameB}'")
            def levelSetOn = B_levelDimOn
            def levelSetOff = B_levelDimOff
            if (!levelSetOn) {
            	levelSetOn = 100
            }
            if (!levelSetOff) {
            	levelSetOff = 0
            }
            if (B_calcOn && B_luxSensors) {
    			levelSetOn = (levelSetOn * (1-(B_luxSensors.latestValue("illuminance")/B_turnOnLux))) + levelSetOff
                if (levelSetOn > 100) {
               		levelSetOn = 100
               	}
    		}
   			log.debug levelSetOn
        	B_dimmers?.setLevel(levelSetOn)
        	B_switches?.on()
        	if (B_triggerOnce){
           		state.B_triggered = true
            	if (!B_turnOff) {
					runOnce (getMidnight(), midNightReset)
            	}
			}
			if (state.B_timerStart){
           		unschedule(delayTurnOffB)
       	   		state.B_timerStart = false
        	}	
		}
}
else {
    	if (B_turnOff) {
			runIn(B_turnOff * 60, "delayTurnOffB")
        	state.B_timerStart = true
        }
        
        else {
        	B_switches?.off()
			def levelSetOff = B_levelDimOff
        	if (!levelSetOff) {
        		levelSetOff = 0
        		}
    		B_dimmers?.setLevel(levelSetOff)
            if (state.B_triggered) {
    			runOnce (getMidnight(), midNightReset)
    		}
        }
	
}
}
}
else{
log.debug("Motion outside of mode or time/date/trigger restriction.  Not running scenario.")
}
}

def delayTurnOffB(){
	B_switches?.off()
	def levelSetOff = B_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
    B_dimmers?.setLevel(levelSetOff)
	state.B_timerStart = false
    if (state.B_triggered) {
    	runOnce (getMidnight(), midNightReset) 
	}
}

def onPressB(evt) {
	if (evt.physical){
    	state.B_triggered = true
        unschedule(delayTurnOffB)
        log.debug "Physical switch in '${ScenarioNameB}' pressed. Trigger for this scenario disabled."
	}
}

def onEventC(evt) {

if ((!C_mode || C_mode.contains(location.mode)) && getTimeOk (C_timeStart, C_timeEnd) && getDayOk(C_day)) {
if (!C_luxSensors || (C_luxSensors.latestValue("illuminance") <= C_turnOnLux)){
    if (C_motion.latestValue("motion").contains("active")) {
         if ((!C_triggerOnce || (C_triggerOnce && !state.C_triggered)) && (!C_switchDisable || (C_switchDisable && !state.C_triggered))) {
        	log.debug("Motion Detected Running '${ScenarioNameB}'")
            def levelSetOn = C_levelDimOn
            def levelSetOff = C_levelDimOff
            if (!levelSetOn) {
            	levelSetOn = 100
            }
            if (!levelSetOff) {
            	levelSetOff = 0
            }
            if (C_calcOn && C_luxSensors) {
    			levelSetOn = (levelSetOn * (1-(C_luxSensors.latestValue("illuminance")/C_turnOnLux))) + levelSetOff
                if (levelSetOn > 100) {
               		levelSetOn = 100
               	}
    		}
   			log.debug levelSetOn
        	C_dimmers?.setLevel(levelSetOn)
        	C_switches?.on()
        	if (C_triggerOnce){
           		state.C_triggered = true
            	if (!C_turnOff) {
					runOnce (getMidnight(), midNightReset)
            	}
			}
			if (state.C_timerStart){
           		unschedule(delayTurnOffC)
       	   		state.C_timerStart = false
        	}	
		}
}
else {
    	if (C_turnOff) {
		runIn(C_turnOff * 60, "delayTurnOffC")
        state.C_timerStart = true
        }
        else {
        C_switches?.off()
		def levelSetOff = C_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
    	C_dimmers?.setLevel(levelSetOff)
        	if (state.C_triggered) {
    			runOnce (getMidnight(), midNightReset)
    		}
        }
	
}
}
}
else{
log.debug("Motion outside of mode or time/date/trigger restriction.  Not running scenario.")
}
}

def delayTurnOffC(){
	C_switches?.off()
	def levelSetOff = C_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
    C_dimmers?.setLevel(levelSetOff)
	state.C_timerStart = false
	if (state.C_triggered) {
    	runOnce (getMidnight(), midNightReset)
    }

}

def onPressC(evt) {
	if (evt.physical){
    	state.C_triggered = true
        unschedule(delayTurnOffC)
        log.debug "Physical switch in '${ScenarioNameC}' pressed. Trigger for this scenario disabled."
	}
}

def onEventD(evt) {

if ((!D_mode || D_mode.contains(location.mode)) && getTimeOk (D_timeStart, D_timeEnd) && getDayOk(D_day)) {
if (!D_luxSensors || (D_luxSensors.latestValue("illuminance") <= D_turnOnLux)){
    if (D_motion.latestValue("motion").contains("active")) {
         if ((!D_triggerOnce || (D_triggerOnce && !state.D_triggered)) && (!D_switchDisable || (D_switchDisable && !state.D_triggered))) {
        	log.debug("Motion Detected Running '${ScenarioNameB}'")
            def levelSetOn = D_levelDimOn
            def levelSetOff = D_levelDimOff
            if (!levelSetOn) {
            	levelSetOn = 100
            }
            if (!levelSetOff) {
            	levelSetOff = 0
            }
            if (D_calcOn && D_luxSensors) {
    			levelSetOn = (levelSetOn * (1-(D_luxSensors.latestValue("illuminance")/D_turnOnLux))) + levelSetOff
                if (levelSetOn > 100) {
               		levelSetOn = 100
               	}
    		}
   			log.debug levelSetOn
        	D_dimmers?.setLevel(levelSetOn)
        	D_switches?.on()
        	if (D_triggerOnce){
           		state.D_triggered = true
            	if (!D_turnOff) {
					runOnce (getMidnight(), midNightReset)
            	}
			}
			if (state.D_timerStart){
           		unschedule(delayTurnOffD)
       	   		state.D_timerStart = false
        	}	
		}
}
else {
    	if (D_turnOff) {
		runIn(D_turnOff * 60, "delayTurnOffD")
        state.D_timerStart = true
        }
        else {
        D_switches?.off()
		def levelSetOff = D_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
    	D_dimmers?.setLevel(levelSetOff)
        	if (state.D_triggered) {
    			runOnce (getMidnight(), midNightReset)
    		}
        }
}
}
}
else{
log.debug("Motion outside of mode or time/date/trigger restriction.  Not running scenario.")
}
}

def delayTurnOffD(){
	D_switches?.off()
	def levelSetOff = D_levelDimOff
        if (!levelSetOff) {
        	levelSetOff = 0
        }
    D_dimmers?.setLevel(levelSetOff)
	state.D_timerStart = false
	if (state.D_triggered) {
    	runOnce (getMidnight(), midNightReset)
    }

}

def onPressD(evt) {
	if (evt.physical){
    	state.D_triggered = true
        unschedule(delayTurnOffD)
        log.debug "Physical switch in '${ScenarioNameD}' pressed. Trigger for this scenario disabled."
	}
}

//Common Methods

def midNightReset() {
	state.A_triggered = false
    state.B_triggered = false
    state.C_triggered = false
    state.D_triggered = false
}

private def helpText() {
	def text =
    	"Select motion sensors to control a set of lights. Each scenario " +
        "can control dimmers and switches but can also be restricted " +
        "to modes or between certain times and turned off after motion " +
        "motion stops. Scenarios can also be limited to running once " +
        "or to stop running if the physical switches are pressed."
	text
}

def greyOut(scenario){
	def result = ""
    if (scenario) {
    	result = "complete"	
    }
    result
}

def greyedOutTime(start, end){
	def result = ""
    if (start || end) {
    	result = "complete"	
    }
    result
}

def greyedOutLevel(dimmers){
	def result = ""
    if (dimmers) {
    	result = "complete"	
    }
    result
}

def getTitle(scenario) {
	def title = "Empty"
	if (scenario) {
		title = scenario
    }
	title
}

def getDesc(scenario) {
	def desc = "Tap to create a scenario"
	if (scenario) {
		desc = "Tap to edit scenario"
    }
	desc	
}

def getMidnight() {
	def midnightToday = timeToday("2000-01-01T23:59:59.999-0000", location.timeZone)
	midnightToday
}

private getTimeOk(startTime, endTime) {
	def result = true
	if (startTime && endTime) {
		def currTime = now()
		def start = timeToday(startTime).time
		def stop = timeToday(endTime).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start || currTime <= stop
	}
	result
}


def getTimeLabel(start, end){
	def timeLabel = "Tap to set"
	
    if(start && end){
    	timeLabel = "Between" + " " + hhmm(start) + " "  + "and" + " " +  hhmm(end)
    }
    else if (start) {
		timeLabel = "Start at" + " " + hhmm(start)
    }
    else if(end){
    timeLabel = "End at" + hhmm(end)
    }
	timeLabel	
}

def getLevelLabel(on, off, dimmers, calcOn) {
    def levelLabel = "Choose dimmers above and tap here to set levels"
    if (dimmers) {
    	levelLabel="'On' level: "
        if (!on) {
        	on= 100
        }
        if (!off) {
        	off = 0
        }
        if (calcOn) {
    		levelLabel = levelLabel + "Between ${off}% and ${on}% based on lux"
        }
        else {
        	if (on) {
    			levelLabel = levelLabel + "${on}%"
    		}
        }
        levelLabel = levelLabel + "\n'Off' level: ${off}%" 
    }
    levelLabel
}


private hhmm(time, fmt = "h:mm a")
{
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}

private getDayOk(dayList) {
	def result = true
    if (dayList) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = dayList.contains(day)
	}
    result
}

page(name: "timeIntervalInputA", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "A_timeStart", "time", title: "Starting", required: false, refreshAfterSelection:true
			input "A_timeEnd", "time", title: "Ending", required: false, refreshAfterSelection:true
		}
}  
page(name: "timeIntervalInputB", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "B_timeStart", "time", title: "Starting", required: false, refreshAfterSelection:true
			input "B_timeEnd", "time", title: "Ending", required: false, refreshAfterSelection:true
		}
}  
page(name: "timeIntervalInputC", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "C_timeStart", "time", title: "Starting", required: false, refreshAfterSelection:true
			input "C_timeEnd", "time", title: "Ending", required: false, refreshAfterSelection:true
		}
}         

page(name: "timeIntervalInputD", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "D_timeStart", "time", title: "Starting", required: false, refreshAfterSelection:true
			input "D_timeEnd", "time", title: "Ending", required: false, refreshAfterSelection:true
		}
}

page(name: "levelInputA", title: "Set dimmers options...", refreshAfterSelection:true) {
		section {
			input name: "A_levelDimOn", type: "number", title: "On Level", multiple: false, required: false, refreshAfterSelection:true
        	input name: "A_levelDimOff", type: "number", title: "Off Level", multiple: false, required: false, refreshAfterSelection:true
			input name: "A_calcOn",type: "bool",title: "Calculate 'on' level via lux and value above", defaultValue: false
        }
}

page(name: "levelInputB", title: "Set dimmers options...", refreshAfterSelection:true) {
		section {
			input name: "B_levelDimOn", type: "number", title: "On Level", multiple: false, required: false, refreshAfterSelection:true
        	input name: "B_levelDimOff", type: "number", title: "Off Level", multiple: false, required: false, refreshAfterSelection:true
            input name: "B_calcOn",type: "bool",title: "Calculate 'on' level via lux and value above", defaultValue: false
        }
}

page(name: "levelInputC", title: "Set dimmers options...", refreshAfterSelection:true) {
		section {
			input name: "C_levelDimOn", type: "number", title: "On Level", multiple: false, required: false, refreshAfterSelection:true
        	input name: "C_levelDimOff", type: "number", title: "Off Level", multiple: false, required: false, refreshAfterSelection:true
            input name: "C_calcOn",type: "bool",title: "Calculate 'on' level via lux and value above", defaultValue: false
        }
}

page(name: "levelInputD", title: "Set dimmers options...", refreshAfterSelection:true) {
		section {
			input name: "D_levelDimOn", type: "number", title: "On Level", multiple: false, required: false, refreshAfterSelection:true
        	input name: "D_levelDimOff", type: "number", title: "Off Level", multiple: false, required: false, refreshAfterSelection:true
            input name: "D_calcOn",type: "bool",title: "Calculate 'on' level via lux and value above", defaultValue: false
        }
}