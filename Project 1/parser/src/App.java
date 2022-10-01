import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws Exception {
        
        Scanner scanner=new Scanner();
        // ArrayList<Token> tokens=scanner.scan("{1,2,3}");
        // for (Token t: tokens ){
        //     System.out.println(t);
        // };
        String input="";
        for(String a: args){
            input+=a;
        }
        ArrayList<Token> tokens=scanner.scan(input);

        C99Parser parser=new C99Parser(tokens);
        System.out.println(parser.parse());

    }
}

    
/**
 * This class contains the common functionality for the parser 
 */
class Parser{
    ArrayList<Token> tokens;
    int index;
    Token lookahead;
    public Parser(ArrayList<Token> tokens){
        this.tokens=tokens;
        this.index=0;
        this.lookahead=nextToken();

    }

    Token nextToken(){
        if(this.index<tokens.size())
            return this.tokens.get(index++);
        else{
            return new Token("EOF", "EOF");
        }
    }

    boolean check(String kind){
        return lookahead.kind.equals(kind);
    }

    void match(String kind){
        if(this.check(kind)){
            this.lookahead=nextToken();
        }
        else{
            System.out.println("error: expecting '"+kind+"' but got '"+this.lookahead.lexeme+"'");
            System.exit(1);
        }
    }

}

/**
 * This class contains the functinality according to the grammer
 */
class C99Parser extends Parser{

    public C99Parser(ArrayList<Token> tokens){
        super(tokens);
    }

    String parse(){
        String val=val();
        if(this.check("EOF"))
            return val;
        else{
            System.out.println("error: expecting 'EOF' but got '"+this.lookahead.lexeme+"'");
            System.exit(1);
            // throw new Exception("error: expecting 'EOF' but got '"+this.lookahead.lexeme+"'");
            return "";
        }
    }

    /**
     * val
     *  :  '{' initializer  '}'
     *  |   INT
    */
    String val(){
        
        if(this.check("INT")){
            Token t=lookahead;
            this.match("INT");
            return t.lexeme;
        }
        else if(this.check("{")){
            ArrayList<String> aux=new ArrayList<>();
            this.match("{");
            initializers(aux);
            String output="[";

            for (int i=0;i<aux.size();i++){
                if(i<aux.size()-1){
                    output+=aux.get(i)+",";
                }
                else{
                    output+=aux.get(i);
                }
            }

            this.match("}");
            output+="]";
            return output;
        }
        else{
            System.out.println("error: expecting '{' but got '"+this.lookahead.lexeme+"'");
            System.exit(1);
            // throw new Exception("error: expecting '{' but got '"+this.lookahead.lexeme+"'");
            return "";
        }

    }

    /**
     * initializers
     *  : initializer ( ',' initializer )* ','? //optional comma after last init
     *  | //empty
     *  ;
    */
    void initializers(ArrayList<String> aux) {
        
        initializer(aux);
        while(this.check(",")){
            this.match(",");
            initializer(aux);
        }

    }

    /**
     * This handles the range designated initializer
     *  Grammer:  '[' INT '...' INT ']' '=' val 
    */
    void parseRangeInitializer(ArrayList <String> aux, Token startIndex) {
        
        this.match("...");
        if(this.check("INT")){
            Token endIndex=this.lookahead;
            this.match("INT");
            if(this.check("]")){
                this.match("]");
            }
            else{
                System.out.println("error: expecting ']' but got '"+this.lookahead.lexeme+"'");
                System.exit(1);
                // throw new Exception("error: expecting ']' but got '"+this.lookahead.lexeme+"'");
                
            }

            if(this.check("=")){
                this.match("=");
            }
            else{
                System.out.println("error: expecting '=' but got '"+this.lookahead.lexeme+"'");
                System.exit(1);
                // throw new Exception("error: expecting '=' but got '"+this.lookahead.lexeme+"'");
            }

            String val=val();
            intializeUsingRange(aux, Integer.parseInt(startIndex.lexeme), Integer.parseInt(endIndex.lexeme), val);
        }
        else{
            System.out.println("error: expecting 'INT' but got '"+this.lookahead.lexeme+"'");
            System.exit(1);
            // throw new Exception("error: expecting INT but got '"+this.lookahead.lexeme+"'");
        }
        
    }

