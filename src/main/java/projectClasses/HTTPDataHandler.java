/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectClasses;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author Yoana
 */
public class HTTPDataHandler 
{
   
    public String stream = null;
    public String responseBody = "";
    public static final String URL = "http://systemintegrationrulebase.azurewebsites.net/api/rulebase/getsimplerulebase?creditscore=100";

    public HTTPDataHandler()
    {
         
    }

        public String GetHTTPData()
    {
        try
        {
            
                URL url = new URL(URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // Check the connection status
                if(urlConnection.getResponseCode() == 200)
                {
                    
                    InputStream response = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(response);
                    //{
                          responseBody = scanner.next();
                         System.out.println("the answer is "+responseBody);
                    //}
                    
//                    // if response code = 200 ok
//                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//                    // Read the BufferedInputStream
//                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder sb = new StringBuilder();
//                    String line;
////                    while ((line = r.readLine()) != null)
////                    {
////                        System.out.println("The line is "+line);
////                        sb.append(line);
////                    }
//                  
//
//    //                Log.d("THE JSON IS ",sb.toString());
//
//                    stream = sb.toString();
//               
//                    urlConnection.disconnect();
                    
                    
                    
            }
            else
            {
                // Do something
            }
            
        }catch (MalformedURLException e)
        {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally 
        {

        }
        // Return the data from specified url
       // System.out.println("The string from the rule base is "+stream);
       // return stream;
        System.out.println("the answer is now"+responseBody);
       return responseBody;
        // return sb;
    }
    
    
    
    
//    public String GetHTTPData(String urlString)
//    {
//        try
//        {
//            
//            URL url = new URL(urlString);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            // Check the connection status
//            if(urlConnection.getResponseCode() == 200)
//            {
//                // if response code = 200 ok
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//                // Read the BufferedInputStream
//                BufferedReader r = new BufferedReader(new InputStreamReader(in));
//                StringBuilder sb = new StringBuilder();
//                String line;
//                while ((line = r.readLine()) != null)
//                {
//                    sb.append(line);
//                }
//
////                Log.d("THE JSON IS ",sb.toString());
//
//                stream = sb.toString();
//                //stream = sb.toString();
//                // End reading...............
//
//                // Disconnect the HttpURLConnection
//                urlConnection.disconnect();
//            }
//            else
//            {
//                // Do something
//            }
//            
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        }catch(IOException e){
//            e.printStackTrace();
//        }finally {
//
//        }
//        // Return the data from specified url
//        return stream;
//        // return sb;
//    }
      
}
