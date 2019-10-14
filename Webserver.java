import java.io.*;
import java.net.* ;
import java.util.* ;

public final class Webserver
{
    private static final int BUFFER_SIZE = 32768;

	
	public static void main(String args[]) throws IOException
	{
		
		ServerSocket server = new ServerSocket(8080); 
		System.out.println("Listening for connection on port 8080...");
		System.out.println();
		
		while(true)
		{
			Socket clientSocket = server.accept();
			
			InputStreamReader in = new InputStreamReader(clientSocket.getInputStream());
			BufferedReader out = new BufferedReader(in);
            DataOutputStream outData =new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader rd = null;

			
			//print HTTP request
			String urlToCall = "";
			String line = out.readLine();
            StringTokenizer tok = new StringTokenizer(line);
            String[] tokens = line.split(" ");
            urlToCall = tokens[1];

			while(!line.isEmpty())
			{
				System.out.println(line);
				line = out.readLine(); 
			}
			
			//send HTTP response
			//Date current = new Date(); 
			//String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + current; 
			//clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			
			URL url;
            url = new URL(urlToCall); 
            
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            InputStream is = null;
            
            conn.getResponseCode();
            boolean cacheHit=false;
        	File file = new File("url.txt");

        	File cache=new File("Cache.txt");
        	try 
        	{
        		//check to see if the website was cached
        	    Scanner scanner = new Scanner(file);
        	    if(scanner.hasNextLine())
        	    {
            	    String cacheURL=scanner.nextLine();
            	    if(cacheURL.equals(urlToCall))
            	    {
            	    	cacheHit=true;
            	    }
        	    }
        	    scanner.close();

        	} 
        	catch(FileNotFoundException e)
        	{ 
        	    System.out.println("File was not found");
        	}
        	
        	System.out.println(conn.getContentLength()); 
        	
            if (conn.getContentLength() > 0)
            {

            	try 
                {
                	if(cacheHit)
                	{
                		//uses cache
                  		FileInputStream fis = new FileInputStream(cache);
                		byte[] data = new byte[(int) cache.length()];
                		fis.read(data);
                		fis.close();
                		outData.write(data);
                   	}
                    is = conn.getInputStream();
                	rd = new BufferedReader(new InputStreamReader(is));
                	System.out.println(cacheHit);
                } 
                catch (IOException ioe)
                {
                	System.out.println("404 not found");
                    System.out.println(	"********* IO EXCEPTION **********: " + ioe);
                    outData.writeBytes("HTTP/1.1 404 NOT FOUND" + "\r\n");
                }
                
            }
			
			 byte by[] = new byte[ BUFFER_SIZE ];
             int index = is.read( by, 0, BUFFER_SIZE );
             
             String s = new String(by); 

             //write url and data to cache
             PrintWriter writer = new PrintWriter("Cache.txt");
             writer.println(urlToCall);
             writer.println(s);

             writer.close();
             //output to client
             while (index != -1)
             {
               outData.write( by, 0, index );
               index = is.read( by, 0, BUFFER_SIZE );
             }
             
             
             //read the result from the server
             //InputStream rdData = conn.getInputStream();
             
             Map<String, List<String>> map = conn.getHeaderFields();

            System.out.println();
            System.out.println(); 
         	System.out.println("-------RESPONSE--------\n");

         	for (Map.Entry<String, List<String>> entry : map.entrySet())
         	{
         		System.out.println(entry.getKey() + " : " +entry.getValue());
         	}

         	System.out.println("\nGet Response Header By Key ...\n");
         	String webserver = conn.getHeaderField("Server");

         	if (webserver == null)
         	{
         		System.out.println("Key 'Server' is not found!");
         	}
         	else
         	{
         		System.out.println("Server - " + server);
         	}

         	System.out.println("\n Done");
              
             
             outData.flush();
             
             
             if (rd != null)
             {
                 rd.close();
             }
             if (out != null)
             {
                 out.close();
             }
             if (in != null) 
             {
                 in.close();
             }
             if (clientSocket != null)
             {
                 clientSocket.close();
             }

			
		}
		
	}
	
}