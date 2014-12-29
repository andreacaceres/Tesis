package ec.cacehure.classfinder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class pantalla_haciaDondeIr extends Activity{

	Button btn_Yes, btn_No;
	//LOCALIZACION
	WifiManager allwifi;
	WifiScanReceiver wifiReciever;
	//25 de Mayo 2014
	private static final String url_localizacion = "http://192.168.0.6/WebService/localizacion_three.php";
	private static final String TAG_VALUE0 = "value0";
	private static final String TAG_VALUE1 = "value1";
	private static final String TAG_VALUE2 = "value2";
	private static final String TAG_VALUE3 = "value3";
	
	//Domingo 4 de Mayo 2014
	private ProgressDialog pDialog;
	JSONParser JParser = new JSONParser();
	private static final String TAG_SUCCESS = "success";
	JSONArray courses = null;
	
	//Julio 4 de 2014
	Vector p = new Vector();
	private static String url_localizacionOne = "http://192.168.0.6/WebService/localizacion_one.php";
	private static String url_two = "http://192.168.0.6/WebService/dos.php";
	private static final String TAG_VALUE = "value";
	private static final String TAG_AP_ONE = "ap";
	private static final String TAG_DESCRIPCION_ONE = "descripcion_one";
	private static final String TAG_PATH_IMAGEN_ONE = "path_imagen_one";
	JSONArray ap = null;
	//End
	
	//Mostrar imagen
	ImageView imagen_one;
	//Mostrar el Sitio
	private TextView lugar;
	private String descripcion;
	private String path_imagen_one;
	
	//test maps
	Button btnNext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.togo);
				
		allwifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifiReciever = new WifiScanReceiver();
		allwifi.startScan();
		
		btn_Yes = (Button)findViewById(R.id.btnYes);
		btn_Yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(pantalla_haciaDondeIr.this, cursos.class);
				startActivity(intent);
			}
		});
						
		btn_No = (Button)findViewById(R.id.btnNo);
		btn_No.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(pantalla_haciaDondeIr.this, lugares_conocidos.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(wifiReciever);
		super.onPause();
	}
		@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}
    
    //Clase WifiScanReceiver
    class WifiScanReceiver extends BroadcastReceiver{
    	
		@Override
		public void onReceive(Context c, Intent intent) {
			// TODO Auto-generated method stub
			//Estan todas las wifi detectadas
			List<ScanResult> wifiScanList = allwifi.getScanResults();
			final int n = wifiScanList.size(); //El tama�o de la lista
			
			//Ordena la lista de menos level a mayor level
			Comparator<ScanResult> comparator = new Comparator<ScanResult>(){
				@Override
				public int compare(ScanResult arg0, ScanResult arg1) {
					// TODO Auto-generated method stub
					//return (arg0.level<arg1.level ? -1 : (arg0.level==arg1.level ? 0: 1));
					return (arg0.level>arg1.level ? -1 : (arg0.level==arg1.level ? 0: 1));
					//return 0;
				}
	        };       
	        Collections.sort(wifiScanList,comparator);
	        
			//Imprime la lista que destecto y ordenada
			Log.v("=============>DETECTA", "AP's: "+ wifiScanList);
			
			for(int i=0;i<n;i++){
				if( (wifiScanList.get(i).SSID).equalsIgnoreCase("FIEC") || (wifiScanList.get(i).SSID).equalsIgnoreCase("FIEC-WIFI") || (wifiScanList.get(i).SSID).equalsIgnoreCase("Claro_MOLINA0000029162") || (wifiScanList.get(i).SSID).equalsIgnoreCase("FIEC_EVENTOS") || (wifiScanList.get(i).SSID).equalsIgnoreCase("FIEC_CONSEJO") || (wifiScanList.get(i).SSID).equalsIgnoreCase("FIEC_MET") || (wifiScanList.get(i).SSID).equalsIgnoreCase("Cidis_Lab")){
					p.add(wifiScanList.get(i));
				}
			}
			Log.v("=============> Android BSSID", "AP DISPONIBLES VECTOR: "+ p.toString());
			
			int tam_vetor = p.size();
			Log.v("=============> Android BSSID", "TAMA�O VECTOR: "+ tam_vetor);
			
			if(tam_vetor > 3){
				//Se pasa los TRES primeros valores al servidor
				LoadWifiScan tres = new LoadWifiScan();
				//tres.execute( (p.elementAt(0).toString()), (p.elementAt(1).toString()), (p.elementAt(2).toString()) );
				//tres.execute( (p.elementAt(0).toString()), (p.elementAt(1).toString()) );
				//Le voy a pasar 4 se�ales.
				tres.execute( (p.elementAt(0).toString()), (p.elementAt(1).toString()), (p.elementAt(2).toString()), (p.elementAt(3).toString()) );
			}else{
				//Se pasa solo el PRIMER valor al servidor
				LoadOneWifi one = new LoadOneWifi();
				one.execute((p.elementAt(0).toString()));
			}
		}		
    }
    
    class LoadWifiScan extends AsyncTask<String, String, String>{
    	
		@Override
		protected String doInBackground(String... params) {
			//25 de Mayo del 2014
			List<NameValuePair> parametrosWifi = new ArrayList<NameValuePair>();
			String uno = params[0];
			String dos = params[1];
			String tres = params[2];
			String cuatro = params[3];
			Log.v("======>UNO", uno);
			Log.v("======>DOS", dos);
			Log.v("======>TRES", tres);
			Log.v("======>CUADRO", cuatro);
			parametrosWifi.add(new BasicNameValuePair(TAG_VALUE0, uno ));
			parametrosWifi.add(new BasicNameValuePair(TAG_VALUE1, dos ));
			parametrosWifi.add(new BasicNameValuePair(TAG_VALUE2, tres ));
			parametrosWifi.add(new BasicNameValuePair(TAG_VALUE3, cuatro ));
			
			JSONObject jsonWifi = JParser.makeHttpRequest(url_localizacion, "POST", parametrosWifi);
			//JSONObject jsonWifi = JParser.makeHttpRequest(url_two, "POST", parametrosWifi);
			Log.v("======>Lo que paso al otro lado TRES", jsonWifi.toString());
			//Log.v("======>Lo que paso al otro lado DOS", jsonWifi.toString());
						
			try{
				int success = jsonWifi.getInt(TAG_SUCCESS);
				if(success == 1){
					//Paso todo
				}else{
					//Hubo error
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
    	
    }
    
    //Testing the async
    class LoadOneWifi extends AsyncTask<String, String, String>{
    	
    	//Antes de comenzar el hilo background le muestra un mensajito =P
    	@Override
		protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(pantalla_haciaDondeIr.this);
    		pDialog.setMessage("Calculando coordenadas...");
    		pDialog.setIndeterminate(false);
    		pDialog.setCancelable(true);
    		pDialog.show();
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			Log.v("======>PARAMS_TO_STRING", Arrays.toString(params));
			List<NameValuePair> parametrosWifi = new ArrayList<NameValuePair>();
			parametrosWifi.add(new BasicNameValuePair(TAG_VALUE, Arrays.toString(params) ));
			
			JSONObject jsonWifi = JParser.makeHttpRequest(url_localizacionOne, "POST", parametrosWifi);
			//reavisar como regresa el request
			Log.v("======>Lo que paso al otro lado ONE", jsonWifi.toString());
			try{
				int success = jsonWifi.getInt(TAG_SUCCESS);
				if(success == 1){
					ap = jsonWifi.getJSONArray(TAG_AP_ONE);
					for (int i = 0; i< ap.length(); i++){
						JSONObject c = ap.getJSONObject(i);
						lugar = (TextView)findViewById(R.id.textplace);
						descripcion = c.getString(TAG_DESCRIPCION_ONE);
						path_imagen_one = c.getString(TAG_PATH_IMAGEN_ONE);
						Log.v("=====>Dentro del for",descripcion);
						Log.v("=====>Dentro del for",path_imagen_one);
						try{
							imagen_one = (ImageView)findViewById(R.id.image1);
							URL url = new URL(path_imagen_one);
							Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
							imagen_one.setImageBitmap(bitmap);
						}catch(Exception e){
							
						}
					}
				}else{
					//Hubo error
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			return null;
		}
		//Despues
		@Override
		protected void onPostExecute(String file_url){
			pDialog.dismiss();
			lugar = (TextView)findViewById(R.id.textplace);
			lugar.setText(descripcion);
		}
    }
		
}
