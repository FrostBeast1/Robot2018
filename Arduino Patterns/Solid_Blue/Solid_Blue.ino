#include <Adafruit_DotStar.h>
#define DATAPIN 11
#define CLOCKPIN 13
Adafruit_DotStar strip = Adafruit_DotStar(
  NUMPIXELS, DATAPIN, CLOCKPIN, DOTSTAR_BRG);
void setup() {
strip.begin(); 
  strip.show();
}

void loop() {
 for (int i=0; i <= 255; i++){
  color = (i, 0, 0, 255);
 }
}