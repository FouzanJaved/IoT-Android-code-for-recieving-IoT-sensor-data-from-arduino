Android code

package wolfsoft.ozzon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Timer;

public class ActivitySignin extends AppCompatActivity {


     ImageView signinback;
     TextView Setbtn;
     EditText temperature;
     EditText humidity;

    //ui for setting up temp and humidity values
    TextView Setbtn2;
    EditText temperature2;
    EditText humidity2;

    private Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        signinback = (ImageView)findViewById(R.id.signinback);
        Setbtn = (TextView)findViewById(R.id.signin1);
        temperature=(EditText) findViewById(R.id.temperature);
        humidity=(EditText) findViewById(R.id.humidity);


        Setbtn2 = (TextView)findViewById(R.id.signin2);
        temperature2=(EditText) findViewById(R.id.temperature2);
        humidity2=(EditText) findViewById(R.id.humidity2);


        signinback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ActivitySignin.this,MainActivity.class);
                startActivity(it);
                Toast.makeText(ActivitySignin.this, "This is my Toast message!",
                        Toast.LENGTH_LONG).show();

            }
        });


        Setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ActivitySignin.this, "",
                //       Toast.LENGTH_LONG).show();
                getWebsite();
                //new Timer().scheduleAtFixedRate(getWebsite() , 5000, 5000);

            }
        });

        Setbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp2=temperature2.getText().toString();
                // Toast.makeText(ActivitySignin.this, "temperature Set"+temp2,
                //        Toast.LENGTH_LONG).show();

                String humid2=humidity2.getText().toString();
                //Toast.makeText(ActivitySignin.this, "Humidity Set"+humid2,
                //        Toast.LENGTH_LONG).show();
                Volleyy volleyy1=new Volleyy();

                //Passing String array to this and getting values on other end
                volleyy1.execute(new String[]{temp2,humid2});

                //SetWebsite();

            }
        });



        final Handler handler = new Handler();

        //here is timer running after every 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override

            public void run() {

                //Do something here
                // Toast.makeText(ActivitySignin.this, "Set button is clicked !",
                //        Toast.LENGTH_LONG).show();
                Setbtn.performClick();
                handler.postDelayed(this, 3000);

            }
        }, 3000);


    }




    private void getWebsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc= Jsoup.connect("http://info.perridrugs.com/getWeather.php").get();
                    Element title= doc.body();
                    Elements links= doc.select("a[ref]");

                    builder.append(title).append("\n");

                    for(Element link: links)
                    {
                        builder.append("\n").append("Link: ").append(link.attr("href")).append("\n").append("Text: ").append(link.text());
                    }

                } catch (IOException e) {
                    builder.append("Error :").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str1=builder.toString();
                        String str=builder.toString();

                        //deleting extra words from the string from start and from end
                        StringBuilder FetchedData = new StringBuilder(str);
                        FetchedData.delete(0,6);
                        FetchedData.delete(17,40);
                        temperature.setText(FetchedData);

                        Log.d("check", "1");
                        StringBuilder FetchedData1 = new StringBuilder(str1);
                        Log.d("check", "2");
                        FetchedData1.delete(0,25);

                        Log.d("check", "3");
                        humidity.setText(FetchedData1);

                    }
                });

            }
        }).start();

    }


    private void SetWebsite(){

                try {
                    String temp2=temperature2.getText().toString();
                    Toast.makeText(ActivitySignin.this, "Humidity Set"+temp2,
                           Toast.LENGTH_LONG).show();

                    Document doc = Jsoup.connect("http://info.perridrugs.com/getWeather.php?temp=12&humidity=50").get();
                    Element title = doc.body();
                    Elements links = doc.select("a[ref]");

                    // builder.append(title).append("\n");

                    // for (Element link : links) {
                        //    builder.append("\n").append("Link: ").append(link.attr("href")).append("\n").append("Text: ").append(link.text());
                    //}

                } catch (IOException e) {
        //  builder.append("Error :").append(e.getMessage()).append("\n");
                }


    }







    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        Setbtn.performClick();
    }


}



-------------Server code --------------------

