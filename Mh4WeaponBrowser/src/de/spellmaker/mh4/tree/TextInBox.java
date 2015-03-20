package de.spellmaker.mh4.tree;
import de.spellmaker.mh4.data.Weapon;



public class TextInBox {

        public final String text;
        public final int height;
        public final int width;
        
        public final Weapon source;
        
        public TextInBox(String s){
        	this.text = s;
        	this.height = 0;
        	this.width = 0;
        	this.source = null;
        }
        
        public TextInBox(Weapon w){
        	source = w;
        	if(w.isDummy()){
        		this.text = "";
        		this.height = 50;
        	}
        	else{
        		String txt = w.getLocal_name();
        		switch(w.getRankId()){
        			case 1: txt += "\nLow"; break;
        			case 2: txt += "\nHigh"; break;
        			case 3: txt += "\nG"; break;
        			default: txt += "\nunknown";
        		}
        		this.text = txt;
        		this.height = 50;
        	}
        	this.width = this.text.length() * 7;
        }
        
        @Override
        public String toString(){
        	return text;
        }
        
        @Override
        public boolean equals(Object o){
        	if(o instanceof TextInBox){
        		TextInBox other = (TextInBox) o;
        		if(other.source != null && source != null){
        			return other.source.equals(source);
        		}
        		return other.text.startsWith(text) || text.startsWith(other.text);
        	}
        	return false;
        }
}
