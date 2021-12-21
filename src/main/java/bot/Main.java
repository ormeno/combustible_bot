package bot;

import java.util.Properties;

//import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import res.resourceLoader;


public class Main {
		    
	public static void main(String[] args) {
		
    
	    Properties prop = resourceLoader.loadProperties("configuracion.properties");
	    
        // Initialize Api Context
        //ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        //TelegramBotsApi botsApi = new TelegramBotsApi();
       
        
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Principal bot = new Principal();
            botsApi.registerBot(bot);                   
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("combustible_bot successfully started v.1.2.0 !!!!!!!!!!!");       
    }
}