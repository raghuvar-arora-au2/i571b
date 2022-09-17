import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws Exception {
        // System.out.println("Hello, World!");
        // Pattern pattern = Pattern.compile("-?\\d+");
        // System.out.println("this"+pattern.matcher("-123ui1").matches());
        // Matcher m=pattern.matcher("-123ui1");
        // System.out.println("   "+m.find());

        // System.out.println(m.group());
        // System.out.println(pattern.matcher("12345").matches());
        // System.out.println(pattern.matcher("123456789").matches());
        
        Scanner scanner=new Scanner();
        // ArrayList<Token> tokens=scanner.scan("{22, {44, 99}, [6...8]={33, [3]=4, 77}, [7...8]=99,}");
        // for (Token t: tokens ){
        //     System.out.println(t);
        // };
        
        ArrayList<Token> tokens=scanner.scan(args[0]);

        C99Parser parser=new C99Parser(tokens);
        System.out.println(parser.parse());

        // {1,2,3,4, {,1,2,3,{1,2,3}}}
    }
}

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
            // TODO:throw error
        }
    }

}

class C99Parser extends Parser{

    public C99Parser(ArrayList<Token> tokens){
        super(tokens);
    }

    String parse() throws Exception{
        String val=val();
        if(this.check("EOF"))
            return val;
        else{
            throw new Exception("error: expecting 'EOF' but got '"+this.lookahead.lexeme+"'");
        }
    }

    String val() throws Exception{
        
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
                    output+=aux.get(i)+", ";
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
            throw new Exception("error: expecting '{' but got '"+this.lookahead.lexeme+"'");
        }

    }

    void initializers(ArrayList<String> aux) throws Exception{
        
        initializer(aux);
        while(this.check(",")){
            this.match(",");
            initializer(aux);
        }

    }

    void parseRangeInitializer(ArrayList <String> aux, Token startIndex) throws Exception{
        
        this.match("...");
        if(this.check("INT")){
            Token endIndex=this.lookahead;
            this.match("INT");
            if(this.check("]")){
                this.match("]");
            }
            else{
                throw new Exception("error: expecting ']' but got '"+this.lookahead.lexeme+"'");
                
            }

            if(this.check("=")){
                this.match("=");
            }
            else{
                
                throw new Exception("error: expecting '=' but got '"+this.lookahead.lexeme+"'");
            }

            String val=val();
            intializeUsingRange(aux, Integer.parseInt(startIndex.lexeme), Integer.parseInt(endIndex.lexeme), val);
        }
        else{
            
            throw new Exception("error: expecting INT but got '"+this.lookahead.lexeme+"'");
        }
        
    }

    void parseSimpleDesignatedInitializer(ArrayList <String> aux, Token index) throws Exception{
        this.match("]");

        if(this.check("=")){
            this.match("=");
            String val=val();
            intializeUsingSimpleInitializer(aux, Integer.parseInt(index.lexeme), val);
        }
        else{
            throw new Exception("error: expecting '=' but got '"+this.lookahead.lexeme+"'");
        }
    }

    void initializer(ArrayList <String> aux) throws Exception{
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
                    throw new Exception("error: expecting ']' but got '"+this.lookahead.lexeme+"'");
                }
            }
            else{
                throw new Exception("error: expecting INT but got '"+this.lookahead.lexeme+"'");
            }
        }
        else if(this.check("INT") || this.check("{")){
            aux.add(this.val());
             
        }
        else if(this.check(",")){
            throw new Exception("error: expecting '}' but got '"+this.lookahead.lexeme+"'");
        }       
        
    }

    private void intializeUsingSimpleInitializer(ArrayList<String> aux, int index, String val){
        if(aux.size()<index){
            for(int i=aux.size()-1;i<index;i++){
                aux.add("0");
            }
        }

        aux.set(index, val);
    }
    
    private void intializeUsingRange(ArrayList<String> aux, int start, int end, String val) throws Exception{
        if(end<start){        
            throw new Exception("error: Incorrect range");
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
            else if(str.charAt(i)=='['){
                tokens.add(new Token("[", "["));

            }
            else if(str.charAt(i)==']'){
                tokens.add(new Token("]", "]"));
            }
            else if(i+3<str.length() && str.substring(i, i+3).equals("...")){
                tokens.add(new Token("...","..."));
                i=i+2;
            }
            else if(matcherNumber.find(i) && matcherNumber.start()==i){
                tokens.add(new Token("INT", matcherNumber.group()));
                i=matcherNumber.end()-1;
            }

            else if (str.charAt(i)=='{'){
                tokens.add(new Token("{", "{"));
            }
            else if(str.charAt(i)=='}'){
                tokens.add(new Token("}", "}"));
            }
            else if(str.charAt(i)=='='){
                tokens.add(new Token("=", "="));
            }
            else if( str.charAt(i)==','){
                tokens.add(new Token(",", ","));
            }
            else{
                tokens.add(new Token(str.substring(i, i+1),str.substring(i, i+1)));
            }

        }

        return this.tokens;
    }
}
