/*************************************** PURPOSE **********************************

 - This class implements the WebDriverEventListener, which is included under events.
   The purpose of implementing this interface is to override all the 9 abstract methods 
   and define certain useful Log statements which would be displayed/logged as the application under test
   is being run.
   
   Do not call any of these methods, instead these methods will be invoked automatically
   as an when the action done (click, type etc). 
   
   This would allow us not to log any additional messages which we've used in other utility classes so far.
   
*/

package com.listener;

import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebListener implements WebDriverEventListener
{
	private Logger log = LoggerFactory.getLogger("Weblistener");
    
	
	//Constructor
	public WebListener(){}

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {

    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {

    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {

    }

    public void beforeNavigateTo(String url, WebDriver driver)
    {
    	log.info("Before navigating to url printing the browser associated capabilities");
    }
    
    public void afterNavigateTo(String url, WebDriver driver)
    {
    	log.info("WebDriver has navigated to:'"+url+"'");    		
    }
    
    public void beforeChangeValueOf(WebElement element, WebDriver driver)
    {
    	log.info("Value of the:"+ElementValue(element)+" before any changes made");
    }
 
    public void afterChangeValueOf(WebElement element, WebDriver driver)
    {
    	log.info("Value of element after change: "+ElementValue(element));
    }
    
    public void beforeClickOn(WebElement element, WebDriver driver)
    {
    	log.info("Trying to click on: "+ElementValue(element));
 
    }
    
    public void afterClickOn(WebElement element, WebDriver driver)
    {
    	//log.info("Clicked on: "+ElementValue(element));    	    	
    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver, CharSequence[] charSequences) {

    }

    public void beforeNavigateBack(WebDriver driver)
    {
    	log.info("Navigating back to previous page");
    }
    
    public void afterNavigateBack(WebDriver driver)
    {
    	log.info("Navigated back to previous page");
    }
    
    public void beforeNavigateForward(WebDriver driver)
    {
    	log.info("Navigating forward to next page");
    }
    
    public void afterNavigateForward(WebDriver driver)
    {	
    	log.info("Navigated forward to next page");
    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {

    }


    public void onException(Throwable error, WebDriver driver)
    {
        if (error.getClass().equals(NoSuchElementException.class))
            System.out.print("...");  
        else
        	log.error("WebDriver error:", error);
    }

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> outputType) {

    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> outputType, X x) {

    }

    @Override
    public void beforeGetText(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterGetText(WebElement webElement, WebDriver webDriver, String s) {

    }

    /*
     * non overridden methods of WebListener class
     */
    public void beforeScript(String script, WebDriver driver){}

    public void afterScript(String script, WebDriver driver){}

    @Override
    public void beforeSwitchToWindow(String s, WebDriver webDriver) {

    }

    @Override
    public void afterSwitchToWindow(String s, WebDriver webDriver) {

    }

    public void beforeFindBy(By by, WebElement element, WebDriver driver){}

    public void afterFindBy(By by, WebElement element, WebDriver driver){}
 
    
	public static String ElementValue(WebElement element)
	{
		String sValue = element.toString();
		int iStartindex = sValue.indexOf(">");
		int iEndindex = sValue.lastIndexOf("]");
		String sElementValue = sValue.substring(iStartindex+1,iEndindex);		
		return sElementValue;
	}
         
 
}

