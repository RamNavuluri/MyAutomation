package com.listener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import ru.yandex.qatools.allure.annotations.Attachment;

import com.datamanager.ConfigManager;
//import com.gurock.testrail.TestRailResultUpdator;
import com.testng.Assert;
import com.utilities.ReportSetup;
import com.utilities.ScreenCapture;
import com.utilities.UtilityMethods;

public class TestListener extends TestListenerAdapter implements ISuiteListener
{

	private  static char cQuote = '"';
	ConfigManager sys = new ConfigManager();
	ConfigManager depend = new ConfigManager("TestDependency");
	private  static String fileSeperator = System.getProperty("file.separator");
	Logger log = LoggerFactory.getLogger("TestListener");
	private final String PASSED="PASSED";
	private final String FAILED="FAILED";
	private final String SKIPPED="SKIPPED";
	private final String DASHBOARD_DIR=System.getProperty("user.dir")+fileSeperator+"Dashboard";
//	TestRailResultUpdator testRailResultUpdator = new TestRailResultUpdator();
	private static boolean milestoneCreated = false;

	/**
	 * This method will be called if a test case is failed. 
	 * Purpose - For attaching captured screenshots and videos in ReportNG report 
	 */
	public void onTestFailure(ITestResult result)
	{
		depend.writeProperty(result.getName(),"Fail");

		log.error("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n" );
		log.error("ERROR ----------"+result.getName()+" has failed-----------------" );
		log.error("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n" );
		ITestContext context = result.getTestContext();
		WebDriver driver = (WebDriver)context.getAttribute("driver");
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		Reporter.setCurrentTestResult(result);
		String id = getCaseId(result.getName());
		updateToTestRail(id, result.getName(), 5);
		String imagepath = ".." + fileSeperator+"Screenshots" + fileSeperator + ScreenCapture.saveScreenShot(driver);
		createAttachment(driver);
		Reporter.log("<a href="+cQuote+imagepath+cQuote+">"+" <img src="+cQuote+imagepath+cQuote+" height=48 width=48 ></a>");
//		ScreenCapture.stopVideoCapture(result.getName());
		updateRuntimeReport(result, Thread.currentThread().getId(), FAILED);
		UtilityMethods.verifyPopUp();
		String sValue = new ConfigManager().getProperty("VideoCapture");
		String sModeOfExecution = new ConfigManager().getProperty("ModeOfExecution");
		if(sValue.equalsIgnoreCase("true") && sModeOfExecution.equalsIgnoreCase("linear"))
		{
			String sVideoPath = null;
			sVideoPath = testCaseVideoRecordingLink(result.getName());
			Reporter.log("<a href="+cQuote+sVideoPath+cQuote+" style="+cQuote+"text-decoration: none; color: white;"+cQuote+"><div class = cbutton>Download Video</div></a>");
			Reporter.log("<font color='Blue' face='verdana' size='2'><b>"+Assert.doAssert()+"</b></font>");
			//			Reporter.log("<a color='Blue' face='verdana' size='2'><b>"+Assert.doAssert()+"</b></a>");
		}
		Reporter.setCurrentTestResult(null);

	}

	/**
	 * Method to capture screenshot for allure reports
	 * @param driver , need to pass the driver object
	 * @return , returns the captured image file in the form of bytes
	 */
	@Attachment(value="Screenshot",type = "image/png")
	private byte[] createAttachment(WebDriver driver)
	{
		try
		{
			return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		}
		catch(Exception e)
		{
			log.error("An exception occured while saving screenshot of current browser window from createAttachment method.."+e.getCause());
			return null;
		}
	} 

	public void test()
	{

	}

	/**
	 * This method will be called if a test case is skipped. 
	 * 
	 */
	public void onTestSkipped(ITestResult result)
	{			
		log.warn("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" );
		log.warn("WARN ------------"+result.getName()+" has skipped-----------------" );
		log.warn("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" );			
		updateRuntimeReport(result, Thread.currentThread().getId(), SKIPPED);
		depend.writeProperty(result.getName(),"Skip");

		//************* comment below code if you are using TestNG dependency methods
		String id = getCaseId(result.getName());
		updateToTestRail(id, result.getName(), 6);
		Reporter.setCurrentTestResult(result);
//		ScreenCapture.stopVideoCapture(result.getName());
		UtilityMethods.verifyPopUp();
		Reporter.setCurrentTestResult(null);
	}

