package interfacecompilador;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Semantico implements Constants {

    // ------------------------------------------------------------------
    // Registros semânticos 
    // ------------------------------------------------------------------

    // Código objeto (IL) gerado
    private final StringBuilder codigoObjeto = new StringBuilder();

    // Tipos das expressões durante sua avaliação
    private final Stack<String> pilhaTipos = new Stack<>();

    // Rótulos usados nos comandos de seleção e repetição
    private final Stack<String> pilhaRotulos = new Stack<>();

    // Operador relacional reconhecido em #121, usado em #122
    private String operadorRelacional = "";

    // Identificador reconhecido em #103, usado em #104, #105, #106, #107, #108
    private String identificador = "";

    private final Map<String, Integer> tabelaSimbolos = new LinkedHashMap<>();

    private final Set<String> identificadoresUtilizados = new LinkedHashSet<>();

    private int contadorRotulos = 0;

    public void executeAction(int action, Token token) throws SemanticError {
        switch (action) {
            case 101: acao101(); break;
            case 102: acao102(token); break;
            case 103: acao103(token); break;
            case 104: acao104(); break;
            case 105: acao105(token); break;
            case 106: acao106(); break;
            case 107: acao107(); break;
            case 108: acao108(); break;
            case 109: acao109(); break;
            case 110: acao110(token); break;
            case 111: acao111(); break;
            case 112: acao112(); break;
            case 113: acao113(); break;
            case 114: acao114(token); break;
            case 115: acao115(); break;
            case 116: acao116(token); break;
            case 117: acao117(); break;
            case 118: acao118(); break;
            case 119: acao119(); break;
            case 120: acao120(); break;
            case 121: acao121(token); break;
            case 122: acao122(); break;
            case 123: acao123(); break;
            case 124: acao124(); break;
            case 125: acao125(); break;
            case 126: acao126(); break;
            case 127: acao127(token); break;
            case 128: acao128(token); break;
            case 129: acao129(token); break;
            case 130: acao130(token); break;
            case 131: acao131(); break;
            case 132: acao132(); break;
            case 133: acao133(); break;
            case 134: acao134(); break;
            default: throw new SemanticError("ação semântica não implementada: #" + action);
        }
    }

    public String getCodigoObjeto() {
        return codigoObjeto.toString();
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------

    private void emitir(String instrucao) {
        codigoObjeto.append(instrucao).append('\n');
    }

    private String novoRotulo() {
        return "L" + (contadorRotulos++);
    }

    // Tipo definido pelo prefixo do identificador
    private static String tipoDoIdentificador(String id) {
        if (id.startsWith("i_")) return "int64";
        if (id.startsWith("f_")) return "float64";
        if (id.startsWith("s_")) return "string";
        return "bool"; // b_
    }

    private static String classeDoTipo(String tipo) {
        switch (tipo) {
            case "int64":   return "[mscorlib]System.Int64";
            case "float64": return "[mscorlib]System.Double";
            case "string":  return "[mscorlib]System.String";
            default:        return "[mscorlib]System.Boolean";
        }
    }

    private static String sufixoElemento(String tipo) {
        switch (tipo) {
            case "int64":   return "i8";
            case "float64": return "r8";
            case "string":  return "ref";
            default:        return "i1";
        }
    }

    private boolean ehArray(String id) {
        Integer tamanho = tabelaSimbolos.get(id);
        return tamanho != null && tamanho > 0;
    }

    // ------------------------------------------------------------------
    // Programa
    // ------------------------------------------------------------------

    // #101: cabeçalho do programa objeto
    private void acao101() {
      codigoObjeto.append(".assembly extern mscorlib {}\n");
      codigoObjeto.append(".assembly _codigo_objeto{}\n");
      codigoObjeto.append(".module _codigo_objeto.exe\n");
      codigoObjeto.append("\n");
      codigoObjeto.append(".class public _codigo_objeto{\n");
      codigoObjeto.append(".method static public void _principal(){\n");
      codigoObjeto.append(".entrypoint\n");
    }

    // #102: verificação semântica 01 + finalização do programa objeto
    private void acao102(Token token) throws SemanticError {
        for (String id : tabelaSimbolos.keySet()) {
            if (!identificadoresUtilizados.contains(id)) {
                throw new SemanticError(id + " identificador declarado, não utilizado", token.getPosition());
            }
        }
      codigoObjeto.append("ret\n");
      codigoObjeto.append("}\n");
      codigoObjeto.append("}");
    }

    // ------------------------------------------------------------------
    // Declaração de variáveis
    // ------------------------------------------------------------------

    // #103: guarda o identificador reconhecido
    private void acao103(Token token) {
        identificador = token.getLexeme();
    }

    // #104: declaração de variável simples
    private void acao104() {
        tabelaSimbolos.put(identificador, 0);
        emitir(".locals (" + tipoDoIdentificador(identificador) + " " + identificador + ")");
    }

    // #105: declaração de array com tamanho cte_int
    private void acao105(Token token) {
        int tamanho = Integer.parseInt(token.getLexeme());
        tabelaSimbolos.put(identificador, tamanho);

        String tipo = tipoDoIdentificador(identificador);
        emitir(".locals (" + tipo + "[] " + identificador + ")");
        emitir("ldc.i4 " + tamanho);
        emitir("newarr " + classeDoTipo(tipo));
        emitir("stloc " + identificador);
    }

    // ------------------------------------------------------------------
    // Atribuição e entrada
    // ------------------------------------------------------------------

    // #106: armazena o valor da expressão no identificador (ou posição do array)
    private void acao106() {
        String tipoExpressao = pilhaTipos.pop();
        if ("int64".equals(tipoExpressao)) {
            emitir("conv.i8");
        }

        identificadoresUtilizados.add(identificador);

        if (ehArray(identificador)) {
            // pilha IL: array, índice, valor
            emitir("stelem." + sufixoElemento(tipoDoIdentificador(identificador)));
        } else {
            emitir("stloc " + identificador);
        }
    }

    // #107: uso de array em atribuição/entrada
    private void acao107() {
        emitir("ldloc " + identificador);
    }

    // #134: índice do array em atribuição/entrada
    private void acao134() {
        pilhaTipos.pop();
        emitir("conv.i4");
    }

    // #108: lê um valor da entrada padrão e armazena no identificador
    private void acao108() {
        String tipo = tipoDoIdentificador(identificador);

        emitir("call string [mscorlib]System.Console::ReadLine()");
        switch (tipo) {
            case "int64":
                emitir("call int64 [mscorlib]System.Int64::Parse(string)");
                break;
            case "float64":
                emitir("call float64 [mscorlib]System.Double::Parse(string)");
                break;
            case "bool":
                emitir("call bool [mscorlib]System.Boolean::Parse(string)");
                break;
            default:
                break;
        }

        identificadoresUtilizados.add(identificador);

        if (ehArray(identificador)) {
            emitir("stelem." + sufixoElemento(tipo));
        } else {
            emitir("stloc " + identificador);
        }
    }

    // ------------------------------------------------------------------
    // Saída
    // ------------------------------------------------------------------

    // #109: escreve o valor da expressão
    private void acao109() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         codigoObjeto.append("conv.i8\n");
      }
      codigoObjeto.append("call void [mscorlib]System.Console::Write(" + tipo + ")\n");
    }

    // ------------------------------------------------------------------
    // Seleção
    // ------------------------------------------------------------------

    private void acao110(Token token) {
        pilhaTipos.pop();
        String rotulo = novoRotulo();
        if (token.getId() == t_istruedo) {
            emitir("brfalse " + rotulo);
        } else {
            emitir("brtrue " + rotulo);
        }
        pilhaRotulos.push(rotulo);
    }

    // #111: fim do comando de seleção
    private void acao111() {
        emitir(pilhaRotulos.pop() + ":");
    }

    private void acao112() {
        String rotuloFim = novoRotulo();
        String rotuloAlternativo = pilhaRotulos.pop();
        emitir("br " + rotuloFim);
        emitir(rotuloAlternativo + ":");
        pilhaRotulos.push(rotuloFim);
    }

    // ------------------------------------------------------------------
    // Repetição
    // ------------------------------------------------------------------

    private void acao113() {
        String rotuloInicio = novoRotulo();
        emitir(rotuloInicio + ":");
        pilhaRotulos.push(rotuloInicio);
    }

    // #114: desvio para fora do laço
    private void acao114(Token token) {
        pilhaTipos.pop();
        String rotuloFim = novoRotulo();
        if (token.getId() == t_istruedo) {
            emitir("brfalse " + rotuloFim);
        } else {
            emitir("brtrue " + rotuloFim);
        }
        pilhaRotulos.push(rotuloFim);
    }

    // #115: volta para a avaliação da expressão e marca o fim do laço
    private void acao115() {
        String rotuloFim = pilhaRotulos.pop();
        String rotuloInicio = pilhaRotulos.pop();
        emitir("br " + rotuloInicio);
        emitir(rotuloFim + ":");
    }

    // ------------------------------------------------------------------
    // Expressões lógicas
    // ------------------------------------------------------------------

    // #116: and
    private void acao116(Token token) throws SemanticError {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();
        if (!"bool".equals(tipo1) || !"bool".equals(tipo2)) {
            throw new SemanticError("tipos incompatíveis em operador lógico and", token.getPosition());
        }
        pilhaTipos.push("bool");
        emitir("and");
    }

    // #117: or
    private void acao117() {
        pilhaTipos.pop();
        pilhaTipos.pop();
        pilhaTipos.push("bool");
        emitir("or");
    }

    // #118: true
    private void acao118() {
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.1\n");
    }

    // #119: false
    private void acao119() {
        pilhaTipos.push("bool");
        emitir("ldc.i4.0");
    }

    // #120: not
    private void acao120() {
      String tipo = pilhaTipos.pop();
      pilhaTipos.push("bool");
      codigoObjeto.append("ldc.i4.1\n");
      codigoObjeto.append("xor\n");
    }

    // ------------------------------------------------------------------
    // Expressões relacionais
    // ------------------------------------------------------------------

    // #121: guarda o operador relacional
    private void acao121(Token token) {
        operadorRelacional = token.getLexeme();
    }

    // #122: comparação
    private void acao122() {
        String tipo2 = pilhaTipos.pop();
        String tipo1 = pilhaTipos.pop();
        pilhaTipos.push("bool");

        if ("string".equals(tipo1) && "string".equals(tipo2)) {
            emitir("call int32 [mscorlib]System.String::Compare(string, string)");
            emitir("ldc.i4.0");
        }

        switch (operadorRelacional) {
            case "=":
                emitir("ceq");
                break;
            case "<>":
                emitir("ceq");
                emitir("ldc.i4.0");
                emitir("ceq");
                break;
            case "<":
                emitir("clt");
                break;
            case ">":
                emitir("cgt");
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------
    // Expressões aritméticas
    // ------------------------------------------------------------------

    // #123: +
    private void acao123() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("add\n");
    }

    // #124: -
    private void acao124() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("sub\n");
    }

    // #125: *
    private void acao125() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      if ("int64".equals(tipo1) && "int64".equals(tipo2)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("mul\n");
    }

    // #126: /
    private void acao126() {
      String tipo1 = pilhaTipos.pop();
      String tipo2 = pilhaTipos.pop();
      pilhaTipos.push("float64");
      codigoObjeto.append("div\n");
    }

    // #127: identificador em expressão
    private void acao127(Token token) {
        String id = token.getLexeme();
        String tipo = tipoDoIdentificador(id);
        pilhaTipos.push(tipo);
        emitir("ldloc " + id);

        if (!ehArray(id) && "int64".equals(tipo)) {
            emitir("conv.r8");
        }
    }

    // #133: elemento de array em expressão
    private void acao133() {
        pilhaTipos.pop(); 
        String tipo = pilhaTipos.peek();
        emitir("conv.i4");
        emitir("ldelem." + sufixoElemento(tipo));
        if ("int64".equals(tipo)) {
            emitir("conv.r8");
        }
    }

    // #128: constante inteira
    private void acao128(Token token) {
     pilhaTipos.push("int64");
     codigoObjeto.append("ldc.i8 " + token.getLexeme() + "\n");
     codigoObjeto.append("conv.r8\n");
    }

    // #129: constante real
    private void acao129(Token token) {
        pilhaTipos.push("float64");
        emitir("ldc.r8 " + token.getLexeme().replace(',', '.'));
    }

    // #130: constante string
    private void acao130(Token token) {
        pilhaTipos.push("string");
        emitir("ldstr " + token.getLexeme());
    }

    // #131: + unário 
    private void acao131() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
    }

    // #132: - unário
    private void acao132() {
      String tipo = pilhaTipos.pop();
      if ("int64".equals(tipo)) {
         pilhaTipos.push("int64");
      } else {
         pilhaTipos.push("float64");
      }
      codigoObjeto.append("ldc.i8 -1\n");
      codigoObjeto.append("conv.r8\n");
      codigoObjeto.append("mul\n");
    }
}
