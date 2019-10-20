//package com.gurock.testrail;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.log4j.Logger;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//
//import com.datamanager.ConfigManager;
//import com.utilities.UtilityMethods;
//
//public class TestRailResultUpdator{
//
//	private APIClient client;
//	private final String MILESTONE=UtilityMethods.getCurrentDateTime();
////	private final String SECTIONTOEXCLUDE="-closed";
//	private final String SEPERATOR ="#";
////	private Map<String, List<String>> map=new HashMap<String, List<String>>();
//	private static ConfigManager sys = new ConfigManager();
//	private static String milestoneId = sys.getProperty("MileStoneID");
//	private static Set<String> runIds = new HashSet<String>();
//	private Logger log = Logger.getLogger("TestRailResultUpdator");
//
//
//	//Constructor to Initialize TestRailResultUpdator and login into test rail account
//	public TestRailResultUpdator(){
//		log.info("Initializing TestRailResultUpdator");
//		client = new APIClient(getServerURL());
//		client.setUser(getClientUserName());
//		client.setPassword(getClientPassword());
//	}
//
//	/**
//	 * Purpose - To get test rail server Url
//	 * @return Test rail Server Url
//	 */
//	private String getServerURL(){
//		return sys.getProperty("TestrailServerUrl");
//	}
//
//	/**
//	 * Purpose - To get test rail user name
//	 * @return Test rail client user name
//	 */
//	private String getClientUserName(){
//		return sys.getProperty("TestrailUsername");
//	}
//
//	/**
//	 * Purpose - To get test rail password
//	 * @return Test rail client password
//	 */
//	private String getClientPassword(){
//		return sys.getProperty("TestrailPassword");
//	}
//
//	/**
//	 * Purpose - To get name of the project
//	 * @return project name
//	 */
//	private String getProjectName(){
//		return sys.getProperty("ProjectName");
//	}
//
//	/**
//	 * Purpose - To get project Id
//	 * @return projectID - Id of the project
//	 */
//	private String getProjectID(){
//		JSONArray response = null;
//		String projectID = null;
//		try {
//			response = (JSONArray)client.sendGet("get_projects");
//			for(int i=0;i<response.size();i++){
//				JSONObject project=(JSONObject) response.get(i);
//				if(project.get("name").equals(getProjectName())){
//					projectID = project.get("id").toString();
//				}
//			}
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		return projectID;
//
//	}
//
//	/**
//	 * Purpose - To create new milestone if milestone Id is empty in sys.properties file
//	 * return milestoneId - Id of milestone
//	 */
//	public String createMilestone(){
//
//		if(sys.getProperty("UpdateResultsToTestRail").equalsIgnoreCase("true")){
//			if(milestoneId=="")
//			{
//				//create a new Milestone
//				milestoneId = createMilestone(getProjectID(), MILESTONE);
//			}
//		}
//		return milestoneId;
//	}
//
//	/**
//	 * Purpose - To get run ids from created milestone
//	 * @return runIds - Set of run ids from created mile stone
//	 */
//	public Set<String> createMilestoneAndAddRuns(){
//		if (runIds.size() == 0) {
//			try {
//				runIds = createOrAddRun(getProjectID(), createMilestone());
//			}catch (IOException | APIException e) {
//				log.error("Failed to get run ids from created milestone, Here is the issue: " + e.getMessage());
//			}
//		}
//		return runIds;
//	}
//
//	/**
//	 * Purpose - To add test case result to test rail
//	 * @param status - pass status id of executed test case
//	 * @param case_id -  pass id of current test case
//	 */
//	public void addResultToTestRail(int status, int case_id)
//	{
//		Map<String, Object> data=new HashMap<String, Object>();
//		//1=Passed, 5-Failed, 6-Skipped
//		if(status==1){
//			data.put("status_id", 1);
//			data.put("comment", "testcase passed");
//		}else if(status==2){
//			data.put("status_id", 2);
//			data.put("comment", "testcase blocked");
//		}else if(status==5){
//			data.put("status_id", 5);
//			data.put("comment", "testcase failed");
//		}else if(status==6){
//			data.put("status_id", 6);
//			data.put("comment", "testcase skipped");
//		}
//		try {
//			boolean flag = false;
//			JSONObject response;
//			for(String runID : runIds){
//				if(getCaseIdsForRun(runID).contains(String.valueOf(case_id))){
//					response = (JSONObject)client.sendPost("add_result_for_case/"+runID+"/"+case_id, data);
//					if(response.get("id")!=null){
//						log.info("Result Added Successfully into TestRail for case ID : "+case_id);
//						flag = true;
//					}
//					if(!flag){
//						log.error("Failed to add the result into TestRail");
//					}
//				}
//			}
//		}
//		catch (IOException | APIException e) {
//			log.error("Failed to add result to testrail, Here is the issue: " + e.getMessage());
//		}
//	}
//
//	/**
//	 * Purpose - To get existed projects in test rail
//	 * @return projects - list of projects existed in test rail
//	 */
//	public Map<String, String> getProjects(){
//		Map<String, String> projects=new HashMap<String, String>();
//		JSONArray response=null;
//		try {
//			response = (JSONArray)client.sendGet("get_projects");
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		for(int i=0;i<response.size();i++){
//			JSONObject project = (JSONObject) response.get(i);
//			projects.put(project.get("id").toString(), project.get("name").toString());
//		}
//		return projects;
//	}
//
//	/**
//	 * Purpose - To create milestone with given project id and milestone name
//	 * @param strProjectId - pass id of the project
//	 * @param strMilestoneName - pass name of milestone
//	 * @return milestone_id - Id of created milestone
//	 */
//	public String createMilestone(String strProjectId, String strMilestoneName){//
//		Map<String, String> data=new HashMap<String, String>();
//		data.put("name", strMilestoneName);
//		JSONObject response=null;
//		try {
//			response = (JSONObject)client.sendPost("add_milestone/"+strProjectId,data);
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		String milestone_id= response.get("id").toString();
//		return milestone_id;
//	}
//
//	/**
//	 * Purpose - To get Suites with given project id
//	 * @param strProjectId - pass id of the project
//	 * @return list of suites
//	 */
//	public List<String> getSuits(String strProjectId){
//		List<String> suites=new ArrayList<String>();
//		JSONArray response=null;
//		try {
//			response = (JSONArray)client.sendGet("get_suites/"+strProjectId);
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		for(int i=0;i<response.size();i++){
//			JSONObject suite=(JSONObject) response.get(i);
//			suites.add(suite.get("id")+SEPERATOR+suite.get("name").toString());
//		}
//		return suites;
//
//	}
//
//	public String getCaseId(String tcName){
//		JSONArray response = null;
//		for (String projectID : getProjects().keySet()) {
//			for (String suite : getSuits(projectID)) {
//				try {
//					response = (JSONArray)client.sendGet("get_cases/"+projectID+"&suite_id="+suite.split(SEPERATOR)[0]);
//				} catch (IOException | APIException e) {
//					log.error(e.getMessage());
//				}
//				for(int i=0;i<response.size();i++){
//					JSONObject cases=(JSONObject) response.get(i);
//					if(cases.get("title").equals(tcName)){
//						return cases.get("id").toString();
//					}
//				}
//			}
//		}
//		return null;
//	}
//
//	public List<String> getSections(String strProjectId, String strSuiteId){
//		JSONArray response=null;
//		List<String> sections=new ArrayList<String>();
//		try {
//			response = (JSONArray)client.sendGet("get_sections/"+strProjectId+"&suite_id="+strSuiteId);
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		for(int i=0;i<response.size();i++){
//			JSONObject jsections=(JSONObject) response.get(i);
//			sections.add(jsections.get("name")+SEPERATOR+jsections.get("id"));
//		}
//		return sections;
//	}
//
//	private List<String> getCases(String strProjectId, String strSuitId, String strSectionId){
//		JSONArray response=null;
//		List<String> caseIds = new ArrayList<String>();
//		try {
//			response = (JSONArray)client.sendGet("get_cases/"+strProjectId+"&suite_id="+strSuitId+"&section_id="+strSectionId);
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		for(int i=0;i<response.size();i++){
//			JSONObject sections=(JSONObject) response.get(i);
//			caseIds.add(sections.get("id").toString());
//		}
//		return caseIds;
//
//	}
//
//	private List<String> getRuns(String strProjectId, String strMilestoneId){//get_tests
//		JSONArray response=null;
//		List<String> runIds = new ArrayList<String>();
//		try {
//			response = (JSONArray)client.sendGet("get_runs/"+strProjectId);
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		for(int i=0;i<response.size();i++){
//			JSONObject sections=(JSONObject) response.get(i);
//			Object milestone_id=sections.get("milestone_id");
//			if(milestone_id!=null){
//				if(sections.get("milestone_id").toString().equals(strMilestoneId)){
//					runIds.add(sections.get("id").toString());
//				}
//			}
//
//		}
//		return runIds;
//	}
//
//	private List<String> getCaseIdsForRun(String strRunId){
//		List<String> caseIds = new ArrayList<String>();
//		JSONArray response=null;
//		try {
//			response = (JSONArray)client.sendGet("get_tests/"+strRunId);
//		} catch (IOException | APIException e) {
//			log.error(e.getMessage());
//		}
//		for(int i=0;i<response.size();i++){
//			JSONObject obj=(JSONObject) response.get(i);
//			caseIds.add(obj.get("case_id").toString());
//		}
//		return caseIds;
//	}
//
//	/**
//	 * Purpose - To create a new test run to milestone
//	 * @param strRunName - Run name of new test run
//	 * @param strProjectId - The ID of the project the test run should be added to
//	 * @param milestoneId - The ID of the milestone to link to the test run
//	 * @param strInclideAll - True for including all test cases of the test suite and false for a custom case selection
//	 * @param strSuiteId - The ID of the test suite for the test run
//	 * @param strSectionId - The ID of the section
//	 * @param testCaseIds - The list of case IDs
//	 * @return run_id - Id of created new Run
//	 */
//	private String addRun(String strRunName, String strProjectId, String milestoneId, String strInclideAll, String strSuiteId, String strSectionId, List<String> testCaseIds){
//		//POST index.php?/api/v2/add_run/:project_id
//		String run_id = null;
//		try{
//			// adding a test run
//			Map<String, Object> data = new HashMap<String, Object>();
//			data.put("name", strRunName);
//			data.put("milestone_id",milestoneId);
//			data.put("include_all", strInclideAll);// 0 means false, 1 means true( if 1 is given then no need to put "case_ids")
//			data.put("case_ids",testCaseIds);
//			data.put("suite_id", strSuiteId);
//			if(strSectionId!=null)
//				data.put("section_id", strSectionId);
//			JSONObject response = (JSONObject) client.sendPost("add_run/"+strProjectId,data);
//			run_id = response.get("id").toString();
//		}catch (IOException | APIException e) {
//			log.error("Failed to add test run, Here is the issue: " + e.getMessage());
//		}
//		return run_id;
//	}
//
//    /**
//     * Purpose - To create or Add run to milestone in test rail
//     * @param strProjectId - The ID of the project
//     * @param milestoneId - The ID of the milestone to link to the test run
//     * @return runIds - set of run ids
//     * @throws MalformedURLException
//     * @throws IOException
//     * @throws APIException
//     */
//	public Set<String> createOrAddRun(String strProjectId, String milestoneId) throws MalformedURLException, IOException, APIException{
//		boolean flag = false;
//		String runsNeeddToAdd = sys.getProperty("RunID");
//		List<String> existingRuns = getRuns(strProjectId, milestoneId);
//		if (milestoneId!=null && existingRuns.size()>0 && runsNeeddToAdd!="") {
//			for (int i=0; i < runsNeeddToAdd.split(",").length; i++) {
//				String suppliedRun = runsNeeddToAdd.split(",")[i];
//				for(String existingRun : existingRuns){
//					if(existingRun.equals(suppliedRun)){
//						flag=true;
//						break;
//					}
//				}
//				if(!flag){
//				runIds.add(suppliedRun);
//				Map<String, Object> data = new HashMap<String, Object>();
//				data.put("milestone_id",milestoneId);//6
//				JSONObject response = (JSONObject) client.sendPost("update_run/"+suppliedRun,data);
//				log.info("Added Run : "+suppliedRun+" to milestone : "+milestoneId);
//				}
//				else{
//					flag=false;
//				}
//			}
//			runIds.addAll(existingRuns);
//			return runIds;
//		} else {
//			for (String suite : getSuits(strProjectId)) {
//				List<String> caseIds=new ArrayList<String>();
//				for (String section : getSections(strProjectId, suite.split(SEPERATOR)[0])) {
//						caseIds.addAll(getCases(strProjectId, suite.split(SEPERATOR)[0], section.split(SEPERATOR)[1]));
//				}
//				runIds.add(addRun(suite.split(SEPERATOR)[1], strProjectId, milestoneId, "0", suite.split(SEPERATOR)[0], null, caseIds));
//			}
//
//		}
//		return runIds;
//	}
//}
//
//
//
//
////private String isMilestoneCreated(String strProjectId){
////for(String milestone :getMilestones(strProjectId)){
////	String name=milestone.split("#")[1];
////	if(name.endsWith(getCurrentDateTime(0))||name.endsWith(getCurrentDateTime(-1))||name.endsWith(getCurrentDateTime(-2))){
////		return milestone.split("#")[0];
////
////	}
////}
////return null;
////}
//
////public void createRuns(String... projectList) {
////Map<String, String> projects = getProjects();
////for (String project : projects.keySet()){
////	if(Arrays.asList(projectList).get(0).contains(projects.get(project)))
////	{
////		String milestoneId=isMilestoneCreated(project);
////		if (milestoneId!=null) {
////			for(String runId : getRuns(project, milestoneId)){
////				map.put(runId, getCaseIdsForRun(runId));
////			}
////		} else {
////			for (String suite : getSuits(project)) {
////				List<String> caseIds=new ArrayList<String>();
////				for (String section : getSections(project, suite.split(SEPERATOR)[0])) {
////					if (!section.split(SEPERATOR)[0].toLowerCase().endsWith(SECTIONTOEXCLUDE)) {
////						caseIds.addAll(getCases(project, suite.split(SEPERATOR)[0], section.split(SEPERATOR)[1]));
////					}
////				}
////				caseIds=null;
////			}
////		}
////	}
////}
////}
//
////public void addResultsFoeCase_InTestRail(int status, int case_id){
////
////	for(String key : map.keySet()){
////		if(map.get(key).contains(String.valueOf(case_id))){
////		}
////	}
////}
//
////private String getCurrentDateTime(int backDay)
////{
////	//log.info("Get current date");
////	Calendar currentDate = Calendar.getInstance();
////	DateFormat formatter= new SimpleDateFormat("MMddYYY");
////	currentDate.add(Calendar.DATE, backDay);
////	String dateNow = formatter.format(currentDate.getTime());
////	return dateNow;
////
////}
//
//
////public String isMilestoneCreated(String strProjectId, String strMilestoneName){
////	for(String milestone :getMilestones(strProjectId)){
////		String name=milestone.split("#")[1];
////		if(name.equals(strMilestoneName.trim())){
////			return milestone.split("#")[0];
////		}
////	}
////	return null;
////}
//
////private List<String> getMilestones(String strProjectId){
////	List<String> milestones=new ArrayList<String>();
////	JSONArray response=null;
////	try {
////		response = (JSONArray)client.sendGet("get_milestones/"+strProjectId);
////	} catch (IOException | APIException e) {
////		log.error(e.getMessage());
////	}
////	for(int i=0;i<response.size();i++){
////		JSONObject suite=(JSONObject) response.get(i);
////		milestones.add(suite.get("id")+SEPERATOR+suite.get("name").toString());
////	}
////	return milestones;
////}