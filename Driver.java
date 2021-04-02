import java.util.Scanner;

/**
 * @author jesse russell
 */
public class Driver {
    static AvlTree<KeyValuePair<Integer, String>> table = new AvlTree<>();
    
    static void    put(Integer key, String value) { table.put(new KeyValuePair<>(key, value)); }
    static String  get(Integer key)      { return table.get(new KeyValuePair<>(key)).value; }
    static boolean contains(Integer key) { return table.contains(new KeyValuePair<>(key)); }
    
    static class Result<T>{boolean pass; T value;}
    static Result<Integer> tryParseInt(String s){
        Result<Integer> result = new Result<>();
        
        try{
            result.value = Integer.parseInt(s);
            result.pass = true;
        }
        catch(NumberFormatException e){
            result.pass = false;
            result.value = 0;
        }
        
        return result;
    }
    
    public static void main(String[] args){
        System.out.println(
            "java MyBST\n" +
            "Welcome to my MyBST!\n" +
            "Here are the commands you can use:\n" +
            "(p)ut key value\n" +
            "(g)et key\n" +
            "(f)loor key\n" +
            "(c)eiling key\n" +
            "(s)how\n" +
            "(d)elete\n" +
            "e(x)it");
        
        Scanner input = new Scanner(System.in);
                
        String com;
        String[] com_args;
        boolean run = true;
        while(run){
            System.out.print("> ");
            System.out.flush();

            com = input.nextLine();
            if (com.isBlank()) continue;

            com_args = com.split(" ");

            if (com_args.length == 0) continue;

            switch(com_args[0]){
                case "p":
                    if (com_args.length >= 3){
                        var k = tryParseInt(com_args[1]);
                        if (!k.pass){
                            System.out.println("key must be int");
                            break;
                        }

                        put(k.value, com_args[2]);
                    }
                    else
                        System.out.println("too few arguments");
                    break;

                case "g":
                    if (com_args.length >= 2){
                        var k = tryParseInt(com_args[1]);
                        if (!k.pass){
                            System.out.println("key must be int");
                            break;
                        }

                        if (contains(k.value))
                            System.out.println(get(k.value));
                        else
                            System.out.println("not found");
                    }
                    else System.out.println("too few arguments");
                    break;

                case "f":
                    if (com_args.length >= 2){
                        var k = tryParseInt(com_args[1]);
                        if (!k.pass){
                            System.out.println("key must be int");
                            break;
                        }

                        var flr = table.floor(new KeyValuePair(k.value));
                        if (flr == null) System.out.println("not found");
                        else System.out.println(flr.value);
                    }
                    else System.out.println("too few arguments");
                    break;

                case "c":
                    if (com_args.length >= 2){
                        var k = tryParseInt(com_args[1]);
                        if (!k.pass){
                            System.out.println("key must be int");
                            break;
                        }

                        var clg = table.ceiling(new KeyValuePair(k.value));
                        if (clg == null) System.out.println("not found");
                        else System.out.println(clg.value);
                    }
                    else System.out.println("too few arguments");
                    break;

                case "s":
                    for(var p : table)
                        System.out.printf("%-7s %s\n", p.key, p.value);
                    break;

                case "d":
                    if (com_args.length >= 2){
                        var k = tryParseInt(com_args[1]);
                        if (!k.pass){
                            System.out.println("key must be int");
                            break;
                        }

                        if (contains(k.value))
                            table.delete(new KeyValuePair<>(k.value));
                        else
                            System.out.println("not found");
                    }
                    else System.out.println("too few arguments");
                    break;

                case "x":
                    System.out.println("Bye!");
                    run = false;
                    break;

                default:
                    System.out.println("what?");
                    break;
            }
        }
        
        input.close();
    }
}