	/**
	 * This method will be called if a test case is passed. 
	 * Purpose - For attaching captured videos in ReportNG report 
	 */
	public void onTestSuccess(ITestResult result)
	{
		depend.writeProperty(result.getName(),"Pass");
		System.setProperty("org.uncommons.reportng.escape-output", "false");
		log.info("###############################################################" );
		log.info("SUCCESS ---------"+result.getName()+" has passed-----------------" );
		log.info("###############################################################" );
		String id = getCaseId(result.getName());
		updateToTestRail(id, result.getName(), 1);
		Reporter.setCurrentTestResult(result);
//		ScreenCapture.stopVideoCapture(result.getName());
		UtilityMethods.verifyPopUp();
		updateRuntimeReport(result, Thread.currentThread().getId(), PASSED);
		String sValue = new ConfigManager().getProperty("VideoCapture");
		String sModeOfExecution = new ConfigManager().getProperty("ModeOfExecution");
		if(sValue.equalsIgnoreCase("true")&&sModeOfExecution.equalsIgnoreCase("linear"))
		{
			String sVideoPath = testCaseVideoRecordingLink(result.getName());
			Reporter.log("<a href="+cQuote+sVideoPath+cQuote+" style="+cQuote+"text-decoration: none; color: white;"+cQuote+"><div class = cbutton>Download Video</div></a>");
		}
		Reporter.setCurrentTestResult(null);
	}

	/**
	 * This method will be called before a test case is executed. 
	 * Purpose - For starting video capture and launching balloon popup in ReportNG report 
	 */
	public void onTestStart(ITestResult result)
	{
		log.info("<h2>**************CURRENTLY RUNNING TEST************ "+result.getName()+"</h2>" );
		createFile(Thread.currentThread().getId());
//		ScreenCapture.startVideoCapture();
		UtilityMethods.currentRunningTestCaseBalloonPopUp(result.getName());
	}

	public void onStart(ITestContext context) 
	{

	}

	public void onFinish(ITestContext context) 
	{
		Iterator<ITestResult> failedTestCases = context.getFailedTests().getAllResults().iterator();
		while (failedTestCases.hasNext())
		{
			ITestResult failedTestCase = failedTestCases.next();
			ITestNGMethod method = failedTestCase.getMethod();            
			if (context.getFailedTests().getResults(method).size() > 1)
			{
				if(sys.getProperty("KeepFailedResult").equalsIgnoreCase("false")){
					//log.info("failed test case remove as dup:" + failedTestCase.getTestClass().toString());
					failedTestCases.remove(); 
				}
			}
			else
			{	                
				if (context.getPassedTests().getResults(method).size() > 0)
				{
					if(sys.getProperty("KeepFailedResult").equalsIgnoreCase("false")){
						//log.info("failed test case remove as pass retry:" + failedTestCase.getTestClass().toString());
						failedTestCases.remove();
					}	                    
				}                          
			}            
		}
	}

	/**
	 * 
	 * To identify the latest captured screenshot
	 *
	 * @return
	 */
	public String capturedScreenShot()
	{

		File mediaFolder=new File(ReportSetup.getImagesPath());
		File[] files = mediaFolder.listFiles();
		Arrays.sort( files, new Comparator<Object>()
				{
			public int compare(Object o1, Object o2) {
				//return new Long(((File)o1).lastModified()).compareTo(new Long(((File)o2).lastModified())); // for ascending order
				return -1*(new Long(((File)o1).lastModified()).compareTo(new Long(((File)o2).lastModified()))); //for descending order 
			}
				});
		return files[0].getName();
	}

