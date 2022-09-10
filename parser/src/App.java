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
        for (Token t: scanner.scan("-123ui,-1,[123...  234 ]{[3], [45] } [123...  234 ],52")){
            System.out.println(t);
        };
        

    }
}

class parser{
    ArrayList<Token> tokens;
    int index;
    int lookahead;
    public parser(ArrayList<Token> tokens){
        this.tokens=tokens;
        this.index=0;

    }

    int nextToken(){
        return 0;
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

// class Range extends Token{
//     public Range(String lexeme){
//         super("RANGE", lexeme);
//     }
// }

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
                tokens.add(new Token("RANGE", matcherRange.group()));
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

        }

        return this.tokens;
    }
}
