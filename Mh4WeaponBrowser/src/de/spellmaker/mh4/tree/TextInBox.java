import java.awt.Graphics;

public class TextInBox {

        public final String text;
        public final int height;
        public final int width;
        
        public final Weapon source;
        
        public TextInBox(Weapon w){
        	source = w;
        	if(w.isDummy()){
        		this.text = "";
        		this.height = 50;
        	}
        	else{
        		this.text = w.getLocal_name();
        		this.height = 50;
        	}
        	this.width = this.text.length() * 10;
        }
}