    /**
     * This handles the simple designated initializer
     *  Grammer : '[' INT '] '=' val       
    */
    void parseSimpleDesignatedInitializer(ArrayList <String> aux, Token index){
        this.match("]");

        if(this.check("=")){
            this.match("=");
            String val=val();
            intializeUsingSimpleInitializer(aux, Integer.parseInt(index.lexeme), val);
        }
        else{
            System.out.println("error: expecting '=' but got '"+this.lookahead.lexeme+"'");
            System.exit(1);
            // throw new Exception("error: expecting '=' but got '"+this.lookahead.lexeme+"'");
        }
    }

    /**
     * initializer
     *  : '[' INT '] '=' val              //simple designated initializer
     *  | '[' INT '...' INT ']' '=' val   //range designated initializer
     *  | val                             //positional initializer
     *  ;
    */
    void initializer(ArrayList <String> aux){
        if(this.check("[")){
            this.match("[");
            if(this.check("INT")){
                Token startIndex=this.lookahead;
                this.match("INT");

                if(this.check("...")){
                    parseRangeInitializer(aux, startIndex);
                }
                else if(this.check("]")){
                    parseSimpleDesignatedInitializer(aux, startIndex);
                }
                else{
                    System.out.println("error: expecting ']' but got '"+this.lookahead.lexeme+"'");
                    System.exit(1);
                    // throw new Exception("error: expecting ']' but got '"+this.lookahead.lexeme+"'");
                }
            }
            else{
                // throw new Exception("error: expecting INT but got '"+this.lookahead.lexeme+"'");
                System.out.println("error: expecting 'INT' but got '"+this.lookahead.lexeme+"'");
                System.exit(1);
            }
        }
        else if(this.check("INT") || this.check("{")){
            aux.add(this.val());
             
        }
        else if(this.check(",")){
            System.out.println("error: expecting '}' but got '"+this.lookahead.lexeme+"'");
            System.exit(1);
            // throw new Exception("error: expecting '}' but got '"+this.lookahead.lexeme+"'");
        }       
        
    }

    /**
     * This sets the value to val for the given index
    */
    private void intializeUsingSimpleInitializer(ArrayList<String> aux, int index, String val){
        if(aux.size()<index){
            for(int i=aux.size()-1;i<index;i++){
                aux.add("0");
            }
        }

        aux.set(index, val);
    }
    /**
     * This sets the value to val for the given range
    */
    private void intializeUsingRange(ArrayList<String> aux, int start, int end, String val){
        if(end<start){        
            // throw new Exception("error: Incorrect range");
            System.out.println("error: incorrect range");
            System.exit(1);
        }
        else if(end> aux.size()) {
            for(int i=aux.size()-1;i<end;i++){
                aux.add("0");
            }
        }
        
        for(int i=start;i<=end;i++){
            aux.set(i,val);
        }

    }
}
/**
 * This represents individual tokens
*/
class Token{
    String kind;
    String lexeme;

    public Token(String kind, String lexeme){
        this.kind=kind;
        this.lexeme=lexeme;
    
    }

    public String toString(){
        return "Kind: "+this.kind+", Lexeme "+this.lexeme;
    }
}

/**
 * This class is used to tokenize the input
 */
class Scanner{
    ArrayList<Token> tokens;

    public Scanner(){
        this.tokens=new ArrayList<>();
    }

    public ArrayList<Token> scan(String str){
        Pattern patternNumber =Pattern.compile("-?\\d+");
        Matcher matcherNumber=patternNumber.matcher(str);      
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==' '){
                continue;
            }
            else if(str.charAt(i)=='\n'){
                continue;
            }
            else if(i+3<str.length() && str.substring(i, i+3).equals("...")){
                tokens.add(new Token("...","..."));
                i=i+2;
            }
            else if(matcherNumber.find(i) && matcherNumber.start()==i){
                tokens.add(new Token("INT", matcherNumber.group()));
                i=matcherNumber.end()-1;
            }
            else{
                tokens.add(new Token(str.substring(i, i+1),str.substring(i, i+1)));
            }

        }

        return this.tokens;
    }
}
