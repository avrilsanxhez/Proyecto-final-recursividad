package proyectorecursividad;

public class ProyectoRecursividad {
    public static int resolver(String expresion) {
        if (expresion == null || expresion.isEmpty()) {
            throw new IllegalArgumentException("Expresión vacía o nula");
        }
        return evaluar(expresion);
    }

    private static int evaluar(String expr) {
        expr = expr.replaceAll("\\s+", ""); //eliminar espacios

        if (esNumero(expr)) {    //caso base, si es número puro se devuelve directamente
            return Integer.parseInt(expr);
        }
        
        int nivel = 0;     //buscar el operador de menor precedencia fuera del parentesis
        int posSuma = -1, posResta = -1, posMultiplicacion = -1;

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (c == '(') {
                nivel++;
            } else if (c == ')') {
                nivel--;
                if (nivel < 0) {
                    throw new IllegalArgumentException("Formato inválido: paréntesis desbalanceados");
                }
            } else if (nivel == 0) {
                switch (c) {
                    case '+' -> posSuma = i;
                    case '-' -> {
                        if (i > 0 && esOperador(expr.charAt(i - 1))) {   //para evitar q haya un signo negativo antes o despues de otro operador
                            throw new IllegalArgumentException("Formato inválido: '-' tras operador");
                        }   posResta = i;
                    }
                    case '*' -> posMultiplicacion = i;
                    default -> {
                    }
                }
            }
        }

        if (nivel != 0) {
            throw new IllegalArgumentException("Formato inválido: paréntesis desbalanceados");
        }

        //en caso de que toda la expresión esté entre paréntesis, quitarlos y reevaluar
        if (expr.startsWith("(") && expr.endsWith(")")) {
            return evaluar(expr.substring(1, expr.length() - 1));
        }

        //jerarquía de operaciones y así
        int pos;
        if (posSuma != -1) {
            pos = posSuma;
        } else if (posResta != -1) {
            pos = posResta;
        } else if (posMultiplicacion != -1) {
            pos = posMultiplicacion;
        } else {
            throw new IllegalArgumentException("Expresión inválida");
        }

        char op = expr.charAt(pos);
        String izq = expr.substring(0, pos);
        String der = expr.substring(pos + 1);

        //para validar que no haya operadores consecutivos en los extremos
        if (izq.isEmpty() || der.isEmpty() ||
            esOperador(izq.charAt(izq.length() - 1)) ||
            esOperador(der.charAt(0))) {
            throw new IllegalArgumentException("Operadores consecutivos inválidos");
        }

        //llamada recursiva en ambas partes
        int a = evaluar(izq);
        int b = evaluar(der);

        switch (op) {
            case '+' -> {
                return a + b;
            }
            case '-' -> {
                return a - b;
            }
            case '*' -> {
                return a * b;
            }
            default -> throw new IllegalArgumentException("Operador desconocido :o");
        }
    }

    private static boolean esNumero(String s) {
        return s.matches("-?\\d+");
    }

    private static boolean esOperador(char c) {
        return c == '+' || c == '-' || c == '*';
    }

    public static void main(String[] args) {
        
        System.out.println(resolver("7+10+2"));
        System.out.println(resolver("1+5*(7-1)+10"));
        System.out.println(resolver("5+7+(1+2*(8-9)+2)-10"));
        System.out.println(resolver("9-2*(75-9+(6+3+(1+2)*1)+7)+9"));
        
        try {
            resolver("5++1+10");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
