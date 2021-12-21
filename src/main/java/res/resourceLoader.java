package res;

import java.io.IOException;
import java.util.Properties;

public class resourceLoader {

	
	public static Properties loadProperties(String propName)
    {
		Properties prop = new Properties();	
		try {
			prop.load(resourceLoader.class.getResourceAsStream(propName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prop;
	}
	

}
