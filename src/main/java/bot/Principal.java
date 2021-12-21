package bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.vdurmont.emoji.EmojiParser;

import bot.bo.ConsultaGas;
import bot.bo.Gasolineras;
import res.resourceLoader;



public class Principal extends TelegramLongPollingBot {
		
	
	protected Principal(DefaultBotOptions botOptions) {
        super(botOptions);
    }

	public Principal() {
		// TODO Auto-generated constructor stub
	}
	public static int responseCode = 0;
	public static String responseString = "";
	
	CallbackQuery respuesta = new CallbackQuery();    
	ForwardMessage f = new ForwardMessage();

	
    @SuppressWarnings("unused")
	@Override
    public void onUpdateReceived(Update update) {        	    
    	Properties prop = resourceLoader.loadProperties("configuracion.properties");	 
    	// We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {            
        	// Set variables        	
            String message_text = update.getMessage().getText();
            String user_username = update.getMessage().getChat().getUserName();            
            long user_id = update.getMessage().getChat().getId();
            long chat_id = update.getMessage().getChatId();              
			long idUsuAdm = Long.parseLong(prop.getProperty("var.userAdm"));
			
			if (message_text.toUpperCase().equals("/CREARBD")) {
				 GestionCrearBd(chat_id, user_id, idUsuAdm);	         	   	            				    
			} else if (message_text.toUpperCase().equals("/DATOS")) {
				GestionLeerBd(chat_id, user_id, idUsuAdm);	   	            				    
			} else {

        	 String msgPrincipal = "En @SurtidorBot podrá consultar el precio de las gasolineras más cercanas a ti. \n";
        	 msgPrincipal = msgPrincipal.concat("\n");
        	 msgPrincipal = msgPrincipal.concat("Datos procedentes del portal de Datos Abiertos del Gobierno de España. \n");
        	 msgPrincipal = msgPrincipal.concat("\n");
        	 msgPrincipal = msgPrincipal.concat("Consulta los precios con el botón PRECIO COMBUSTIBLE \n");
        	 SendMessage message = new SendMessage();
        	 message.setChatId(Long.toString(chat_id));      
        	 message.setParseMode(ParseMode.HTML);
		     message.setText(msgPrincipal);       
		     ReplyKeyboardMarkup keyboardMarkup = botoneraPrincipal();
	         message.setReplyMarkup(keyboardMarkup);	
			 try {
                execute(message); 
              } catch (TelegramApiException e) {
                e.printStackTrace();
	          }   
			}
        } else if (update.hasMessage() && update.getMessage().hasLocation()) {
     	   long chat_id = update.getMessage().getChatId();
     	   String user_username = update.getMessage().getChat().getUserName();
           long user_id = update.getMessage().getChat().getId();
           double longitud = update.getMessage().getLocation().getLongitude();
           double latitud = update.getMessage().getLocation().getLatitude();
           //SendMessage msg = new SendMessage().setChatId(chat_id).setParseMode(ParseMode.HTML);
           SendMessage msg = new SendMessage();
           msg.setChatId(Long.toString(chat_id)); 
           msg.setParseMode(ParseMode.HTML);
           String texto = "<b> Indica tipo de combustible </b>";
      	   InlineKeyboardMarkup markupInline = botoneraCombustibles(longitud, latitud);	
           msg.setReplyMarkup(markupInline);
           msg.setText(texto);  
           try {            
               execute(msg);            
            } catch (TelegramApiException e) {
                        e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
        	String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            long user_id = update.getCallbackQuery().getFrom().getId();     
            int tipoProducto = Integer.parseInt(call_data.substring(0,call_data.indexOf("#")));
            String tipoCombus = call_data.substring(call_data.indexOf("&")+1,call_data.indexOf("Long_"));
            double longitud = Double.parseDouble(call_data.substring(call_data.indexOf("Long_")+5,call_data.indexOf("_Lat_")));
            double latitud = Double.parseDouble(call_data.substring(call_data.indexOf("_Lat_")+5,call_data.length()));
            if (call_data.contains("#C")) {
            	SendMessage msg = new SendMessage();
            	msg.setChatId(Long.toString(chat_id));
            	msg.setParseMode(ParseMode.HTML);
                String texto = "<b> Indica la distancia </b>";
           	    InlineKeyboardMarkup markupInline = botoneraDistancia(longitud, latitud,tipoProducto,tipoCombus);	
                msg.setReplyMarkup(markupInline);
                msg.setText(texto);  
                try {            
                    execute(msg);            
                 } catch (TelegramApiException e) {
                             e.printStackTrace();
                 }	            		             	
            } else if (call_data.contains("#D")) {
            	SendMessage msg = new SendMessage();
            	msg.setChatId(Long.toString(chat_id));
            	msg.setParseMode(ParseMode.HTML);
                String texto = "<b> Indica el orden en el que aparezcan los datos </b>";
                int disPedida = Integer.parseInt(call_data.substring(call_data.indexOf("d_")+2,call_data.indexOf("&")));
           	    InlineKeyboardMarkup markupInline = botoneraOrden(longitud, latitud,tipoProducto,tipoCombus, disPedida);	
                msg.setReplyMarkup(markupInline);
                msg.setText(texto);  
                try {            
                    execute(msg);            
                 } catch (TelegramApiException e) {
                             e.printStackTrace();
                 }
            } else {
	            double lon2 = 0;
	            double lat2 = 0;
	            int disPedida = Integer.parseInt(call_data.substring(call_data.indexOf("d_")+2,call_data.indexOf("&")));
	            int orden = Integer.parseInt(call_data.substring(call_data.indexOf("#O")+2,call_data.indexOf("d_")));
	            String ballEmoji = EmojiParser.parseToUnicode(":small_blue_diamond:");
	            String squareEmoji = EmojiParser.parseToUnicode(":white_small_square:");
	            String fuelEmoji = EmojiParser.parseToUnicode(":fuelpump:");
	            String moneyEmoji = EmojiParser.parseToUnicode(":moneybag:");
	            String chinchetaEmoji = EmojiParser.parseToUnicode(":round_pushpin:");
	            String relojEmoji = EmojiParser.parseToUnicode(":clock8:");
	            ArrayList<Gasolineras> gasolineras = new ArrayList<Gasolineras>();
	            String user_username = update.getCallbackQuery().getFrom().getUserName();
	            OkHttpClient client = new OkHttpClient();
	            try {
	         	String urlResults = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProducto/" + tipoProducto;
	       	   //String urlResults = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/ProductosPetroliferos/";
	   	    	Request request = new Request.Builder()
	   				.url(urlResults)
	   				.get()
	   				.build();
	   	       Response response = client.newCall(request).execute();
			   if ((responseCode = response.code()) == 200) {
	               // Get response
	               String jsonRespuestaWS = response.body().string();
	               
	               JSONObject output;
	               output = new JSONObject(jsonRespuestaWS);
	               SendMessage mensajeLista = new SendMessage();
	               mensajeLista.setChatId(Long.toString(chat_id));
	               mensajeLista.setParseMode(ParseMode.HTML);
	               String textoLista = null;
	               String fecha = output.getString("Fecha");
	               String nota = output.getString("Nota");
	               String res = output.getString("ResultadoConsulta");
	               JSONArray results = output.getJSONArray("ListaEESSPrecio");
	               String latEESS=null;
	               String longEESS=null;
	               // 
	               database db = new database();
	               db.actuContadores();

	               for (int i = 0; i < results.length(); i++){
	               	 JSONObject eess = results.getJSONObject(i);
	                 //
	               	 latEESS= eess.getString("Latitud");
	               	 lat2 = Double.parseDouble(latEESS.replace(",","."));               	 
	               	 longEESS = eess.getString("Longitud (WGS84)");
	               	 lon2 = Double.parseDouble(longEESS.replace(",","."));
	                 //
	               	 double dis = distanciaCoord( latitud,  longitud,  lat2,  lon2);
	               	 if (dis<disPedida) {
	               		Gasolineras gasolinera = new Gasolineras();
	               		gasolinera.setLatEESS(latEESS);
	               		gasolinera.setLongEESS(longEESS);
		               	gasolinera.setPrecioEESS(eess.getString("PrecioProducto"));
		               	gasolinera.setDistancia(dis);
		               	gasolinera.setRotulo(eess.getString("Rótulo"));
		               	gasolinera.setDireccion(eess.getString("Dirección"));
		               	gasolinera.setMunicipio(eess.getString("Municipio"));
		               	gasolinera.setLocalidad(eess.getString("Localidad"));
		               	gasolinera.setCodPostal(eess.getString("C.P."));
		               	gasolinera.setHorario(eess.getString("Horario"));
		               	gasolinera.setMargen(eess.getString("Margen"));
		               	gasolineras.add(gasolinera);
	               	 }
	               }
	               textoLista = "<b> GASOLINERAS A " +disPedida+" KM (limitado a las 20 primeras) </b> \n";
	               textoLista = textoLista.concat("\n");
	               textoLista = textoLista.concat(ballEmoji + nota.substring(nota.indexOf(".")+1,nota.length()) + " \n");
	               if (orden==1) {
	            	   gasolineras.sort(new precSorter());   
	               } else {
	            	   gasolineras.sort(new disSorter());  
	               }
	               DecimalFormat df = new DecimalFormat("0.000");
	               if (gasolineras.isEmpty()) {
	            	  String texto = "<b> No hay gasolineras en las distancia indicada </b>";
	            	  ReplyKeyboardMarkup keyboardMarkup = botoneraPrincipal();
	 				  mensajeLista.setReplyMarkup(keyboardMarkup);
	 				  mensajeLista.setText(texto);  
	 	              try {            
	 	                  execute(mensajeLista);            
	 	              } catch (TelegramApiException e) {
	 	                             e.printStackTrace();
	 	              }	
	               } else {
		               for (int j = 0; j < gasolineras.size(); j++) {
		              	 textoLista = textoLista.concat("\n");
		                 textoLista = textoLista.concat(fuelEmoji + " <b>" + gasolineras.get(j).getRotulo().toUpperCase() + "</b> - Distancia: " + df.format(gasolineras.get(j).getDistancia())+ "Km \n");
		                 textoLista = textoLista.concat(moneyEmoji + " <b>" + tipoCombus + ":</b> " + gasolineras.get(j).getPrecioEESS() + "â‚¬ \n");
		                 String destino = gasolineras.get(j).getDireccion() + "," + gasolineras.get(j).getCodPostal() +"," + gasolineras.get(j).getMunicipio() +"," + gasolineras.get(j).getLocalidad();
		                 textoLista = textoLista.concat(chinchetaEmoji +" <a href='https://www.google.com/maps/dir/?api=1&destination=" + URLEncoder.encode(destino,"UTF-8") + "&travelmode=driving'>"+ gasolineras.get(j).getDireccion() + " - " + gasolineras.get(j).getMunicipio() + "</a> (Margen: " + gasolineras.get(j).getMargen() + ")\n");
		                 textoLista = textoLista.concat(relojEmoji + " " + gasolineras.get(j).getHorario() + " \n");
		                 textoLista = textoLista.concat("\n");
		  	             if (j>20 || j == gasolineras.size()-1) {
		  	               mensajeLista.setText(textoLista);
		  	               mensajeLista.disableWebPagePreview();
		  	               try {            
		  	                  execute(mensajeLista);            
		  	               } catch (TelegramApiException e) {
		  	                             e.printStackTrace();
		  	               }
		  	               j = gasolineras.size();
		  	              }
		                }
	               }
			  } else {
				  SendMessage msg = new SendMessage();
				  msg.setChatId(Long.toString(chat_id));
				  msg.setParseMode(ParseMode.HTML);
				  String texto = "<b> Servicio Web no operativo, disculpen las molestias </b>";
				  ReplyKeyboardMarkup keyboardMarkup = botoneraPrincipal();
				  msg.setReplyMarkup(keyboardMarkup);
				  msg.setText(texto);   
	              try {            
	                  execute(msg);            
	              } catch (TelegramApiException e) {
	                             e.printStackTrace();
	              }	  
			  }
            } catch (IOException e) {
		    	// TODO Auto-generated catch block
			  e.printStackTrace();
	    	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
       }
    }
    
    private double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {  
        //double radioTierra = 3958.75;//en millas  
        double radioTierra = 6371;//en kilÃƒÂ³metros  
        double dLat = Math.toRadians(lat2 - lat1);  
        double dLng = Math.toRadians(lng2 - lng1);  
        double sindLat = Math.sin(dLat / 2);  
        double sindLng = Math.sin(dLng / 2);  
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)  
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));  
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));  
        double distancia = radioTierra * va2;  
        return distancia;  
    } 
    
    public class disSorter implements Comparator<Gasolineras> 
    {  	
    	@Override
        public int compare(Gasolineras g1, Gasolineras g2) {
            return Double.compare(g1.getDistancia(), g2.getDistancia());
        }

    }
    
    public class precSorter implements Comparator<Gasolineras> 
    {  	
    	Double val1 = null;
    	Double val2 = null;
    	@Override
        public int compare(Gasolineras g1, Gasolineras g2) {
    		val1 = Double.parseDouble(g1.getPrecioEESS().replace(",", "."));
    		val2 = Double.parseDouble(g2.getPrecioEESS().replace(",", "."));
            return Double.compare(val1, val2);
        }

    }
   private void GestionCrearBd(long chat_id, long user_id, long idUsuAdm) {
    	
    	SendMessage msg = new SendMessage();
    	msg.setChatId(Long.toString(chat_id));
        msg.setParseMode(ParseMode.HTML);
    	database db = new database();	
    	 
		if (user_id == idUsuAdm) {
			db.createTables();
			db.insert();
			msg.setText("Base de datos creada e inicializada");  
			try {
				execute(msg); // Call method to send the photo
			} catch (TelegramApiException e) {
			   e.printStackTrace();
			}
		} 
				    	    	    
    }
    private void GestionLeerBd(long chat_id, long user_id, long idUsuAdm) {
   	
   	  SendMessage msg = new SendMessage();
      msg.setChatId(Long.toString(chat_id));
      msg.setParseMode(ParseMode.HTML);
      database db = new database();	
      ConsultaGas datosCons = null;
   	 
	  if (user_id == idUsuAdm) {
		    datosCons = db.selectAll();
		    String msgPrincipal = null;
		    msgPrincipal = "- Contador total: " + datosCons.getContTotal() + " \n";
	    	msgPrincipal = msgPrincipal.concat("- Contador día: " + datosCons.getContDia() + " \n");
	    	msgPrincipal = msgPrincipal.concat("- Contador semana: " + datosCons.getContSemana() + " \n");
	    	msgPrincipal = msgPrincipal.concat("- Tope Semana: " + datosCons.getDiaTopeSemana() + " \n");
	    	msgPrincipal = msgPrincipal.concat("- Tope día: " + datosCons.getDiaTope() + " \n");
	    	msgPrincipal = msgPrincipal.concat(" \n");
		    
			msg.setText(msgPrincipal);  
			try {
				execute(msg); // Call method to send the photo
			} catch (TelegramApiException e) {
			   e.printStackTrace();
			}
	  } 
				    	    	    
    }
   
    private ReplyKeyboardMarkup botoneraPrincipal() {
    	ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
	    List<KeyboardRow> keyboard = new ArrayList<>();
	    KeyboardRow rowBotonera = new KeyboardRow();	  
	    rowBotonera.add("AYUDA");
	    //
	    KeyboardRow rowBotonera2 = new KeyboardRow();
	    KeyboardButton rowBotoneraLoc = new KeyboardButton();
	    rowBotoneraLoc.setText("PRECIO COMBUSTIBLE");
	    rowBotoneraLoc.setRequestLocation(true);	  
	    rowBotonera2.add(rowBotoneraLoc);
	    keyboard.add(rowBotonera2);
	    //
	    keyboardMarkup.setKeyboard(keyboard);
	    keyboardMarkup.setResizeKeyboard(true);
	    return keyboardMarkup;
    	
    }             
    
    protected InlineKeyboardMarkup botonMapa(ArrayList<Gasolineras> gasolineras) throws UnsupportedEncodingException {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineMapa = new ArrayList<>();
        String textoURL="https://www.google.com/maps/search/?api=1&query=";
        for (int j = 0; j < gasolineras.size(); j++) {
        	String direccion = gasolineras.get(j).getDireccion() + "," + gasolineras.get(j).getCodPostal() +"," + gasolineras.get(j).getMunicipio() +"," + gasolineras.get(j).getLocalidad();
        	textoURL = textoURL.concat(direccion);
	        if (j>20 || j == gasolineras.size()-1) {       	 
	            	  j = gasolineras.size();
 	         } else {
 	        	textoURL = textoURL.concat("|query=");
 	         }
         }
        InlineKeyboardButton iKB = new InlineKeyboardButton();
        iKB.setText("Mapa gasolineras");
        iKB.setUrl(textoURL);
        rowInlineMapa.add(iKB);
        rowsInline.add(rowInlineMapa);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
        
    
    protected InlineKeyboardMarkup botoneraCombustibles(double longitud, double latitud) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        
        InlineKeyboardButton iKB95 = new InlineKeyboardButton();
        iKB95.setText("Gasolina 95");
        iKB95.setCallbackData("1#C&Gas95Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKB95);
        InlineKeyboardButton iKB98 = new InlineKeyboardButton();
        iKB98.setText("Gasolina 98");
        iKB98.setCallbackData("3#C&Gas98Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKB98);    
        InlineKeyboardButton iKBGasA = new InlineKeyboardButton();
        iKBGasA.setText("Gasoleo A");
        iKBGasA.setCallbackData("4#C&GasoleoALong_" + longitud + "_Lat_" + latitud);
        rowInline2.add(iKBGasA);
        InlineKeyboardButton iKBGasB = new InlineKeyboardButton();
        iKBGasB.setText("Gasoleo B");
        iKBGasB.setCallbackData("6#C&GasoleoBLong_" + longitud + "_Lat_" + latitud);
        rowInline2.add(iKBGasB);
        InlineKeyboardButton iKBBio1 = new InlineKeyboardButton();
        iKBBio1.setText("Bioetanol");
        iKBBio1.setCallbackData("16#C&BioetanolLong_" + longitud + "_Lat_" + latitud);
        rowInline3.add(iKBBio1);
        InlineKeyboardButton iKBBioD = new InlineKeyboardButton();
        iKBBioD.setText("Biodiesel");
        iKBBioD.setCallbackData("8#C&BiodiÃ©selLong_" + longitud + "_Lat_" + latitud);
        rowInline3.add(iKBBioD);
        InlineKeyboardButton iKBGasNC = new InlineKeyboardButton();
        iKBGasNC.setText("Gas Nat Comprimido");
        iKBGasNC.setCallbackData("18#C&GasNCLong_" + longitud + "_Lat_" + latitud);
        rowInline4.add(iKBGasNC);
        InlineKeyboardButton iKBGasNL = new InlineKeyboardButton();
        iKBGasNL.setText("Gas Nat Licuado");
        iKBGasNL.setCallbackData("19#C&GasNLLong_" + longitud + "_Lat_" + latitud);
        rowInline4.add(iKBGasNL);
        
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);

        markupInline.setKeyboard(rowsInline);        
    	return markupInline;	
    }
    protected InlineKeyboardMarkup botoneraDistancia(double longitud, double latitud, int tipoPro, String tipoCom) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        
        InlineKeyboardButton iKB1km = new InlineKeyboardButton();
        iKB1km.setText("1 km");
        iKB1km.setCallbackData(tipoPro+"#Dd_1&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKB1km);
        InlineKeyboardButton iKB5km = new InlineKeyboardButton();
        iKB5km.setText("5 km");
        iKB5km.setCallbackData(tipoPro+"#Dd_5&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKB5km);
        InlineKeyboardButton iKB10km = new InlineKeyboardButton();
        iKB10km.setText("10 km");
        iKB10km.setCallbackData(tipoPro+"#Dd_10&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKB10km);
        InlineKeyboardButton iKB20km = new InlineKeyboardButton();
        iKB20km.setText("20 km");
        iKB20km.setCallbackData(tipoPro+"#Dd_20&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline2.add(iKB20km);
        InlineKeyboardButton iKB50km = new InlineKeyboardButton();
        iKB50km.setText("50 km");
        iKB50km.setCallbackData(tipoPro+"#Dd_50&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline2.add(iKB50km);
        InlineKeyboardButton iKB100km = new InlineKeyboardButton();
        iKB100km.setText("100 km");
        iKB100km.setCallbackData(tipoPro+"#Dd_100&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline2.add(iKB100km);
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);        
    	return markupInline;	
    }

    protected InlineKeyboardMarkup botoneraOrden(double longitud, double latitud, int tipoPro, String tipoCom, int distancia) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        
        InlineKeyboardButton iKBPrecio = new InlineKeyboardButton();
        iKBPrecio.setText("Precio");
        iKBPrecio.setCallbackData(tipoPro+"#O1d_" +distancia + "&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKBPrecio);
        InlineKeyboardButton iKBDis = new InlineKeyboardButton();
        iKBDis.setText("Distancia");
        iKBDis.setCallbackData(tipoPro+"#O2d_" +distancia + "&"+tipoCom + "Long_" + longitud + "_Lat_" + latitud);
        rowInline1.add(iKBDis);
       
        rowsInline.add(rowInline1);


        markupInline.setKeyboard(rowsInline);        
    	return markupInline;	
    }
    
      
    private void log(String user_name,  String user_id, String txt, String bot_answer) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + user_name +  " - (id = " + user_id + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
    }
    
    @Override
    public String getBotUsername() {	
    	Properties prop = resourceLoader.loadProperties("configuracion.properties");
        return prop.getProperty("var.BotUsername");
    }

    @Override
    public String getBotToken() {
        Properties prop = resourceLoader.loadProperties("configuracion.properties");
        return prop.getProperty("var.BotToken");
    }
}
