package tpandroid.flashlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Morse {

	final private Map<Character,String> encode = new HashMap<Character,String>(); //codificador
    final private Map<String,Character> decode = new HashMap<String,Character>(); //decodificador
    final private String SPACE =" ";                               //espacio
    final private String LETTERGAP = SPACE+SPACE+SPACE+SPACE+SPACE;            //espacio entre letras
    final private String WORDGAP=SPACE+SPACE+SPACE+SPACE+SPACE+SPACE+SPACE+SPACE+SPACE+SPACE;// espacio entre palabras
   
    public Morse() {
        initCode();
    }

    //funcion que agrega valores a las matrices codificador y decodificador
    private void addValue(Character character, String code){
        encode.put(character, code);
        decode.put(code, character);
    }

    private void initCode(){
        addValue('A', ".-");
        addValue('B', "-...");
        addValue('C', "-.-.");
        addValue('D', "-..");
        addValue('E', ".");
        addValue('F', "..-.");
        addValue('G', "--.");
        addValue('H', "....");
        addValue('I', "..");
        addValue('J', ".---");
        addValue('K', "-.-");
        addValue('L', ".-..");
        addValue('M', "--");
        addValue('N', "-.");
        addValue('O', "---");
        addValue('P', ".--.");
        addValue('Q', "--.-");
        addValue('R', ".-.");
        addValue('S', "...");
        addValue('T', "-");
        addValue('U', "..-");
        addValue('V', "...-");
        addValue('W', ".--");
        addValue('X', "-..-");
        addValue('Y', "-.--");
        addValue('Z', "--..");
        addValue('1', ".----");
        addValue('2', "..---");
        addValue('3', "...--");
        addValue('4', "....-");
        addValue('5', ".....");
        addValue('6', "-....");
        addValue('7', "--...");
        addValue('8', "---..");
        addValue('9', "----.");
        addValue('0', "-----");
    }

    /**
     * Decodifica el codigo morse a su equivalente caracter
     * @parametro morseCode
     * @return the character
     * @throws IllegalArgumentException if el fragmento en código morse no es conocido
     */
    public Character decodeSingle(String morseCode){
        if(!decode.containsKey(morseCode)){
            throw new IllegalArgumentException("Código desconocido: "+morseCode);
        }
        return decode.get(morseCode);
    }
    
    /**
     * Decodifica el simple caracter al equivalente en codigo morse
     * @parametro caracter
     * @return el string en código morse
     * @throws IllegalArgumentException si el caracter no es conocido
     */
    public String encodeSingle(Character character){
        if (SPACE.equals(character.toString()))return WORDGAP;
        if(!encode.containsKey(character)){
            throw new IllegalArgumentException("Código no encontrado para caracter: "+character);
        }
        return encode.get(character)+LETTERGAP;
    }
    
    /**
     * Devuelve un string en su equivalente a codigo morse
     * Umlauts, etc. are simply ignored
     * @parametro mensaje
     * @return el string en código morse
     */
    public String encodeMessage(String message){
        message = message.trim() 
        		.toUpperCase(Locale.ENGLISH)//the codemap is uppercase....
                .replaceAll("[^\\w\\s]", "")//remove special characters
                .replaceAll("\\s+", " ")//replace multiple spaces, tabs, etc. by a single space
                ;
        String result ="";
        for (char c :message.toCharArray()){
            result+=encodeSingle(c);
        }
        return result;
    }
    
    /**
     * Codifica un mensaje a una lista de señales que representan el estado on/off
     * @parametro Mensaje ingresado por el usuario
     * @return una lista de valores booleanos para representar los estados on/off
     */
    
    public List<Boolean> messageToSignal(String cleartextMessage){	
        List<Boolean> result = new ArrayList<Boolean>();
        for (char c: encodeMessage(cleartextMessage).toCharArray()){
            if (c=='-'){
                for (int i=0;i<6;i++){
                    result.add(Boolean.TRUE);//rayas son 6 unidades de tiempo
                }
                //separacion entre punto o raya son dos unidades de tiempo
                result.add(Boolean.FALSE);
                result.add(Boolean.FALSE);
            }else if (c=='.'){
                //puntos son dos unidad de tiempo
                result.add(Boolean.TRUE);
                result.add(Boolean.TRUE);
                //separacion entre punto o raya son dos unidades de tiempo
                result.add(Boolean.FALSE);
                result.add(Boolean.FALSE);
            }else{
                result.add(Boolean.FALSE);//cualquier espacio en blanco es una unidad de tiempo
            }
        }
        
        return result;
    }
    

}