<?php
	$servername = "localhost";
	$username = "fouzanweather";
	$password = "12345";
	$dbname = "weather_fouzan";
	$conn = new mysqli($servername, $username, $password, $dbname);
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 
if(isset($_GET['temp']) && isset($_GET['humidity'])) {	
	$temp = $_GET['temp'];
	$humidity = $_GET['humidity'];
	$sql = "UPDATE humidity_temp SET temp='$temp', humitity='$humidity' WHERE id=1";

	if ($conn->query($sql) === TRUE) {
		echo "New record created successfully";
	} else {
		echo "Error: " . $sql . "<br>" . $conn->error;
	}
}
$sql = "SELECT id, temp, humidity FROM humidity_temp WHERE id=1";
$result = $conn->query($sql);
if($result->num_rows>0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "Temperature: " . $row["temp"]. ", Humidity: " . $row["humidity"]. "<br>";
    }
} else {
    echo "0 results";
}
$conn->close();



------------- Arduino Code ------------------------------

#include <dht.h>

#include <LiquidCrystal.h>  
int Contrast=100;
 LiquidCrystal lcd(12, 11, 5, 4, 3, 2); 
int FanPin=13;
int HumidPin=7;

#include <SoftwareSerial.h>
SoftwareSerial mySerial(8, 9); // RX, TX


#define dht_dpin A1 //no ; here. Set equal to channel sensor is on

dht DHT;
String voice;
String TemppVal;
String HumiddVal;

void setup(){
  Serial.begin(9600);
  pinMode(FanPin, OUTPUT);
  pinMode(HumidPin, OUTPUT);
  delay(300);//Let system settle
  Serial.println("Humidity and temperature\n\n");
  delay(700);//Wait rest of 1000ms recommended delay before
  //accessing sensor
      analogWrite(6,Contrast);
     lcd.begin(16, 2);
    lcd.setCursor(0, 0);
    lcd.print( "  Adnan , Aamir");
    lcd.setCursor(0, 1);
    lcd.print("     Fazal     ");
    delay(2000);
    lcd.clear();

      digitalWrite(FanPin,HIGH); //HIGH means off and low means on
      digitalWrite(HumidPin,HIGH);//Because its NOT logic relay.
      // set the data rate for the SoftwareSerial port
  mySerial.begin(9600);
}//end "setup()"

void loop(){
  //This is the "heart" of the program.
  DHT.read11(dht_dpin);

   // Serial.print("Current humidity = ");
  //  Serial.print(DHT.humidity);
   // Serial.print("%  ");
  //  Serial.print("temperature = ");
  //  Serial.print(DHT.temperature); 
  //  Serial.println("C  ");
 // delay(800);//Don't try to access too frequently... in theory
  //should be once per two seconds, fastest,
  //but seems to work after 0.8 second.


    lcd.setCursor(0, 0);
    lcd.print( "TEMP ");
    lcd.print(DHT.temperature);
    lcd.print( " C");
    lcd.setCursor(0, 1);
    lcd.print( "HUMD ");
    lcd.print(DHT.humidity);
    lcd.print( " %");
    delay(500);

    while (mySerial.available()){  //Check if there is an available byte to read
  delay(10); //Delay added to make thing stable
  char c = mySerial.read(); //Conduct a serial read
  if (c == '#') {break;} //Exit the loop when the # is detected after the word
   voice = voice + c; //Shorthand for voice = voice + c
  } 
  if (voice.length() > 0) {
    
    Serial.println(voice);
    Serial.println(voice);
    Serial.println(voice.substring(15,17));
    Serial.println(voice.substring(27,29));
    TemppVal=voice.substring(15,17);
    Serial.println("TEMPVAl Heree");
    Serial.println(TemppVal.toInt());
    if(TemppVal.toInt()>DHT.temperature)
    {
      Serial.println("Command given for more temp than DHT.temperature");
      digitalWrite(FanPin,HIGH); //HIGH means off and low means on
      digitalWrite(HumidPin,LOW);//Because its NOT logic relay.
      }
     else{
      Serial.println("Command given for smaller temp than DHT.temperature");
      digitalWrite(FanPin,LOW);
      digitalWrite(HumidPin,HIGH);
      }
    
    lcd.setCursor(0, 0);
    lcd.print( "ServerTEMP ");
    lcd.print(voice.substring(15,17));
    lcd.print( " C");
    lcd.setCursor(0, 1);
    lcd.print( "ServerHUMD ");
    lcd.print(voice.substring(27,29));
    lcd.print( " %");
    delay(2000);
    
    if(voice=="1")
    {
    //As this will be serially printed so it is send back to android phone
    Serial.print(" hello");
    }
    else if (voice=="2")
    {
     //This too will be send back 
    Serial.println(" hello 2");
    }
    voice="";
  }

 
    
}// end loop()