	/**
	 * 
	 * This method is used to rename the captured video with test case name
	 *
	 * @param tname , Need to pass the test case name
	 * @return, Returns the captured video path name
	 */
	public  String testCaseVideoRecordingLink(String tname)
	{	
		String sVideoPath = ".." + fileSeperator + "Videos" + fileSeperator + tname + "(1).avi";		
		if(new File(ReportSetup.getVideosPath()+fileSeperator+tname+"(1).avi").exists())
		{			
			return sVideoPath;
		}
		else
		{
			String sVideoPath2 = sVideoPath.substring(0,sVideoPath.length()-7)+".avi";
			return sVideoPath2;
		}
	}

	private void updateRuntimeReport(ITestResult result, long theadId, String strStatus){
		int count=0;
		int dpmethodcount=0;
		StringBuffer param=new StringBuffer();
		String p="";
		param.append("(");
		ConfigManager rs=new ConfigManager(getFilePath(theadId));
		Object[] params = result.getParameters();
		for(int i=0; i<params.length; i++){
			param.append(params[i]+",");
		}
		//		param.append(")");
		if(param.length()>2){
			count++;
			p=param.substring(0, param.length()-2)+")".toString();
		}
		else {
			count=0;
		}
		rs.writeToDashboard(result.getMethod().getRealClass().getName()+"-"+result.getName()+p, strStatus);

		rs=null;
	}

	@Override
	public void onFinish(ISuite arg0) {
		// TODO Auto-generated method stub
		ConfigManager suiteFile=new ConfigManager(getFilePath(Thread.currentThread().getId()));
		suiteFile.writeToDashboard("SUITE_STATUS", "COMPLETED");

	}

	@Override
	public void onStart(ISuite iSuite) {
		for(File file : UtilityMethods.fileList(DASHBOARD_DIR, ".properties")){
			file.delete();
		}
		File dir=new File(DASHBOARD_DIR);
		if(!dir.exists())
		{
			dir.mkdir();
		}
		createFile(Thread.currentThread().getId());
		ConfigManager suiteFile=new ConfigManager(getFilePath(Thread.currentThread().getId()));
		suiteFile.writeToDashboard("TOTAL_TEST_SCRIPTS", String.valueOf(iSuite.getAllMethods().size()));
		suiteFile.writeToDashboard("SUITE_NAME", iSuite.getName());
		if(!milestoneCreated){
//			testRailResultUpdator.createMilestoneAndAddRuns();
			milestoneCreated = true;
		}
	}

	private String getFilePath(long ld){
		return DASHBOARD_DIR+fileSeperator+ld;
	}

	private void createFile(long ld) {
		File file=new File(getFilePath(ld)+".properties");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Can not create file in specified path : "+file.getAbsolutePath()+UtilityMethods.getStackTrace());
				Assert.fail("Can not create file in specified path : "+file.getAbsolutePath()+e.getMessage());
			}
			file=null;
		}
	}

	/**
	 * 
	 * This method is used to rename the captured video with test case name
	 *
	 * @param tname , Need to pass the test case name
	 * @param sessionId, Need to pass the remote webdriver session id
	 * @return tcNameVideo, Returns the captured video path name
	 */
	public String testCaseGridVideoRecordingLink(String tname, String sessionId)
	{
		String sessionVideo = ReportSetup.getVideosPath()+fileSeperator + sessionId + ".mp4";
		File sessionVideoFile = new File(sessionVideo);
		String tcNameVideo = ReportSetup.getVideosPath()+fileSeperator + tname + ".mp4";
		File tcNameFile = new File(tcNameVideo);
		if(sessionVideoFile.renameTo(tcNameFile)) {
			log.info("renamed");
		} else {
			log.error("Error - File is not renamed");
		}
		if(tcNameFile.exists()){
			return tcNameVideo;
		}else{
			log.error("Error - File is not found");
		}
		return tcNameVideo;
	}

	
	
	
	private void updateToTestRail(String id, String testName, int testStatus) {
		if (sys.getProperty("UpdateResultsToTestRail").equalsIgnoreCase("true")) {
//					testRailResultUpdator.addResultToTestRail(testStatus, Integer.parseInt(id));
		}
	}
	
	private String getCaseId(String strTestName){
		if(sys.getProperty("UpdateResultsToTestRail").equalsIgnoreCase("true")){
			String[] arr = strTestName.split("_");
			return arr[arr.length-2];
		}
		else
			return null;
		
	}

}
