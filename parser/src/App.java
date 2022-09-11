import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Pattern pattern = Pattern.compile("-?\\d+");
        System.out.println("this"+pattern.matcher("-123ui1").matches());
        Matcher m=pattern.matcher("-123ui1");
        System.out.println("   "+m.find());

        System.out.println(m.group());
        System.out.println(pattern.matcher("12345").matches());
        System.out.println(pattern.matcher("123456789").matches());

        Scanner scanner=new Scanner();
        for (Token t: scanner.scan("-123ui,-1,[123...  234 ]{[3], [45] } [123...  2534 ],52")){
            System.out.println(t);
        };
        

    }
}

class Parser{
    ArrayList<Token> tokens;
    int index;
    Token lookahead;
    public Parser(ArrayList<Token> tokens){
        this.tokens=tokens;
        this.index=0;

    }

    Token nextToken(){
        return this.tokens.get(index++);
    }

    boolean check(String kind){
        return lookahead.kind.equals(kind);
    }

    void match(String kind){
        if(this.check(kind)){
            this.lookahead=nextToken();
        }
        else{
            // throw error
        }
    }

}

class C99Parser extends Parser{

    public C99Parser(ArrayList<Token> tokens){
        super(tokens);
    }

    String val(){
        ArrayList<String> aux=new ArrayList<>();
        if(this.check("INT")){
            Token t=this.nextToken();
            this.match("INT");
            return t.lexeme;
        }
        else if(this.check("{")){
            
        }

        String output="[";

        for (int i=0;i<aux.size();i++){
            output+=aux.get(i);
        }

        return output;
    }

    // String initializers(){
    //     return initializer();
    // }

    String initializer(ArrayList <String> aux){
        // check if SIMPLE INITILAER
        // doo the same as following
        
        if(this.check("RANGE")){
            // get the look ahead VAL (after ""="")
            // call val
            // and set output to the ranges of the arraylist
            // if look ahead not satisfied throw ERROR
        }
        // if val: evaluate val and add to the end of arraylist 
        
        
        return "";
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

class Range extends Token{
    String start;
    String end;

    public Range(String start, String end){
        super("RANGE", "RANGE");
        this.start=start;
        this.end=end;
    }

    @Override
    public String toString() {
        return "RANGE: "+ start+" -> "+end;
    }
    
}

class Scanner{
    ArrayList<Token> tokens;

    public Scanner(){
        this.tokens=new ArrayList<>();
    }

    public ArrayList<Token> scan(String str){
        Pattern patternNumber =Pattern.compile("-?\\d+");
        Pattern patternRange=Pattern.compile("\\[*\\s*\\d+\\s*\\.\\.\\.\\s*\\d+\\s*\\]");
        Pattern patternSimpleInitializer=Pattern.compile("\\[*\\s*\\d+\\s*\\]");
        Matcher matcherNumber=patternNumber.matcher(str);
        Matcher matcherRange=patternRange.matcher(str);
        Matcher matcherSimpleInitializer=patternSimpleInitializer.matcher(str);
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==' '){
                continue;
            }
            if (matcherRange.find(i)  && matcherRange.start()==i  ){
                String range=matcherRange.group();
                String[] nums=new String[2];
                Matcher numbers=patternNumber.matcher(range);
                int j=0;
                while(numbers.find()){
                   nums[j]=numbers.group(); 
                   j++;
                }
                tokens.add(new Range(nums[0],nums[1] ));

                i=matcherRange.end()-1;
            }
            else if (matcherSimpleInitializer.find(i) && matcherSimpleInitializer.start()==i){
                tokens.add(new Token("SIMPLE", matcherSimpleInitializer.group()));
                i=matcherSimpleInitializer.end()-1;
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

        }

        return this.tokens;
    }
}
